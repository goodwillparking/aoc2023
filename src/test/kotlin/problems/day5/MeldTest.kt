package problems.day5

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import problems.day5.One.Mapping

class MeldTest : StringSpec({

    fun Mapping.log(): Mapping {
        return also { println("${it.sourceRange} -> ${it.destinationRange} ($offset)") }
    }

    "no dest" {
        One.meld(
            source = listOf(Mapping(1, 2, 3)),
            destination = listOf()
        ) shouldBe listOf(
            Mapping(1, 2, 3)
        )
    }

    "no source" {
        One.meld(
            source = listOf(),
            destination = listOf(Mapping(1, 2, 3)),
        ) shouldBe listOf(
            Mapping(1, 2, 3)
        )
    }

    "sample - temp to location" {
        One.meld(
            source = listOf(
                Mapping(69, 0, 1).log(),
                Mapping(0, 1, 69).log(),
            ),
            destination = listOf(
                Mapping(56, 60, 37).log(),
                Mapping(93, 56, 4).log(),
            ),
        ).also { println() }.sortedBy { it.sourceStart }.onEach(Mapping::log) shouldBe listOf(
            Mapping(0, 1, 55),
            Mapping(55, 60, 14),
            Mapping(69, 0, 1),
            Mapping(70, 74, 23),
            Mapping(93, 56, 4),
        )
    }

    "sample - light to location" {
        val mappings = listOf(
            listOf(
                Mapping(45, 81, 19),
                Mapping(64, 68, 13),
                Mapping(77, 45, 23),
            ),
            listOf(
                Mapping(0, 1, 69),
                Mapping(69, 0, 1),
            ),
            listOf(
                Mapping(56, 60, 37),
                Mapping(93, 56, 4),
            ),
        ).onEach {
            it.onEach { it.log() }
            println()
        }

        val reduced = mappings.reduce(One::meld)
            .sortedBy { it.sourceStart }
            .onEach(Mapping::log)

        reduced shouldBe listOf(
            Mapping(0, 1, 45),
            Mapping(45, 85, 12),
            Mapping(57, 56, 4),
            Mapping(61, 97, 3),
            Mapping(64, 73, 1),
            Mapping(65, 0, 1),
            Mapping(66, 74, 11),
            Mapping(77, 46, 10),
            Mapping(87, 60, 13),
        )
    }
})
