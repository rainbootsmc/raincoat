package dev.uten2c.raincoat.updater

sealed interface UpdateStatus {
    object Updating : UpdateStatus
    object Completed : UpdateStatus
    data class ErrorShouldRestart(val message: String) : UpdateStatus
    data class ErrorReturnToTitle(val message: String) : UpdateStatus
}
