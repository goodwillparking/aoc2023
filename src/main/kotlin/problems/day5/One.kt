package problems.day5

import util.getInput
import util.intersect
import kotlin.math.min

object One {

    private val segmentRegex = Regex("^([\\w-]+)(?: map)*:.*$")

    @JvmStatic
    fun main(args: Array<String>) {
        println("part1: ${part1()}")
        println("part2: ${part2()}")
    }

    private fun part1(): Long {
        val almanac = getInput("day5/one")
            .lineSequence()
            .parseAlmanac()

        return almanac.seeds.minOf(almanac::locationForSeed)
    }

    private fun part2(): Long {
         val almanac = getInput("day5/one")
             .lineSequence()
             .parseAlmanac()

        val sortedSource = almanac.seedToLocation.sortedBy { it.destinationStart }
        val seedRanges = almanac.seedRanges

        println(sortedSource)

        return sortedSource.asSequence()
            .mapNotNull { mapping ->
                seedRanges.asSequence()
                    .mapNotNull {
                        val intersection = mapping.sourceRange.intersect(it)
                        if (intersection.isEmpty()) null else intersection.first + mapping.offset
                    }
                    .sorted()
                    .firstOrNull()
            }.first()
    }

    fun Sequence<String>.parseAlmanac(): Almanac {
        val segments = fold(mapOf<String, List<String>>()) { segments, line ->
            val result = segmentRegex.find(line)
            when {
                result != null -> segments + (result.groups[1]!!.value to listOf(line))
                line.isNotBlank() -> {
                    val entry = segments.entries.last()
                    segments + (entry.key to entry.value + line)
                }
                else -> segments
            }
        }

        fun toLongs(line: String) = line.split(" ").map(String::toLong)

        fun parseCatMap(segmentName: String) = segments.getValue(segmentName)
            .drop(1)
            .map(::toLongs)
            .map { (destination, source, range) -> Mapping(
                sourceStart = source,
                destinationStart = destination,
                range = range
            )}

        val seedLine = segments.getValue("seeds").single().split(":")[1].trim()

        return Almanac(
            seeds = toLongs(seedLine),
            seedToSoil = parseCatMap("seed-to-soil"),
            soilToFertilizer = parseCatMap("soil-to-fertilizer"),
            fertilizerToWater = parseCatMap("fertilizer-to-water"),
            waterToLight = parseCatMap("water-to-light"),
            lightToTemperature = parseCatMap("light-to-temperature"),
            temperatureToHumidity = parseCatMap("temperature-to-humidity"),
            humidityToLocation = parseCatMap("humidity-to-location"),
        )
    }

    fun meld(source: Collection<Mapping>, destination: Collection<Mapping>): List<Mapping> {
        val sortedSource = source.sortedBy { it.destinationStart }.toMutableList()
        val sortedDestination = destination.sortedBy { it.sourceStart }.toMutableList()

        return meldRec(sortedSource, sortedDestination, emptyList())
    }

    private tailrec fun meldRec(
        source: List<Mapping>,
        destination: List<Mapping>,
        result: List<Mapping>
    ): List<Mapping> {
        val sHead = source.firstOrNull()
        val sTail = source.drop(1)
        val dHead = destination.firstOrNull()
        val dTail = destination.drop(1)

        return if (sHead != null) {
            if (dHead != null) {
                val mergeResult = merge(sHead, dHead)
                meldRec(
                    source = listOfNotNull(mergeResult.remainingSource) + sTail,
                    destination = listOfNotNull(mergeResult.remainingDestination) + dTail,
                    result = result + mergeResult.merged
                )
            } else {
                meldRec(sTail, dTail, result + sHead)
            }
        } else if (dHead != null) {
            meldRec(sTail, dTail, result + dHead)
        } else {
            result
        }
    }

    fun merge(source: Mapping, destination: Mapping): MergeResult = when {
        source.destinationStart < destination.sourceStart -> {
            val mergeEnd = min(destination.sourceStart, source.destinationRange.last + 1)
            MergeResult(
                merged = Mapping.destRange(source.destinationStart..<mergeEnd, source.offset),
                remainingSource = Mapping.destRange(
                    destRange = destination.sourceStart..source.destinationRange.last,
                    offset = source.offset
                ).takeIf(Mapping::isNotEmpty),
                remainingDestination = destination
            )
        }
        source.destinationStart > destination.sourceStart -> {
            val mergeEnd = min(source.destinationStart, destination.sourceRange.last + 1)
            MergeResult(
                merged = Mapping.sourceRange(destination.sourceStart..<mergeEnd, destination.offset),
                remainingSource = source,
                remainingDestination = Mapping.sourceRange(
                    sourceRange = source.destinationStart..destination.sourceRange.last,
                    offset = destination.offset
                ).takeIf(Mapping::isNotEmpty)
            )
        }
        source.destinationRange.last < destination.sourceRange.last -> {
            val (dMerge, dRemain) = destination.split(source.range)
            MergeResult(
                merged = exactMerge(source, dMerge),
                remainingSource = null,
                remainingDestination = dRemain
            )
        }
        source.destinationRange.last > destination.sourceRange.last -> {
            val (sMerge, sRemain) = source.split(destination.range)
            MergeResult(
                merged = exactMerge(sMerge, destination),
                remainingSource = sRemain,
                remainingDestination = null
            )
        }
        else -> MergeResult(exactMerge(source, destination), null, null)
    }

    private fun exactMerge(source: Mapping, destination: Mapping): Mapping {
        require(source.destinationRange == destination.sourceRange) {
            "Ranges aren't aligned: sourceDest: ${source.destinationRange}, destSource: ${destination.sourceRange}"
        }
        return Mapping.sourceRange(source.sourceRange, source.offset + destination.offset)
    }

    data class MergeResult(val merged: Mapping, val remainingSource: Mapping?, val remainingDestination: Mapping?)

    data class Almanac(
        val seeds: Collection<Long>,
        val seedToSoil: CatMap,
        val soilToFertilizer: CatMap,
        val fertilizerToWater: CatMap,
        val waterToLight: CatMap,
        val lightToTemperature: CatMap,
        val temperatureToHumidity: CatMap,
        val humidityToLocation: CatMap,
    ) {
        private val mapChain = listOf(
            seedToSoil,
            soilToFertilizer,
            fertilizerToWater,
            waterToLight,
            lightToTemperature,
            temperatureToHumidity,
            humidityToLocation,
        )

        val seedRanges = seeds.chunked(2).map { (start, size) -> start..<start + size }

        val seedToLocation by lazy {
            mapChain.reduce { acc, mappings ->
                meld(acc, mappings)
            }
        }

        fun locationForSeed(seed: Long): Long =
            mapChain.fold(seed) { acc, catMap -> catMap.destinationOf(acc) }
    }

    data class Mapping(val sourceStart: Long, val destinationStart: Long, val range: Long) {

        val sourceRange = sourceStart..<sourceStart + range
        val offset = destinationStart - sourceStart
        val destinationRange = destinationStart..<destinationStart + range

        override fun toString() = "$sourceRange -> $destinationRange ($offset)"

        fun isNotEmpty() = range > 0

        fun split(index: Long): Pair<Mapping, Mapping> {
            require(index in 1..<range)
            return Pair(
                Mapping(sourceStart, destinationStart, index),
                Mapping(sourceStart + index, destinationStart + index, range - index),
            )
        }

        fun destinationOf(source: Long) =
            if (source in sourceRange)
                source + offset
            else null

        companion object {
            fun sourceRange(sourceRange: LongRange, offset: Long) =
                Mapping(
                    sourceStart = sourceRange.start,
                    destinationStart = sourceRange.start + offset,
                    range = sourceRange.endInclusive - sourceRange.start + 1
                )

            fun destRange(destRange: LongRange, offset: Long) =
                Mapping(
                    sourceStart = destRange.start - offset,
                    destinationStart = destRange.start ,
                    range = destRange.endInclusive - destRange.start + 1
                )
        }
    }
}

private typealias CatMap = List<One.Mapping>

private fun CatMap.destinationOf(source: Long) = asSequence()
    .mapNotNull { it.destinationOf(source) }
    .firstOrNull()
    ?: source

