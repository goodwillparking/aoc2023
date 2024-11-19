package util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

// TODO: property test
class IntersectTest : StringSpec({

    "equal" {
        (1L..6L).intersect(1L..6L) shouldBe 1L..6L
    }

    "disjoint" {
        (1L..6L).intersect(7L..8L).shouldBeEmpty()
    }

    "a in b" {
        (2L..5L).intersect(1L..6L) shouldBe 2L..5L
    }

    "b in a" {
        (1L..6L).intersect(2L..5L) shouldBe 2L..5L
    }

    "a partial in b" {
        (1L..5L).intersect(2L..8L) shouldBe 2L..5L
    }

    "b partial in a" {
        (5L..10L).intersect(2L..8L) shouldBe 5L..8L
    }
})
