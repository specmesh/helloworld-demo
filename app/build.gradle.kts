plugins {
    java
    id("com.google.protobuf") version "0.9.3"
}

val specMeshVersion : String by extra

dependencies {
    implementation(project(":api"))

    testImplementation("io.specmesh:specmesh-parser:$specMeshVersion")
    testImplementation("io.specmesh:specmesh-kafka:$specMeshVersion")
    testImplementation("io.specmesh:specmesh-kafka-test:$specMeshVersion")
}
