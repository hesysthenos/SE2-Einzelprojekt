package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    /**
     * Returns a list of game results sorted by score (descending) and then by time in seconds (ascending).
     * If a [rank] is specified, returns a sublist: the result with the corresponding rank and three results above and below it, if they exist.
     * Rank 0 corresponds to the first place in the leaderboard.
     * If the [rank] is negative or greater than or equal to the number of game results, responds with HTTP 400.
     */
    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int? = null): ResponseEntity<List<GameResult>> {
        val allResults = gameResultService.getGameResults().sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

        // Return the full leaderboard if no rank was specified
        if (rank == null) {
            return ResponseEntity.ok(allResults)
        }

        // Respond with HTTP 400 if the rank is invalid
        if (rank < 0 || rank >= allResults.size) {
            return ResponseEntity.badRequest().build()
        }

        // Return the required sublist of the leaderboard
        val windowRadius = 3
        val startIndex = (rank - windowRadius).coerceAtLeast(0)
        val endIndex = (rank + windowRadius + 1).coerceAtMost(allResults.size)

        return ResponseEntity.ok(allResults.subList(startIndex, endIndex))
    }

}