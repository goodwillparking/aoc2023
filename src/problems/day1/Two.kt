package problems.day1

import util.getInput

object Two {

    private val textDigits = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    )

    private val reversedTextDigits = textDigits.mapKeys { (k, _) -> k.reversed() }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = getInput("day1/two")

        val sum = input.lineSequence()
            .map { line ->
                val first = findDigit(line, textDigits)
                val last = findDigit(line.reversed(), reversedTextDigits)
                (first + last).toInt()
            }.sum()

        println(sum)
    }

    private fun findDigit(s: String, textToValue: Map<String, Int>): String {
        val textMatch = textToValue.entries.fold(null as TextMatch?) { prev, (text, value) ->
            val i = s.indexOf(text)
            when {
                i < 0 || (prev != null && prev.index < i) -> prev
                i == 0 -> return value.toString()
                else -> TextMatch(index = i, value)
            }
        }

        val maxIndex = textMatch?.index ?: s.length
        val digitMatch = s.substring(0, maxIndex)
            .find { c -> c.isDigit() }

        return (digitMatch ?: textMatch?.value)?.toString() ?: "0"
    }

    private data class TextMatch(val index: Int, val value: Int)
}
