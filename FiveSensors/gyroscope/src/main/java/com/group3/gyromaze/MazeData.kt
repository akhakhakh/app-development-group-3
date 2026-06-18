package com.group3.gyromaze

import com.group3.gyromaze.model.TileType
import com.group3.gyromaze.model.Vec2

data class MazeLevel(
    val grid: List<List<TileType>>,
    val marbleStart: Vec2,
    val teleporterPairs: Map<Vec2, Vec2> = emptyMap(),
    val timedDoorPositions: List<Vec2> = emptyList(),
    val doorIntervalSeconds: Float = 3f,
    val tutorialMessage: String? = null
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

        // ------ Level 1: tutorial level ------
        // Path: start (1,1) → zigzag corridors → hole at (5,9)
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "W........W",
                "W.WWWWW..W",
                "W........W",
                "W..WWWWW.W",
                "W........W",
                "WWWWW.WWWW",
                "W....G...W",  // hole in middle row, reachable from left and right
                "W........W",
                "WWWWWWWWWW"
            ),
            marbleStart     = Vec2(1.5f, 1.5f),
            tutorialMessage = "Tilt your phone to roll the marble.\nGuide it into the dark hole to complete the level!"
        ),

        // ------ Level 2: Ice tiles introduced ------
        // Path: start (1,8) bottom-left → navigate up through ice patches → hole (8,1)
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "WWWWWWWWGW",  // hole top-right, open from below
                "WWWWWWWW.W",
                "W.WWWW.I.W",
                "W......I.W",
                "W.WWWWWI.W",
                "W.W..I...W",
                "W....WWWWW",
                "W........W",
                "WWWWWWWWWW"
            ),
            marbleStart     = Vec2(1.5f, 8.5f),
            tutorialMessage = "Watch out for the icy blue tiles!\nThey are slippery!"
        ),

        // ------ Level 3: Timed doors introduced ------
        // D tiles at col 5 row 4 and col 5 row 6 — neither adjacent to G
        // Path: start (1,1) → right corridor → wait for door → loop to G (1,5)
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "W........W",
                "W.WWWWW.WW",
                "W.W.....WW",
                "WDWDWDWWWW",
                "GI.......W",  // hole on left wall, open floor to its right at (2,5)
                "WIW.WDWWWW",
                "WIW.....WW",
                "W.WWWWW.WW",
                "WWWWWWWWWW"
            ),
            marbleStart = Vec2(8.5f, 1.5f),
            timedDoorPositions = listOf(Vec2(5f, 4f), Vec2(5f, 6f)),
            doorIntervalSeconds = 3f,
            tutorialMessage = "These doors are cursed. Try to figure out when to pass through them."
        ),

        // ------ Level 4: Teleporters introduced ------
        // T at (7,7) warps to (2,2); path goes through teleporter to reach top half
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "WW..GWWWWW",  // hole at (4,1), open from below at (4,2)
                "WW.......W",
                "WWWWWWWW.W",
                "W........W",
                "WWWWWWWWWW",
                "W........W",
                "W......T.W",  // teleporter entry at (7,7)
                "W.WWWWWWWW",
                "W........W"
            ),
            marbleStart     = Vec2(1.5f, 9.5f),
            teleporterPairs = mapOf(Vec2(7f, 7f) to Vec2(3f, 2f)),
            tutorialMessage = "The glowing purple tile is a teleporter!\nStep on it to be instantly warped somewhere else.\nGood Luck!"
        ),

        // ------ Level 5: Ice + timed doors combined -------
        // G at (5,5) centre; ice corridors lead to it; door guards the direct path
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "W.......WW",
                "W.WWWWI..W",
                "W.W..II..W",
                "W.W.WDWWWW",
                "W.W..G...W",  // hole centre (5,5), reachable from (4,5)(6,5)(5,4)(5,6)
                "W.W.WWWWWW",
                "W.W.I....W",
                "W....WWWWW",
                "WWWWWWWWWW"
            ),
            marbleStart         = Vec2(1.5f, 1.5f),
            timedDoorPositions  = listOf(Vec2(3f, 4f)),
            doorIntervalSeconds = 2.5f
        ),

        // ------ Level 6: Teleporter + ice ------
        // T at (8,2) warps to (1,7); ice patch before the teleporter
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "WWWWWW...W",
                "WWWWWW.I.W",  // teleporter entry (8,2)
                "WWWWWWIITW",
                "W.WWWWWWWW",
                "W........W",
                "W.WWWWWWWW",
                "W........W",  // teleporter exit landing at (2,7), hole at (1,9)
                "WWWWWWWW.W",
                "WG.......W"   // hole at (1,9)
            ),
            marbleStart = Vec2(7.5f, 1.5f),
            teleporterPairs = mapOf(Vec2(8f, 3f) to Vec2(1f, 7f))
        ),

        // ----- Level 7: Two timed doors + teleporter ------
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "WWWWWWWWGW",  // hole (8,1) open from (8,2)
                "WWWWWWWW.W",
                "W.WWWWWDDW",  // door at (7,3)
                "W........W",
                "WWWWWDWWWW",  // door at (5,5)
                "W........W",
                "W.T.WWWWWW",  // teleporter at (2,7) → (7,4)
                "W.WWWWWWWW",
                "W........W"
            ),
            marbleStart         = Vec2(1.5f, 9.5f),
            teleporterPairs     = mapOf(Vec2(2f, 7f) to Vec2(5f, 9f)),
            timedDoorPositions  = listOf(Vec2(7f, 3f), Vec2(5f, 5f)),
            doorIntervalSeconds = 3f
        ),

        // ── Level 8 — Ice + two teleporters, hole on the right side ────────
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "W.WWWWWWWW",
                "W.W..IIITW",  // T entry at (8,2) → warps to (1,6)
                "W.W.WWWWWW",
                "W.W......W",
                "W.WWWWWW.W",
                "WT.......W",  // T exit landing at (1,6); second T at (2,6) → (7,9)
                "WWWWWWWW.W",
                "W..IIII..W",
                "WWWWWWWWGW"   // hole (8,9) open from (8,8)
            ),
            marbleStart = Vec2(1.5f, 1.5f),
            teleporterPairs = mapOf(
                Vec2(8f, 2f) to Vec2(1f, 6f),
                Vec2(1f, 6f) to Vec2(7f, 8f)
            )
        ),

        // ── Level 9 — All obstacles mixed, hole in the middle-left ─────────
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "WIII......",
                "W.WWWWDWII",  // door at (6,2)
                "W.W..IIWWW",
                "W.W.WWWWWW",
                "W.W......W",  // hole at (1,5) open from (1,4) above — floor at (1,4)
                "W.W.WWTWWW",  // teleporter at (7,6)
                "W.W..II.WW",
                "W.WDWWDWWW",  // door at (6,8)
                "WWWGWWWWWW"
            ),
            marbleStart = Vec2(9f, 2f),
            teleporterPairs = mapOf(Vec2(7f, 6f) to Vec2(1f, 4f)),
            timedDoorPositions = listOf(Vec2(6f, 2f), Vec2(6f, 8f)),
            doorIntervalSeconds = 2.8f
        ),

        // ── Level 10 — Hardest: all obstacles, hole in dead centre ──────────
        MazeLevel(
            grid = maze(
                "WWWWWWWWWW",
                "WWWWWWWGWW",
                "WIIIIDII.W",
                "W.WWWWWW.W",
                "W.WWWWWW.W",
                "W.WWWWWW.W",
                "W.IIIIIITW",
                "W.WWWWWW.W",
                "WWWWWDWW.W",
                "WWWWWWWW.W"
            ),
            marbleStart         = Vec2(8.5f, 9.5f),
            teleporterPairs     = mapOf(Vec2(8f, 6f) to Vec2(1f, 2f)),
            timedDoorPositions  = listOf(Vec2(5f, 8f), Vec2(5f, 2f)),
            doorIntervalSeconds = 1.8f
        )
    )
}