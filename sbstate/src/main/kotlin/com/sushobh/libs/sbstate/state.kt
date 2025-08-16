package com.sushobh.libs.com.sushobh.libs.sbstate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Reducer definition
typealias Reducer<State, Event> = (State, Event) -> State
typealias Middleware<State, Event> = (state: State, event: Event, dispatch: (Event) -> Unit) -> Unit

// Store contract
interface Store<State, Event> {
    val state: StateFlow<State>
    fun dispatch(event: Event)
    fun reducer(state: State, event: Event): State
}

// Generic implementation
class StateStore<State, Event>(
    initialState: State,
    private val reducerFn: Reducer<State, Event>,
    private val middlewares: List<Middleware<State, Event>> = emptyList(),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Store<State, Event> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<State> = _state

    override fun reducer(state: State, event: Event): State =
        reducerFn(state, event)

    override fun dispatch(event: Event) {
        coroutineScope.launch {
            // Run middlewares first
            middlewares.forEach { mw ->
                mw(_state.value, event, ::dispatch)
            }
            // Reduce state
            val newState = reducer(_state.value, event)
            _state.value = newState
        }
    }
}
