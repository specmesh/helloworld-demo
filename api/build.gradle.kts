plugins {
    `java-library`
    id("com.google.protobuf") version "0.9.2"
}

val protobufVersion : String by extra

dependencies {
    api("com.google.protobuf:protobuf-java:$protobufVersion")
}
