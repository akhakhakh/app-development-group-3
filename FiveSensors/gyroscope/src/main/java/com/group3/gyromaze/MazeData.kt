// Defines maze layouts as data (walls tiles and goals)
package com.group3.gyromaze

import com.group3.gyromaze.model.TileType
import com.group3.gyromaze.model.Vec2

data class MazeLevel(
    val grid : List<List<TileType>>, // [row][col] -> outer list represents the rows
    val marbleStart : Vec2, // Starting position in grid units
    val teleporterPairs : Map<Vec2, Vec2> = emptyMap(), // entry -> exit
    val timedDoorPositions : List<Vec2> = emptyList(), // doors that toggle
    val doorIntervalSeconds : Float = 2f
) {
    val cols: Int get() = grid[0].size
    val rows: Int get() = grid.size
}

object MazeData {
    // W = Wall, F = Floor, G = Goal, I = Ice, T = Teleporter, D = Door
    private fun maze(vararg rows: String) : List<List<TileType>> {
        return rows.map { row ->
            row.map { char ->
                when (char) {
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

    /*
    Writing levels as strings,
    which are readable like a map. If I want to make any changes I can just edit the string,
    so no geometry math is needed
     */
    val levels = listOf(
        // Level 1 -> simple intro maze
        MazeLevel(
            grid = maze(
                "WWWWWWWGWW",
                "W........W",
                "W.WWWWW..W",
                "W........W",
                "W..WWWWW.W",
                "W........W",
                "WWWWWWWWWW"
            ),
            marbleStart = Vec2(1.5f, 1.5f)
        ),

        // Level 2 -> iced tiles introduced
        MazeLevel(
            grid = maze(
                "WWWWWGWWWW",
                "W.III....W",
                "W.WWWWW..W",
                "W.I......W",
                "W.WWWW.WWW",
                "W........W",
                "WWWWWWWWWW"
            ),
            marbleStart = Vec2(1.5f, 1.5f)
        ),

        // Level 3 -> teleporters
        MazeLevel(
            grid = maze(
                "WWWWWWWGWW",
                "W........W",
                "WWWWWWT.WW",
                "W........W",
                "W.WWWWWWWW",
                "WT.......W",
                "WWWWWWWWWW"
            ),
            marbleStart = Vec2(1.5f, 1.5f),
            teleporterPairs = mapOf(
                Vec2(6f, 2f) to Vec2(1f, 5f)
            )
        )
    )
}