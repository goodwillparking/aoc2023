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

    private fun part1(): Long {
        return getInput("day6/one")
            .let(::parseRaces)
            .map { it.waysToWin() }
            .reduce(Long::times)
    }

    private fun part2(): Long {
        return getInput("day6/one")
            .let(::parseRaces)
            .reduce { acc, race ->
                Race(
                    time = (acc.time.toString() + race.time.toString()).toLong(),
                    distance = (acc.distance.toString() + race.distance.toString()).toLong()
                )
            }.waysToWin()
    }

    private fun parseRaces(raw: String): Sequence<Race> {
        val (times, distances) = raw.lineSequence()
            .filter { it.isNotEmpty() }
            .map { dataRegex.find(it)!!.groups[1]!!.value }
            .map { data -> data.split(" ") }
            .map { datum -> datum.asSequence().filter { it.isNotBlank() }.map { it.toLong() } }
            .toList()
        return times.zip(distances)
            .map { (time, distance) -> Race(time = time, distance = distance)}
    }

    private fun Race.waysToWin(): Long = waysToWin(0..time, this)

    private fun waysToWin(range: LongRange, race: Race): Long = if (range.isEmpty()) {
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

    private fun isWin(chargeTime: Long, race: Race): Boolean {
        val remainingTime = race.time - chargeTime
        return chargeTime * remainingTime > race.distance
    }

    private data class Race(val time: Long, val distance: Long)
}


