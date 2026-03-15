package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_noRank_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = unwrap(controller.getLeaderboard())

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_noRank_sameScore_correctTimeSorting() {
        val first = GameResult(2, "first", 20, 10.0)
        val second = GameResult(3, "second", 20, 15.0)
        val third = GameResult(1, "third", 20, 20.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = unwrap(controller.getLeaderboard())

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_middleRank_returnsWindowOfSeven() {
        val gameResults = createNineGameResults()

        whenever(mockedService.getGameResults()).thenReturn(gameResults)

        val res: List<GameResult> = unwrap(controller.getLeaderboard(4))

        verify(mockedService).getGameResults()
        assertEquals(7, res.size)
        assertEquals(gameResults[7], res[0])
        assertEquals(gameResults[3], res[1])
        assertEquals(gameResults[4], res[2])
        assertEquals(gameResults[0], res[3])
        assertEquals(gameResults[1], res[4])
        assertEquals(gameResults[5], res[5])
        assertEquals(gameResults[6], res[6])
    }

    @Test
    fun test_getLeaderboard_rankZero_returnsStartOfList() {
        val gameResults = createNineGameResults()

        whenever(mockedService.getGameResults()).thenReturn(gameResults)

        val res: List<GameResult> = unwrap(controller.getLeaderboard(0))

        verify(mockedService).getGameResults()
        assertEquals(4, res.size)
        assertEquals(gameResults[8], res[0])
        assertEquals(gameResults[7], res[1])
        assertEquals(gameResults[3], res[2])
        assertEquals(gameResults[4], res[3])
    }

    @Test
    fun test_getLeaderboard_lastRank_returnsEndOfList() {
        val gameResults = createNineGameResults()

        whenever(mockedService.getGameResults()).thenReturn(gameResults)

        val res: List<GameResult> = unwrap(controller.getLeaderboard(8))

        verify(mockedService).getGameResults()
        assertEquals(4, res.size)
        assertEquals(gameResults[1], res[0])
        assertEquals(gameResults[5], res[1])
        assertEquals(gameResults[6], res[2])
        assertEquals(gameResults[2], res[3])
    }

    @Test
    fun test_getLeaderboard_negativeRank_returnsHTTP400() {
        val gameResults = createNineGameResults()

        whenever(mockedService.getGameResults()).thenReturn(gameResults)

        val res: ResponseEntity<List<GameResult>> = controller.getLeaderboard(-1)

        assertEquals(HttpStatus.BAD_REQUEST, res.statusCode)
        assertNull(res.body)
    }

    @Test
    fun test_getLeaderboard_rankTooHigh_returnsHTTP400() {
        val gameResults = createNineGameResults()

        whenever(mockedService.getGameResults()).thenReturn(gameResults)

        val res: ResponseEntity<List<GameResult>> = controller.getLeaderboard(9)

        assertEquals(HttpStatus.BAD_REQUEST, res.statusCode)
        assertNull(res.body)
    }

    /** Verifies that the [response] status is OK and returns the non-null body. */
    private fun unwrap(response: ResponseEntity<List<GameResult>>): List<GameResult> {
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        return response.body!!
    }

    /** Returns a pre-defined unsorted list of nine game results */
    private fun createNineGameResults(): List<GameResult> {
        val r0 = GameResult(9, "player9", 30, 25.0)
        val r1 = GameResult(8, "player8", 25, 20.0)
        val r2 = GameResult(4, "player4", 25, 25.0)
        val r3 = GameResult(5, "player5", 20, 15.0)
        val r4 = GameResult(1, "player1", 20, 20.0)
        val r5 = GameResult(2, "player2", 15, 10.0)
        val r6 = GameResult(6, "player6", 15, 20.0)
        val r7 = GameResult(7, "player7", 10, 10.0)
        val r8 = GameResult(3, "player3", 10, 25.0)

        // The correctly sorted sequence is:
        //  as variables in this method:     r0   r1   r2   r3   r4   r5   r6   r7   r8
        //  as indices of the returned list: [8], [7], [3], [4], [0], [1], [5], [6], [2]
        return listOf(r4, r5, r8, r2, r3, r6, r7, r1, r0)
    }

}