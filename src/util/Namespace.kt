package util

fun getInput(path: String): String =
    object {}.javaClass.getResource("/input/$path.txt")?.readText()!!
