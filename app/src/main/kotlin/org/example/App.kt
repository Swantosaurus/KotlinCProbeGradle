package org.example

class App {
  val greeting: String
    get() {
      return "Hello World!"
    }
}

fun main() {
  // Load the native library at runtime.
  // The string "nativelib" should match the base name of the compiled library file
  // (e.g., libnativelib.so, nativelib.dll, libnativelib.dylib).
  try {
    System.loadLibrary("world")
  } catch (e: UnsatisfiedLinkError) {
    println("Failed to load native library: ${e.message}")
    println(
            "Make sure the compiled native library is in the system's library path or in the application distribution."
    )
    return
  }

  // Print "Hello " from Kotlin
  print("Hello ") // Use print to avoid newline

  // Call the native method implemented in C
  WorldLib.printWorldFromC()
}
