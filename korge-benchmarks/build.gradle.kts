plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.benchmark") version libs.versions.kotlinx.benchmark
    id("org.jetbrains.kotlin.plugin.allopen") version libs.versions.kotlin
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("jvm")
    }
}

dependencies {
    add("commonMainApi", project(":korim"))
    add("commonMainApi", project(":korge"))
    add("commonMainApi", "org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.4")
}
