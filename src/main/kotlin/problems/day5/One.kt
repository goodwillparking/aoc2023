package problems.day5

import util.getInput

private object One {

    val segmentRegex = Regex("^([\\w-]+)(?: map)*:.*$")

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
         return getInput("day5/one")
             .lineSequence()
             .let { TODO() }
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
}

private data class Almanac(
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

    fun locationForSeed(seed: Long): Long =
        mapChain.fold(seed) { acc, catMap -> catMap.destinationOf(acc) }
}

private data class Mapping(val sourceStart: Long, val destinationStart: Long, val range: Long) {

    private val sourceRange = sourceStart..<sourceStart + range
    private val offset = destinationStart - sourceStart

    fun destinationOf(source: Long) =
        if (source in sourceRange)
            source + offset
        else null
}

private typealias CatMap = List<Mapping>

private fun CatMap.destinationOf(source: Long) = asSequence()
    .mapNotNull { it.destinationOf(source) }
    .firstOrNull()
    ?: source

