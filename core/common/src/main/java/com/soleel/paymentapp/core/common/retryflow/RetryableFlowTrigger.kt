package com.soleel.paymentapp.core.common.retryflow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription

class RetryableFlowTrigger {
    internal val retryEvent: MutableStateFlow<RetryEvent> = MutableStateFlow(RetryEvent.INITIAL)

    fun retry() {
        retryEvent.value = RetryEvent.RETRYING
    }
}

fun <T> RetryableFlowTrigger.retryableFlow(
    flowProvider: RetryableFlowTrigger.() -> Flow<T>,
): Flow<T> {
    return retryEvent
        .onSubscription {
            // reset to initial state on each new subscription so that the original flow can be re-evaluated
            retryEvent.value = RetryEvent.INITIAL
        }
        .filter {
            // allow retry and initial events to trigger the flow provider
            it == RetryEvent.RETRYING || it == RetryEvent.INITIAL
        }
        .flatMapLatest {
            // invoke the original flow provider
            flowProvider.invoke(this)
        }
        .onEach {
            // reset to idle on each value
            retryEvent.value = RetryEvent.IDLE
        }
}

internal enum class RetryEvent {
    RETRYING,
    INITIAL,
    IDLE,
}