package problems.day2

import util.getInput

private object One {

    val MAX_COUNTS = mapOf(
        Cube(Cube.Color.red) to 12,
        Cube(Cube.Color.green) to 13,
        Cube(Cube.Color.blue) to 14
    )

    private val GAME_REGEX = Regex("^Game (\\d+): (.*)$")
    private val CUBE_REGEX = Regex("^(\\d+) (.*)$")

    @JvmStatic
    fun main(args: Array<String>) {
        val sum = getInput("day2/one")
            .lineSequence()
            .filter(String::isNotBlank)
            .map(::parseGame)
            .filter(::isPossible)
            .map { it.id }
            .sum()

        println(sum)
    }

    fun isPossible(game: Game): Boolean =
        game.bag.all { round ->
            MAX_COUNTS.all { (cube, maxCount) ->
                val count = round[cube] ?: 0
                count <= maxCount
            }
        }

    fun parseGame(string: String): Game {
        val result = GAME_REGEX.find(string) ?: throw RuntimeException("Failed to parse game in '$string'")
        val gameId = result.groups[1]!!.value.toInt()
        val rounds = result.groups[2]!!.value
            .split("; ")
            .map(::parseRound)
        return Game(gameId, rounds)
    }

    fun parseRound(string: String): Round =
        string.split(", ")
            .map {
                val result = CUBE_REGEX.find(it)!!
                val count = result.groups[1]!!.value.toInt()
                val color = result.groups[2]!!.value.let(Cube.Color::valueOf)
                Cube(color) to count
            }
            .groupBy({ (cube, _) -> cube }) { (_, count) -> count }
            .mapValues { it.value.sum() }
}

private data class Game(val id: Int, val bag: Collection<Round>)

private data class Cube(val color: Color) {
    enum class Color {
        red,
        green,
        blue
    }
}

private typealias Round = Map<Cube, Int>
