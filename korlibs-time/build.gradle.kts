import korlibs.*

description = "Korlibs Time - Former Klock"

project.extensions.extraProperties.properties.apply {
    includeKotlinNativeDesktop()

    applyProjectProperties(
        "https://raw.githubusercontent.com/korlibs/korge/main/korlibs-time",
        "Public Domain",
        "https://raw.githubusercontent.com/korlibs/korge/main/korlibs-time/LICENSE"
    )
}
