import korlibs.samples.clientserver.mySharedString
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.math.geom.SizeInt

suspend fun main() = Korge(windowSize = SizeInt(512, 512), backgroundColor = Colors["#2b2b2b"]).start {
    sceneContainer().changeTo({ MyMainScene() })
}

class MyMainScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        text("Text: $mySharedString")
    }
}