plugins {
    val kotlinVersion = "1.6.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.11.0-M2"
}

group = "icu.dnddl.plugin"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}

fun skikoAwt(ver: String) = "org.jetbrains.skiko:skiko-awt-runtime-$ver:0.7.16"
fun exposed(module: String) = "org.jetbrains.exposed:exposed-$module:0.37.3"

dependencies {
    implementation(exposed("core"))
    implementation(exposed("dao"))
    implementation(exposed("jdbc"))
    implementation(exposed("java-time"))
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("com.alibaba:druid:1.2.8")
    implementation(skikoAwt("windows-x64"))
    implementation(skikoAwt("linux-x64"))
    implementation(skikoAwt("linux-arm64"))
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
    compileOnly("com.github.LaoLittle:SkikoMirai:1.0.4")
    testImplementation(exposed("core"))
    testImplementation(exposed("dao"))
    testImplementation(exposed("jdbc"))
    testImplementation(exposed("java-time"))
    testImplementation("org.xerial:sqlite-jdbc:3.36.0.3")
    testImplementation("com.alibaba:druid:1.2.8")
    testImplementation(kotlin("test", "1.6.20-M1"))
    testImplementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    testImplementation(skikoAwt("windows-x64"))
}