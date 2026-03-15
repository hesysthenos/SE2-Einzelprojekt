package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.mockito.Mockito.`when` as whenever

class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getAllGameResults_noResults_returnsEmptyList() {
        whenever(mockedService.getGameResults()).thenReturn(emptyList<GameResult>())

        val res: List<GameResult> = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(emptyList<GameResult>(), res)
    }

    @Test
    fun test_addGameResult_delegatesToService() {
        val gameResult = GameResult(0, "player1", 17, 15.3)

        controller.addGameResult(gameResult)

        verify(mockedService).addGameResult(gameResult)
    }

    @Test
    fun test_getAllGameResults_withResults_returnsResults() {
        val gameResult1 = GameResult(0, "player1", 17, 15.3)
        val gameResult2 = GameResult(0, "player2", 25, 16.0)

        val gameResults = listOf(gameResult1, gameResult2)

        whenever(mockedService.getGameResults()).thenReturn(gameResults)

        val res: List<GameResult> = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(gameResults, res)
    }

    @Test
    fun test_getGameResultById_existingId_returnsObject() {
        val gameResult = GameResult(1, "player1", 17, 15.3)

        whenever(mockedService.getGameResult(1)).thenReturn(gameResult)

        val res = controller.getGameResult(1)

        verify(mockedService).getGameResult(1)
        assertEquals(gameResult, res)
    }

    @Test
    fun test_getGameResultById_nonexistentId_returnsNull() {
        whenever(mockedService.getGameResult(2)).thenReturn(null)

        val res = controller.getGameResult(2)

        verify(mockedService).getGameResult(2)
        assertNull(res)
    }

    @Test
    fun test_deleteGameResultById_delegatesToService() {
        controller.deleteGameResult(1)

        verify(mockedService).deleteGameResult(1)
    }

}