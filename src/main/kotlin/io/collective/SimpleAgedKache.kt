package io.collective

import java.time.Clock
import java.util.stream.Collectors

class SimpleAgedKache {
    private var entryList: MutableList<ExpirableEntry> = ArrayList()
    private var clock: Clock? = null

    private var startTime: Long = 0


    internal class ExpirableEntry(val key: String, val value: String, val retentionInMillis: Int)


    constructor(clock: Clock?) {
        this.clock = clock
        startTime = this.clock!!.millis()
    }

    constructor() {}

    fun put(key: String, value: String, retentionInMillis: Int) {
        addEntry(key, value, retentionInMillis)
    }

    fun isEmpty(): Boolean {
        return entryList.isEmpty()
    }

    fun size(): Int {
        if (clock != null) {
            entryExpiresCheck()
        }
        return entryList.size
    }

    fun addEntry(key: String, value: String, retentionInMillis: Int) {
        val entry = ExpirableEntry(key, value, retentionInMillis)
        entryList.add(entry)
    }

    operator fun get(key: String): Any? {
        for (entry in entryList) {
            if (entry.key == key) {
                return entry.value
            }
        }
        return null
    }

    fun entryExpiresCheck() {
        val millisPast = clock!!.millis()
        val difference = millisPast - startTime
        entryList = entryList
                .stream()
                .filter { entry: ExpirableEntry -> entry.retentionInMillis > difference }
                .collect(Collectors.toList())
    }


}