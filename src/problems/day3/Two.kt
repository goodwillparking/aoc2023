package problems.day3

import util.getInput

private object Two {

    @JvmStatic
    fun main(args: Array<String>) {
        println("part2: ${part2()}")
    }

    private fun part2(): Int {
        val elements = getInput("day3/one")
            .lineSequence()
            .filter { it.isNotBlank() }
            .foldIndexed(mutableMapOf<Coordinate, Element>()) { y, elements, line ->
                val resolvedRow = line.foldIndexed(RowAccumulator(null, emptyMap())) { x, rowAcc, char ->
                    when {
                        char.isDigit() -> rowAcc.appendDigit(char, Coordinate(x, y))
                        char == '*' -> rowAcc.putGear(Coordinate(x, y)).resolvePartNum()
                        else -> rowAcc.resolvePartNum()
                    }
                }.resolvePartNum().elements
                elements.putAll(resolvedRow)
                elements
            }

        return elements.asSequence()
            .filter { it.value is Gear }
            .map {
                val adjacentParts = it.key.adjacent().mapNotNull { adj ->
                    elements[adj] as? PartNum
                }.toSet()
                if (adjacentParts.size == 2) {
                    adjacentParts
                        .asSequence()
                        .map(PartNum::value)
                        .fold(1, Int::times)
                        .also { println("found pair: ${adjacentParts.map { it.value }}") }
                } else 0
            }.sum()
    }


    sealed interface Element

    data class PartNum(val value: Int, val startCoordinate: Coordinate) : Element

    data object Gear : Element

    data class RowAccumulator(val partial: PartialPartNum?, val elements: Map<Coordinate, Element>) {
        fun appendDigit(digit: Char, coordinate: Coordinate) = if (partial != null) {
            copy(partial = partial.appendDigit(digit))
        } else {
            copy(partial = PartialPartNum(listOf(digit), coordinate))
        }

        fun putGear(coordinate: Coordinate) = copy(elements = elements + (coordinate to Gear))

        fun resolvePartNum() = if (partial != null) {
            val partNum = partial.resolve()
            val partMap = partNum.value.toString().mapIndexed { i, _ ->
                Coordinate(partNum.startCoordinate.x + i, partNum.startCoordinate.y) to partNum
            }.toMap()
            RowAccumulator(partial = null, elements = elements + partMap)
        } else {
            this
        }
    }

    data class PartialPartNum(val digits: List<Char>, val startCoordinate: Coordinate) {
        fun appendDigit(digit: Char) = copy(digits + digit)

        fun resolve() = PartNum(digits.joinToString("").toInt(), startCoordinate)
    }

    data class Coordinate(val x: Int, val y: Int) {
        fun adjacent() = singleDimAdjacent(x).flatMap { ax ->
            singleDimAdjacent(y).map { ay ->
                Coordinate(ax, ay)
            }
        }.toSet()
            .minus(this)

        private fun singleDimAdjacent(value: Int) = ((value - 1)..(value + 1))
    }
}
