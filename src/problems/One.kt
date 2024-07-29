package problems

import util.getInput

object One {
    @JvmStatic
    fun main(args: Array<String>) {
        val input = getInput("one")

        val sum = input.lineSequence()
            .map { line ->
                val first = findDigit(line::find)
                val last = findDigit(line::findLast)
                (first + last).toInt()
            }.sum()

        println(sum)
    }

    private fun findDigit(find: ((Char) -> Boolean) -> Char?): String {
        return find { it.isDigit() }?.toString() ?: "0"
    }
}
