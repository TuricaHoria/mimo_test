package com.example.mimotest.core.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

interface MviViewState

interface MviIntent

interface MviSingleEvent

abstract class MviViewModel<S : MviViewState, I : MviIntent, E : MviSingleEvent>(
    initialState: S
) : ViewModel() {

    private val stateFlow = MutableStateFlow(initialState)
    val state: StateFlow<S> = stateFlow.asStateFlow()

    private val _events = MutableSharedFlow<E>()
    val events = _events.asSharedFlow()

    abstract fun processIntent(intent: I)

    protected fun setState(reducer: S.() -> S) {
        stateFlow.value = stateFlow.value.reducer()
    }


    protected suspend fun emitEvent(event: E) {
        _events.emit(event)
    }
}


