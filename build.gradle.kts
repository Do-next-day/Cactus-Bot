plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "org.laolittle.plugin"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

fun skikoAwt(ver: String) = "org.jetbrains.skiko:skiko-awt-runtime-$ver"

dependencies {
    val exposedVersion = "0.37.3"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("com.alibaba:druid:1.2.8")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    val skikoVer = "0.7.12"

    implementation(skikoAwt("windows-x64:$skikoVer"))
    implementation(skikoAwt("linux-x64:$skikoVer"))
    implementation(skikoAwt("linux-arm64:$skikoVer"))
}