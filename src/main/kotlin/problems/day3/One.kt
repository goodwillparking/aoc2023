package problems.day3

import util.getInput

private object One {

    @JvmStatic
    fun main(args: Array<String>) {
        println("part1: ${part1()}")
    }

    private fun part1(): Int = getInput("day3/one")
        .lineSequence()
        .filter { it.isNotBlank() }
        .fold(
            Accumulator(
                sum = 0,
                priorRow = null,
                potentialPartNums = emptySet()
            )
        ) { acc, current ->
            val completedRow = current.foldIndexed(
                RowAccumulator(
                    sum = 0,
                    digits = emptyList(),
                    isPartNum = false,
                    partNumIndices = emptyList(),
                    potentialPartNums = emptySet()
                )
            ) { index, rowAcc, char ->
                resolveChar(char, rowAcc, index, current, acc)
            }

            val lastNumVal = if (completedRow.isPartNum) {
                completedRow.resolveValue()
                    .also { println("AddingL ${it}") }
            } else 0

            acc.copy(
                sum = acc.sum + completedRow.sum + lastNumVal,
                priorRow = current,
                potentialPartNums = completedRow.potentialPartNums
            )
        }.sum

    private fun resolveChar(
        char: Char,
        rowAcc: RowAccumulator,
        index: Int,
        current: String,
        acc: Accumulator
    ): RowAccumulator = when {
        char.isDigit() -> addDigit(rowAcc, index, current, char, acc.priorRow)
        else -> {
            val resolvedRowChar = if (rowAcc.digits.isNotEmpty()) { // finished a number
                resolveNumber(rowAcc, index, char, acc.priorRow)
            } else { // no number in progress
                rowAcc
            }
            if (char.isSymbol()) {
                val matches = acc.potentialPartNums.filter {
                    index - 1 in it.indices
                        || index in it.indices
                        || index + 1 in it.indices
                }
                matches.fold(resolvedRowChar) { matchAcc, match ->
                    println("AddingP ${match.value}")
                    matchAcc.copy(
                        sum = matchAcc.sum + match.value,
                        potentialPartNums = matchAcc.potentialPartNums - match
                    )
                }

            } else {
                resolvedRowChar
            }
        }
    }

    private fun addDigit(
        rowAcc: RowAccumulator,
        index: Int,
        current: String,
        char: Char,
        priorRow: String?
    ): RowAccumulator {
        val isPartNum = rowAcc.isPartNum
            || priorRow?.get(index)?.isSymbol() ?: false
            || (index > 0 && (current[index - 1].isSymbol() || priorRow?.get(index - 1)?.isSymbol() ?: false))
        return rowAcc.copy(digits = rowAcc.digits + char, isPartNum = isPartNum)
    }

    private fun resolveNumber(
        rowAcc: RowAccumulator,
        index: Int,
        char: Char,
        priorRow: String?
    ): RowAccumulator {
        val isPartNum = rowAcc.isPartNum
            || priorRow?.get(index)?.isSymbol() ?: false
            || char.isSymbol()
        val resolved = rowAcc.resolveValue()
        val digitRange = (index - rowAcc.digits.size)..<index
        return if (isPartNum) {
            println("AddingC ${resolved}")
            rowAcc.copy(
                sum = rowAcc.sum + resolved,
                digits = emptyList(),
                isPartNum = false,
                partNumIndices = rowAcc.partNumIndices.plus<IntRange>(digitRange)
            )
        } else {
            rowAcc.copy(
                digits = emptyList(),
                isPartNum = false,
                potentialPartNums = rowAcc.potentialPartNums.plus(PartNum(resolved, digitRange))
            )
        }
    }

    fun Char.isSymbol() = !isDigit() && this != '.'

    data class PartNum(val value: Int, val indices: IntRange)

    data class Accumulator(val sum: Int, val priorRow: String?, val potentialPartNums: Set<PartNum>)

    data class RowAccumulator(
        val sum: Int,
        val digits: List<Char>,
        val isPartNum: Boolean,
        val partNumIndices: List<IntRange>,
        val potentialPartNums: Set<PartNum>,
    ) {
        fun resolveValue(): Int = digits.joinToString("").toInt()
    }
}
