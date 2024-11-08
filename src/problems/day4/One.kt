package problems.day4

import util.getInput
import kotlin.math.pow

private object One {

    val regex = Regex("^Card +(\\d+): ([\\d ]+) \\| ([\\d ]+)$")

    @JvmStatic
    fun main(args: Array<String>) {
        println("part1: ${part1()}")
        println("part2: ${part2()}")
    }

    private fun part1(): Int = getInput("day4/one")
        .lineSequence()
        .filter { it.isNotBlank() }
        .map(::parseCard)
        .map(Card::points)
        .sum()

    private fun part2(): Int {
         val cards = getInput("day4/one")
             .lineSequence()
             .filter { it.isNotBlank() }
             .map(::parseCard)
             .toList()

        return cardCount(cards, cards, 0)
    }

    fun cardCount(cards: List<Card>, allCards: List<Card>, offset: Int): Int {
        return cards.asSequence().mapIndexed { i, card ->
            val wins = card.wins
            val copyOffset = offset + i + 1
            val copies = allCards.subList(copyOffset, copyOffset + wins)
            cardCount(copies, allCards, copyOffset) + 1
        }.sum()
    }

    fun parseCard(line: String): Card {
        val result = regex.find(line)!!
        val id = result.groups[1]!!.value.toInt()
        val winning = result.groups[2]!!.value
        val actual = result.groups[3]!!.value

        fun parseNums(s: String) = s.split(" ")
            .filter { it.isNotBlank() }
            .map { it.toInt() }
            .toSet()

        return Card(id = id, winningValues = parseNums(winning), values = parseNums(actual))
    }

    data class Card(val id: Int, val winningValues: Set<Int>, val values: Set<Int>) {
        val points by lazy {
            when (val matches = winningValues.intersect(values).size) {
                0 -> 0
                else -> 2.0.pow(matches - 1).toInt()
            }
        }

        val wins by lazy { winningValues.intersect(values).size }
    }
}
