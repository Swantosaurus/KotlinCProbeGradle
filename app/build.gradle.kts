import org.gradle.internal.os.OperatingSystem
import org.gradle.api.tasks.Exec
import org.gradle.api.file.DirectoryProperty
import java.io.File
import org.gradle.api.tasks.bundling.Zip 
import org.gradle.api.distribution.Distribution 

plugins {
    kotlin("jvm") version "1.9.22" // Use your desired Kotlin version
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
}

// --- Native Build Configuration - Top-level OS/Arch detection ---

val nativeLibName = "world"
val C_COMPILER = "gcc"

val os = OperatingSystem.current()
val javaHome = System.getProperty("java.home")

val jniIncludeDir = when {
    os.isLinux -> "linux"
    os.isMacOsX -> "darwin"
    os.isWindows -> "win32"
    else -> throw GradleException("Unsupported operating system: ${os.name}")
}
val libExtension = when {
    os.isLinux -> "so"
    os.isMacOsX -> "dylib"
    os.isWindows -> "dll"
    else -> throw GradleException("Unsupported operating system: ${os.name}")
}
val nativeLibFileName = when {
    os.isWindows -> "$nativeLibName.$libExtension"
    else -> "lib$nativeLibName.$libExtension"
}

val arch = System.getProperty("os.arch").lowercase()

val buildDirFile = layout.buildDirectory.get().asFile
val nativeLibsOsArchDir = buildDirFile.resolve("native_libs/$jniIncludeDir/$arch")
val nativeLibsBaseDir = buildDirFile.resolve("native_libs")

val cSourceDir = file("src/main/c").absolutePath
val cSourceFile = file("src/main/c/${nativeLibName}.c").absolutePath


// --- Application Plugin Configuration ---

application {
    mainClass.set("org.example.AppKt")

    distributions.named("main").configure {
        contents {
            from(nativeLibsBaseDir) {
                 into("lib")
            }
        }
    }
}
// --- End Application Plugin Configuration ---


// --- Native Build Tasks ---

val compileNative = tasks.register<Exec>("compileNative") {
    dependsOn(tasks.compileKotlin)

    val outputFile = nativeLibsOsArchDir.resolve(nativeLibFileName)

    outputFile.parentFile.mkdirs() 

    println("output libfile is at: $outputFile")
    commandLine(C_COMPILER) 
    args(
        "-shared",
        "-fPIC",
        "-I", "$javaHome/include",
        "-I", "$javaHome/include/$jniIncludeDir",
        "-I", cSourceDir, 
        cSourceFile,
        "-o", outputFile.absolutePath
    )
}

tasks.named("run").configure {
     dependsOn(compileNative)

     setProperty("jvmArgs", listOf("-Djava.library.path=${nativeLibsOsArchDir.absolutePath}"))
}
