
#include <jni.h>  // Required for JNI types and macros
#include <stdio.h> // Required for printf

// The JNI function implementation.
// JNIEXPORT and JNICALL are macros for cross-platform compatibility.
// Java_ is the prefix.
// NativeLib is the class name.
// printWorldFromC is the method name.
// JNIEnv* env: Pointer to the JNI environment (allows calling JVM functions from C).
// jclass clazz: Reference to the class containing the static method (for static methods).
JNIEXPORT void JNICALL Java_org_example_WorldLib_printWorldFromC 
  (JNIEnv *env, jclass clazz)
{
    // Print "World" followed by a newline from C
    printf("World\n");
}

