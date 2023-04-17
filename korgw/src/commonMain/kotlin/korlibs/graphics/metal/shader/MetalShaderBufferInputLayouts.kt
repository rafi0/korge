package korlibs.graphics.metal.shader

import korlibs.graphics.shader.Attribute
import korlibs.graphics.shader.Uniform
import korlibs.graphics.shader.VariableWithOffset
import korlibs.graphics.shader.VertexLayout
import korlibs.logger.Logger

internal fun lazyMetalShaderBufferInputLayouts(
    vertexLayouts: List<VertexLayout>,
    uniforms: List<Uniform>
) = lazy {
    MetalShaderBufferInputLayouts(
        vertexLayouts,
        uniforms,
        (vertexLayouts.map { it.items } + uniforms.map { listOf(it) })
            .toList()
    )
}

internal class MetalShaderBufferInputLayouts(
    val vertexLayouts: List<VertexLayout>,
    val uniforms: List<Uniform>,
    val inputBuffers: List<List<VariableWithOffset>>
) {

    private val logger = Logger("MetalShaderBufferInputLayouts")

    fun attributeIndexOf(attribute: Attribute): Int? {
        return bufferIndexOf(attribute)
            // take is not zero indexed, so we add 1
            ?.let { inputBuffers.take(it + 1) }
            ?.fold(0) { acc, list ->
                when (attribute) {
                    in list -> acc + list.indexOf(attribute)
                    else -> acc + list.size
                }
            }
    }

    private fun bufferIndexOf(attribute: Attribute): Int? {
        return inputBuffers.indexOfFirst { attribute in it }
            .let { if (it == -1) null else it }
    }

    internal val vertexInputStructure by lazy {
        vertexLayouts
            .mapIndexed { index, attributes -> index to attributes }
            .filter { (_, attributes) -> attributes.items.size >= 2 }
    }

    internal fun computeFunctionParameter(
        parameters: List<VariableWithOffset>,
        bodyGenerator: MetalShaderBodyGenerator
    ): Lazy<List<String>> = lazy {
        inputBuffers.filterNotIn(parameters)
            .map { it.findDeclarationFromInputBuffer(bodyGenerator) }

    }

    private fun List<List<VariableWithOffset>>.filterNotIn(parameters: List<VariableWithOffset>): List<List<VariableWithOffset>> {
        return filter { buffer -> parameters.any { parameter -> parameter in buffer } }
    }

    private fun List<VariableWithOffset>.findDeclarationFromInputBuffer(bodyGenerator: MetalShaderBodyGenerator): String {
        val bufferIndex = inputBuffers.indexOf(this)
        return when {
            isEmpty() -> {
                logger.error { "buffer without layout" }
                ""
            }
            size > 1 -> "device const Buffer$bufferIndex* buffer$bufferIndex [[buffer($bufferIndex)]]"
            else -> {
                val variableWithOffset = first()
                val type = bodyGenerator.typeToString(variableWithOffset.type)
                val name = variableWithOffset.name
                when (variableWithOffset) {
                    is Attribute -> "const device $type& $name [[attribute(${attributeIndexOf(variableWithOffset)})]]"
                    else -> "constant $type& $name [[buffer($bufferIndex)]]"
                }
            }
        }
    }

    private fun List<VariableWithOffset>.toLocalDeclarations(): List<String> {
        val bufferIndex = inputBuffers.indexOf(this)

        return map {
            when {
                size > 1 -> "auto ${it.name} = buffer$bufferIndex[vertexId].${it.name};"
                else -> "auto ${it.name} = buffer$bufferIndex[vertexId];"
            }

        }
    }



}


