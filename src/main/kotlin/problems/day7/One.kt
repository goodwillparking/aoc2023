package problems.day7

import util.getInput
import util.shrink
import util.size
import util.split

private object One {

    private val roundRegex = Regex("^([\\dAKQJT]+) (\\d+)$")

    private val handComparator = Comparator { h1: Hand, h2: Hand ->
        h1.type.ordinal - h2.type.ordinal
    }.thenComparing { h1, h2 ->
        h1.asSequence()
            .zip(h2.asSequence())
            .mapNotNull { (c1, c2) ->
                (c1.ordinal - c2.ordinal).takeIf { it != 0 }
            }.firstOrNull() ?: 0
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println("part1: ${part1()}")
        println("part2: ${part2()}")
    }

    private fun part1(): Long = getInput("day7/one")
        .lineSequence()
        .filter(String::isNotBlank)
        .map(::parseRound)
        .sortedWith(Comparator.comparing(Round::hand, handComparator))
        .withIndex()
        .map {
            val rank = it.index + 1
            rank * it.value.bid.toLong()
        }.sum()

    private fun part2(): Long {
        return getInput("day7/one")
            .let { TODO() }
    }

    fun parseRound(raw: String): Round {
        val groups = roundRegex.find(raw)!!.groups
        return Round(
            hand = groups[1]!!.value.toCharArray()
                .map(::parseCard)
                .let(::Hand),
            bid = groups[2]!!.value.toInt()
        )
    }

    fun parseCard(char: Char): Card = when(char) {
        '2' -> Card.TWO
        '3' -> Card.THREE
        '4' -> Card.FOUR
        '5' -> Card.FIVE
        '6' -> Card.SIX
        '7' -> Card.SEVEN
        '8' -> Card.EIGHT
        '9' -> Card.NINE
        'T' -> Card.TEN
        'J' -> Card.JACK
        'Q' -> Card.QUEEN
        'K' -> Card.KING
        'A' -> Card.ACE
        else -> throw IllegalArgumentException("Invalid card $char")
    }

    fun determineType(hand: Hand): HandType {
        val groups = hand.groupBy { it }
        return when (groups.size) {
            1 -> HandType.FIVE_OF_A_KIND
            2 -> when (groups.values.first().size) {
                1, 4 -> HandType.FOUR_OF_A_KIND
                else -> HandType.FULL_HOUSE
            }
            3 -> if (groups.values.any { it.size == 2 }) HandType.TWO_PAIR else HandType.THREE_OF_A_KIND
            4 -> HandType.PAIR
            else -> HandType.HIGH_CARD
        }
    }

    data class Round(val hand: Hand, val bid: Int)

    data class Hand(val cards: List<Card>) : List<Card> by cards {
        init {
            require(cards.size == 5)
        }

        val type by lazy {
            determineType(this)
        }
    }

    enum class HandType {
        HIGH_CARD,
        PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND
    }

    enum class Card {
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING,
        ACE
    }
}


