package util

fun getInput(path: String): String =
    object {}.javaClass.getResource("/input/$path.txt")?.readText()!!

fun LongRange.intersect(other: LongRange): LongRange {
    val start = if (first in other) {
        first
    } else if (other.first in this) {
        other.first
    } else null

    return if (start != null) {
        val end = if (last in other) {
            last
        } else other.last
        start..end
    } else LongRange.EMPTY
}
