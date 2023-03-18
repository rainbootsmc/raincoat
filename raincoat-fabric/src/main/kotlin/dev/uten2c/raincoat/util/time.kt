package dev.uten2c.raincoat.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun now(): Instant = Clock.System.now()
