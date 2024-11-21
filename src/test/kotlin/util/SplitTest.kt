package util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SplitTest : StringSpec({

    "split empty" {
        IntRange.EMPTY.split() shouldBe (IntRange.EMPTY to IntRange.EMPTY)
    }

    "split size 1" {
        (3..3).split() shouldBe (IntRange.EMPTY to 3..3)
    }

    "split size 2" {
        (3..4).split() shouldBe (3..3 to 4..4)
    }

    "split size 3" {
        (3..5).split() shouldBe (3..3 to 4..5)
    }

    "split size 4" {
        (3..6).split() shouldBe (3..4 to 5..6)
    }
})
