package com.rui157953.andrarmon.tool.network

object Network {
    private var currentState: State = State.DISCONNECTED
        @Synchronized get

    @Synchronized
    fun changeState(state: State) {
        currentState = state
    }

    @Synchronized
    fun isOnline(): Boolean = currentState == State.ONLINE

    @Synchronized
    fun isConnected(): Boolean = currentState == State.CONNECTED

    enum class State private constructor() {
        CONNECTING, CONNECTED, ONLINE, DISCONNECTING, DISCONNECTED
    }
}