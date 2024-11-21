package problems.day6

import util.getInput
import util.shrink
import util.size
import util.split

private object One {

    private val dataRegex = Regex("^\\w*:([\\d\\s]*)\$")

    @JvmStatic
    fun main(args: Array<String>) {
        println("part1: ${part1()}")
        println("part2: ${part2()}")
    }

    private fun part1(): Int {
        return getInput("day6/one")
            .let(::parseRaces)
            .map { it.waysToWin() }
            .reduce(Int::times)
    }

    private fun part2(): Int {
         return TODO()
    }

    private fun parseRaces(raw: String): Sequence<Race> {
        val (times, distances) = raw.lineSequence()
            .filter { it.isNotEmpty() }
            .map { dataRegex.find(it)!!.groups[1]!!.value }
            .map { data -> data.split(" ") }
            .map { datum -> datum.asSequence().filter { it.isNotBlank() }.map { it.toInt() } }
            .toList()
        return times.zip(distances)
            .map { (time, distance) -> Race(time = time, distance = distance)}
    }

    private fun Race.waysToWin(): Int = waysToWin(0..time, this)

    private fun waysToWin(range: IntRange, race: Race): Int = if (range.isEmpty()) {
        0
    } else {
        val startIsWin = isWin(range.first, race)
        val endIsWin = isWin(range.last, race)
        val (bottom, top) = range.shrink().split()
        if (startIsWin) {
            if (endIsWin) {
                range.size()
            } else {
                1 + waysToWin(bottom, race) + waysToWin(top, race)
            }
        } else {
            if (endIsWin) {
                1 + waysToWin(bottom, race) + waysToWin(top, race)
            } else {
                waysToWin(bottom, race) + waysToWin(top, race)
            }
        }
    }

    private fun isWin(chargeTime: Int, race: Race): Boolean {
        val remainingTime = race.time - chargeTime
        return chargeTime * remainingTime > race.distance
    }

    private data class Race(val time: Int, val distance: Int)
}


