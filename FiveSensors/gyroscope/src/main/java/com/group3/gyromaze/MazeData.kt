package com.group3.gyromaze

import com.group3.gyromaze.model.TileType
import com.group3.gyromaze.model.Vec2

data class MazeLevel(
    val grid: List<List<TileType>>,
    val marbleStart: Vec2,
    val teleporterPairs: Map<Vec2, Vec2> = emptyMap(),
    val timedDoorPositions: List<Vec2> = emptyList(),
    val doorIntervalSeconds: Float = 3f
) {
    val cols: Int get() = grid[0].size
    val rows: Int get() = grid.size
}

object MazeData {

    // Build a grid from readable strings
    // W=Wall  .=Floor  G=Goal(hole)  I=Ice  T=Teleporter  D=Door
    private fun maze(vararg rows: String): List<List<TileType>> {
        return rows.map { row ->
            row.map { ch ->
                when (ch) {
                    'W' -> TileType.WALL
                    'G' -> TileType.GOAL
                    'I' -> TileType.ICED_FLOOR
                    'T' -> TileType.TELEPORTER
                    'D' -> TileType.DOOR_CLOSED
                    else -> TileType.REG_FLOOR
                }
            }
        }
    }

    val levels = listOf(

        // Level 1 — tutorial, learn the basic controls
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "W........W",
                "W.WWWWW..W",
                "W........W",
                "W..WWWWW.W",
                "W........W",
                "WWWW...WWW",
                "WWWW.W.WWW",
                "WWWW...WWW",
                "WWWWWGWWWW"
            ),
            marbleStart = Vec2(1.5f, 1.5f)
        ),

        // Level 2 — ice tiles introduced
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "W.III....W",
                "W.W.WWWW.W",
                "W.I......W",
                "W.WWWW.WWW",
                "W......I.W",
                "WWWWW.WW.W",
                "W....I...W",
                "W.WWWWWW.W",
                "WWWWWWWGWW"
            ),
            marbleStart = Vec2(1.5f, 1.5f)
        ),

        // Level 3 — teleporter + timed door
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "W........W",
                "WWWWWWT.WW",
                "W........W",
                "W.WWWWDWWW",
                "WT.......W",
                "W.WWWWWW.W",
                "W........W",
                "WWWWW.WWWW",
                "WWWWWGWWWW"
            ),
            marbleStart = Vec2(1.5f, 1.5f),
            teleporterPairs = mapOf(
                Vec2(6f, 2f) to Vec2(1f, 5f)
            ),
            timedDoorPositions = listOf(Vec2(6f, 4f)),
            doorIntervalSeconds = 2.5f
        )
    )
}