package org.example

class WorldLib {
  companion object {
    @JvmStatic // Important for JNI to find static methods easily
    external fun printWorldFromC()
  }
}
