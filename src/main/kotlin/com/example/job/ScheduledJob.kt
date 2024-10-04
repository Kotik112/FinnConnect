package com.example.job

import org.slf4j.LoggerFactory

enum class MisfireInstructions {
    MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY,
    MISFIRE_INSTRUCTION_DO_NOTHING,
    MISFIRE_INSTRUCTION_FIRE_ONCE_NOW
}

annotation class ScheduledJob (
    val cronStrings: Array<String> = [""],
    val jobKey: String = "",
    val misfireInstruction: MisfireInstructions = MisfireInstructions.MISFIRE_INSTRUCTION_DO_NOTHING
)