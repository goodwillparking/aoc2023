package util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SplitTest : StringSpec({

    "split empty" {
        LongRange.EMPTY.split() shouldBe (LongRange.EMPTY to LongRange.EMPTY)
    }

    "split size 1" {
        (3L..3).split() shouldBe (LongRange.EMPTY to 3L..3)
    }

    "split size 2" {
        (3L..4).split() shouldBe (3L..3 to 4L..4)
    }

    "split size 3" {
        (3L..5).split() shouldBe (3L..3 to 4L..5)
    }

    "split size 4" {
        (3L..6).split() shouldBe (3L..4 to 5L..6)
    }
})
