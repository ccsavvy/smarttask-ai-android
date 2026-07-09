package com.practice.data.remote

internal object SuggestionEngine {

    private val goalTemplates = mapOf(
        "vacation" to listOf(
            "Research destinations",
            "Set a travel budget",
            "Book flights",
            "Reserve accommodation",
            "Create a packing checklist"
        ),
        "workout" to listOf(
            "Schedule three gym sessions",
            "Plan weekly meal prep",
            "Track daily step count",
            "Buy running shoes",
            "Set a rest-day reminder"
        ),
        "study" to listOf(
            "Review last week's notes",
            "Create a study schedule",
            "Practice with sample questions",
            "Summarize key concepts",
            "Plan a mock exam session"
        ),
        "groceries" to listOf(
            "Check pantry inventory",
            "Write a weekly meal plan",
            "Compare store prices",
            "Buy fresh produce",
            "Prep ingredients for the week"
        )
    )

    fun generate(goal: String, history: List<String>): List<String> {
        val normalizedGoal = goal.lowercase()
        val historyKeywords = history
            .flatMap { it.lowercase().split(Regex("\\W+")) }
            .filter { it.length > 3 }
            .distinct()

        val templateMatch = goalTemplates.entries.firstOrNull { (keyword, _) ->
            normalizedGoal.contains(keyword)
        }?.value

        val historyInspired = historyKeywords.take(2).map { keyword ->
            "Follow up on $keyword-related tasks"
        }

        val goalBased = templateMatch ?: listOf(
            "Break \"$goal\" into smaller steps",
            "Set a deadline for \"$goal\"",
            "Identify blockers for \"$goal\"",
            "Review progress on \"$goal\"",
            "Celebrate a milestone for \"$goal\""
        )

        return (goalBased + historyInspired)
            .distinct()
            .take(5)
    }
}
