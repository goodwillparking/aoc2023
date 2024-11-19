package problems.day5

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import problems.day5.One.Mapping
import problems.day5.One.MergeResult

class MergeTest : StringSpec({

    "1 - 1" {
        val source = Mapping.destRange(1L..5L, 3) // -2..2 -> 1..5
        val dest = Mapping.sourceRange(1L..5L, -2) // 1..5 -> -1..3
        One.merge(source, dest) shouldBe
            MergeResult(
                merged = Mapping.sourceRange(-2L..2L, 1), // -2..2 -> -1..3
                remainingSource = null,
                remainingDestination = null
            )
    }

    "source smaller" {
        val source = Mapping.destRange(2L..3L, 3) // -1..0 -> 2..3
        val dest = Mapping.sourceRange(1L..5L, -2) // 1..5 -> -1..3
        One.merge(source, dest) shouldBe
            MergeResult(
                merged = Mapping.sourceRange(1L..1L, -2), // 1..1 -> -1..-1
                remainingSource = Mapping.destRange(2L..3L, 3),
                remainingDestination = Mapping.sourceRange(2L..5L, -2)
            )
    }

    "source smaller at end" {
        val source = Mapping.destRange(1L..3L, 3) // -2..0 -> 1..3
        val dest = Mapping.sourceRange(1L..5L, -2) // 1..5 -> -1..3
        One.merge(source, dest) shouldBe
            MergeResult(
                merged = Mapping.destRange(-1L..1L, 1), // 0..2 -> -1..1
                remainingSource = null,
                remainingDestination = Mapping.sourceRange(4L..5L, -2)
            )
    }

    "dest smaller at end" {
        val source = Mapping.destRange(1L..5L, 3) // -2..2 -> 1..5
        val dest = Mapping.sourceRange(1L..3L, -2) // 1..3 -> -1..1
        One.merge(source, dest) shouldBe
            MergeResult(
                merged = Mapping.destRange(-1L..1L, 1), // -2..0 -> -1..1
                remainingSource = Mapping.destRange(4L..5L, 3),
                remainingDestination = null
            )
    }

    "source smaller at start" {
        val source = Mapping.destRange(3L..5L, 3) // 0..2 -> 3..5
        val dest = Mapping.sourceRange(1L..5L, -2) // 1..5 -> -1..3
        One.merge(source, dest) shouldBe
            MergeResult(
                merged = Mapping.destRange(-1L..0L, -2), // 1..2 -> -1..0
                remainingSource = Mapping.destRange(3L..5L, 3),
                remainingDestination = Mapping.sourceRange(3L..5L, -2)
            )
    }

    "source overlap at start" {
        val source = Mapping.destRange(1L..5L, 3) // -2..2 -> 1..5
        val dest = Mapping.sourceRange(3L..5L, -2) // 3..5 -> 1..3
        One.merge(source, dest) shouldBe
            MergeResult(
                merged = Mapping.destRange(1L..2L, 3), // -2..-1 -> 1..2
                remainingSource = Mapping.destRange(3L..5L, 3),
                remainingDestination = Mapping.sourceRange(3L..5L, -2)
            )
    }

    "source adjacent to dest at end" {
        val source = Mapping.destRange(1L..2L, 3) // -2..-1 -> 1..2
        val dest = Mapping.sourceRange(3L..5L, -2) // 3..5 -> 1..3
        One.merge(source, dest) shouldBe
            MergeResult(
                merged = Mapping.destRange(1L..2L, 3), // -2..-1 -> 1..2
                remainingSource = null,
                remainingDestination = Mapping.sourceRange(3L..5L, -2)
            )
    }

    "disjoint source first" {
        val source = Mapping.destRange(1L..2L, 3) // -2..-1 -> 1..2
        val dest = Mapping.sourceRange(5L..8L, -2) // 5..8 -> 3..6
        One.merge(source, dest) shouldBe
            MergeResult(
                merged = Mapping.destRange(1L..2L, 3), // -2..-1 -> 1..2
                remainingSource = null,
                remainingDestination = Mapping.sourceRange(5L..8L, -2)
            )
    }

    "disjoint source last" {
        val source = Mapping.sourceRange(5L..8L, -2) // 5..8 -> 3..6
        val dest = Mapping.destRange(1L..2L, 3) // -2..-1 -> 1..2
        One.merge(source, dest) shouldBe
            MergeResult(
                merged = Mapping.destRange(1L..2L, 3), // -2..-1 -> 1..2
                remainingSource = Mapping.sourceRange(5L..8L, -2),
                remainingDestination = null
            )
    }
})
