package problems.day1

import util.getInput

private object One {
    @JvmStatic
    fun main(args: Array<String>) {
        val input = getInput("day1/one")

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
