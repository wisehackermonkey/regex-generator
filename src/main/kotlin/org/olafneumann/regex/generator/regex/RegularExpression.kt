package org.olafneumann.regex.generator.regex


data class RegularExpression(
    val parts: Collection<RecognizerCombiner.RegularExpressionPart>
) {
    companion object {
        @Suppress("MaxLineLength")
        private val patternPartitionerRegex = Regex(
            """(?<complete>\(\?(?:[a-zA-Z]+|[+-]?[0-9]+|&\w+|P=\w+)\))|(?<open>\((?:\?(?::|!|>|=|\||<=|P?<[a-z][a-z0-9]*>|'[a-z][a-z0-9]*'|[-a-z]+:))?)|(?<part>(?:\\.|\[(?:[^\]\\]|\\.)+\]|[^|)])(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)|(?<close>\)(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)|(?<alt>\|)""",
            options = setOf(RegexOption.IGNORE_CASE)
        )
    }

    val patternAfterPartSelection: String
        get() = parts.joinToString(separator = "") { it.pattern }

    val finalPattern: String
        get() {
            var pattern = patternAfterPartSelection
            for (capturingGroup in capturingGroups) {
                val parts = patternPartitionerRegex.findAll(pattern).toList()
                val r1 = if (capturingGroup.range.first != 0) IntRange(start = 0, endInclusive = parts[capturingGroup.range.first - 1].range.last) else null
                val r2 = IntRange(start = parts[capturingGroup.range.first].range.first, endInclusive = parts[capturingGroup.range.last].range.last)
                val r3 = if (capturingGroup.range.last != parts.lastIndex) IntRange(start = parts[capturingGroup.range.last + 1].range.first, endInclusive =  parts.last().range.last) else null

                pattern = "${r1?.let { pattern.substring(it) } ?: ""}(${capturingGroup.name?.let { "<$it>" } ?: ""}${pattern.substring(r2)})${r3?.let { pattern.substring(it) } ?: ""}"
            }
            return pattern
        }

    private val _capturingGroups = mutableListOf<CapturingGroup>()

    val capturingGroups: List<CapturingGroup>
        get() = _capturingGroups

    fun add(capturingGroup: CapturingGroup) {
        for (cg in _capturingGroups) {
            cg.range = cg.range.forPosition(capturingGroup.range)
        }
        _capturingGroups.add(capturingGroup)
        _capturingGroups.sortBy { it.range.first }
    }

    val patternParts: Sequence<MatchResult>
        get() = patternPartitionerRegex.findAll(finalPattern)

    private fun IntRange.forPosition(other: IntRange): IntRange {
        return if (other.last < this.first) {
            plus(2)
        } else if (other.first > this.last) {
            this
        } else if (other.first < this.first && other.last > this.last) {
            plus(1)
        } else if (other.first > this.first && other.last < this.last) {
            plus(0, 2)
        } else {
            error("Invalid capturing group position: this(${this.toString()}) other(${other.toString()})")
        }
    }

    private fun IntRange.plus(amount: Int): IntRange = plus(amount, amount)
    private fun IntRange.plus(forFirst: Int, forLast: Int): IntRange {
        return IntRange(start = start + forFirst, endInclusive = endInclusive + forLast)
    }

    data class CapturingGroup(
        var range: IntRange,
        var name: String?
    )
}

