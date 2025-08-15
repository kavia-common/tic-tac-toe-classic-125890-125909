package org.example.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

/**
 * A simple Tic Tac Toe game implemented entirely on the frontend.
 * Features:
 * - Player X/O selection
 * - Alternating turns
 * - Win/draw detection with winning line highlight
 * - Reset game
 * - Minimal, light-themed design using provided color palette
 */
class MainActivity : Activity() {

    private lateinit var statusText: TextView
    private lateinit var playerGroup: RadioGroup
    private lateinit var radioX: RadioButton
    private lateinit var radioO: RadioButton
    private lateinit var resetBtn: Button
    private lateinit var gridLayout: GridLayout

    private lateinit var cells: Array<Button>

    // Game state
    private var board: CharArray = CharArray(9) { ' ' }
    private var currentPlayer: Char = 'X'
    private var gameActive: Boolean = true
    private var movesCount: Int = 0

    // Winning combinations
    private val winCombos: Array<IntArray> = arrayOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )

    // Colors from palette
    private val colorPrimary by lazy { resources.getColor(R.color.primary, theme) }
    private val colorAccent by lazy { resources.getColor(R.color.accent, theme) }
    private val colorSecondary by lazy { resources.getColor(R.color.secondary, theme) }
    private val colorBackground by lazy { resources.getColor(R.color.background, theme) }

    // PUBLIC_INTERFACE
    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * Activity entry point. Initializes the UI and game state.
         *
         * Parameters:
         * - savedInstanceState: Bundle? - optional saved state for restoring game state on rotation.
         *
         * Returns:
         * - Unit. Sets up the content view, binds UI elements, and sets listeners.
         */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupGrid()
        setupPlayerSelection()
        setupReset()

        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        } else {
            startNewGameFromSelection()
        }
        updateStatus()
    }

    private fun bindViews() {
        statusText = findViewById(R.id.tvStatus)
        playerGroup = findViewById(R.id.playerGroup)
        radioX = findViewById(R.id.rbX)
        radioO = findViewById(R.id.rbO)
        resetBtn = findViewById(R.id.btnReset)
        gridLayout = findViewById(R.id.gridBoard)
        cells = arrayOf(
            findViewById(R.id.cell0),
            findViewById(R.id.cell1),
            findViewById(R.id.cell2),
            findViewById(R.id.cell3),
            findViewById(R.id.cell4),
            findViewById(R.id.cell5),
            findViewById(R.id.cell6),
            findViewById(R.id.cell7),
            findViewById(R.id.cell8)
        )
    }

    private fun setupGrid() {
        for (i in cells.indices) {
            val idx = i
            cells[i].setOnClickListener {
                onCellClicked(idx)
            }
            resetCellVisual(cells[i])
        }
    }

    private fun setupPlayerSelection() {
        // Default to X selected
        radioX.isChecked = true
        playerGroup.setOnCheckedChangeListener { _, _ ->
            // Allow changing only before the first move
            if (movesCount == 0 && gameActive) {
                currentPlayer = selectedPlayer()
                updateStatus()
            }
        }
    }

    private fun setupReset() {
        resetBtn.setOnClickListener {
            resetGame()
        }
    }

    private fun selectedPlayer(): Char {
        return if (radioX.isChecked) 'X' else 'O'
    }

    private fun startNewGameFromSelection() {
        currentPlayer = selectedPlayer()
        board.fill(' ')
        gameActive = true
        movesCount = 0
        for (b in cells) {
            b.text = ""
            resetCellVisual(b)
            b.isEnabled = true
        }
        playerGroup.isEnabled = true
    }

    private fun onCellClicked(index: Int) {
        if (!gameActive) return
        if (board[index] != ' ') return

        board[index] = currentPlayer
        applyCellMoveVisual(cells[index], currentPlayer)
        movesCount++

        val winningCombo = findWinningCombo()
        if (winningCombo != null) {
            gameActive = false
            highlightWinning(winningCombo)
            statusText.text = if (currentPlayer == 'X') getString(R.string.x_wins) else getString(R.string.o_wins)
            disableAllCells()
            return
        }

        if (movesCount == 9) {
            gameActive = false
            statusText.text = getString(R.string.draw)
            disableAllCells()
            return
        }

        // Next player's turn
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
        updateStatus()
        // After first move, prevent changing the starting player
        if (movesCount > 0) {
            playerGroup.isEnabled = false
            radioX.isEnabled = false
            radioO.isEnabled = false
        }
    }

    private fun disableAllCells() {
        for (b in cells) b.isEnabled = false
    }

    private fun findWinningCombo(): IntArray? {
        for (combo in winCombos) {
            val (a, b, c) = combo
            val ch = board[a]
            if (ch != ' ' && ch == board[b] && ch == board[c]) {
                return combo
            }
        }
        return null
    }

    private fun highlightWinning(combo: IntArray) {
        for (i in combo) {
            val b = cells[i]
            b.setBackgroundColor(colorAccent)
            b.setTextColor(colorSecondary)
        }
    }

    private fun resetCellVisual(b: Button) {
        b.setBackgroundColor(colorBackground)
        b.setTextColor(colorSecondary)
    }

    private fun applyCellMoveVisual(b: Button, player: Char) {
        b.text = player.toString()
        // Color code: X -> primary, O -> secondary
        b.setTextColor(if (player == 'X') colorPrimary else colorSecondary)
        b.isEnabled = false
    }

    private fun updateStatus() {
        statusText.text = if (currentPlayer == 'X') {
            getString(R.string.x_turn)
        } else {
            getString(R.string.o_turn)
        }
    }

    private fun resetGame() {
        board.fill(' ')
        movesCount = 0
        gameActive = true
        currentPlayer = selectedPlayer()
        for (b in cells) {
            b.text = ""
            resetCellVisual(b)
            b.isEnabled = true
        }
        playerGroup.isEnabled = true
        radioX.isEnabled = true
        radioO.isEnabled = true
        updateStatus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharArray("board", board)
        outState.putChar("currentPlayer", currentPlayer)
        outState.putBoolean("gameActive", gameActive)
        outState.putInt("movesCount", movesCount)
        outState.putBoolean("radioXChecked", radioX.isChecked)
        outState.putBoolean("radioEnabled", playerGroup.isEnabled)
    }

    private fun restoreState(saved: Bundle) {
        board = saved.getCharArray("board") ?: CharArray(9) { ' ' }
        currentPlayer = saved.getChar("currentPlayer", 'X')
        gameActive = saved.getBoolean("gameActive", true)
        movesCount = saved.getInt("movesCount", 0)
        radioX.isChecked = saved.getBoolean("radioXChecked", true)
        radioO.isChecked = !radioX.isChecked
        playerGroup.isEnabled = saved.getBoolean("radioEnabled", true)
        radioX.isEnabled = playerGroup.isEnabled
        radioO.isEnabled = playerGroup.isEnabled

        // Re-apply visuals to grid based on board
        for (i in board.indices) {
            val ch = board[i]
            val b = cells[i]
            if (ch == ' ') {
                b.text = ""
                resetCellVisual(b)
                b.isEnabled = gameActive
            } else {
                applyCellMoveVisual(b, ch)
            }
        }

        // If game not active and there was a winner, re-highlight
        val winningCombo = findWinningCombo()
        if (!gameActive && winningCombo != null) {
            highlightWinning(winningCombo)
            disableAllCells()
        }
        updateStatus()
    }
}
