package com.finsync

import com.finsync.domain.*
import com.finsync.groups.serverSplit
import org.junit.Assert.*
import org.junit.Test

class ServerSplitTest {
    private val people = listOf("a", "b", "me")
    @Test fun equalRemainderUsesOrderRatherThanPayer() {
        assertEquals(mapOf("a" to 4L, "b" to 3L, "me" to 3L), serverSplit(10, "me", people, SplitMode.Equal, emptyMap()))
    }
    @Test fun percentagesAndSharesUseLargestRemainders() {
        assertEquals(mapOf("a" to 3L, "b" to 3L, "me" to 4L), serverSplit(10, "me", people,
            SplitMode.Percentage, mapOf("a" to "33.33", "b" to "33.33", "me" to "33.34")))
        assertEquals(mapOf("a" to 4L, "b" to 3L, "me" to 3L), serverSplit(10, "me", people,
            SplitMode.Shares, people.associateWith { "1" }))
    }
    @Test fun exactAndInvalidSharesStayValidated() {
        assertEquals(mapOf("a" to 2L, "b" to 3L, "me" to 5L), serverSplit(10, "me", people,
            SplitMode.Exact, mapOf("a" to "2", "b" to "3", "me" to "5")))
        assertTrue(runCatching { serverSplit(10, "me", people, SplitMode.Shares, people.associateWith { "1000001" }) }.isFailure)
        assertTrue(runCatching { serverSplit(10, "me", people, SplitMode.Percentage, people.associateWith { "30" }) }.isFailure)
    }
}
