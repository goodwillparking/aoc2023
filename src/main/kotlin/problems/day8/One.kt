package problems.day8

import util.getInput

private object One {

    private val dataRegex = Regex("^(\\w{3}) = \\((\\w{3}), (\\w{3})\\)$")

    @JvmStatic
    fun main(args: Array<String>) {
        println("part1: ${part1()}")
        println("part2: ${part2()}")
    }

    private fun part1(): Long {
        val nav = getInput("day8/one")
            .let(::parseNetwork)

        val start = nav.network.getValue("AAA")

        data class Acc(val nodeName: String, val node: Node, val steps: Long)

        return nav.instructions
            .scan(Acc("AAA", start, 0)) { (_, node, steps), instruction ->
                val nextNodeName = when (instruction) {
                    Direction.LEFT -> node.left
                    Direction.RIGHT -> node.right
                }
                Acc(nextNodeName, nav.network.getValue(nextNodeName), steps + 1)
            }.first { it.nodeName == "ZZZ" }
                .steps
    }

    private fun part2(): Long {
        return getInput("day8/one")
            .let(::TODO)
    }

    fun parseNetwork(input: String): Nav {
        val baseInstruction = input.lineSequence().first()
        val instructions = generateSequence { baseInstruction }
            .flatMap { it.toCharArray().asSequence() }
            .map {
                when (it) {
                    'L' -> Direction.LEFT
                    'R' -> Direction.RIGHT
                    else -> throw IllegalArgumentException("Invalid direction: $it")
                }
            }

        val network = input.lineSequence()
            .drop(1)
            .filter(String::isNotBlank)
            .map { line ->
                val (_, root, left, right) = dataRegex.find(line)!!.groups.map { it!!.value }
                root to Node(left, right)
            }.toMap()
            .let(::Network)

        return Nav(instructions, network)
    }

    data class Nav(val instructions: Instructions, val network: Network)

    data class Network(val map: Map<String, Node>) : Map<String, Node> by map

    data class Node(val left: String, val right: String)

    enum class Direction {
        LEFT,
        RIGHT
    }
}

private typealias Instructions = Sequence<One.Direction>
