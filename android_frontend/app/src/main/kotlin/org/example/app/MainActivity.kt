package org.example.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * Main Activity that hosts a minimalistic Tic Tac Toe game.
 *
 * This single-screen app displays a centered 3x3 grid with a player indicator on top,
 * and a reset button below. It supports:
 * - Player X/O selection at start (determines who goes first)
 * - Alternating turns between players
 * - Win/draw detection
 * - Highlighting the winning combination
 * - Restarting the game
 */
class MainActivity : Activity() {

    private lateinit var statusText: TextView
    private lateinit var resetButton: Button
    private lateinit var cells: Array<Button>

    // 'X', 'O', or ' ' to represent empty
    private var board: CharArray = CharArray(9) { ' ' }
    private var currentPlayer: Char = 'X'
    private var startingSymbol: Char = 'X'
    private var gameActive: Boolean = true

    private val winningCombos = arrayOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )

    // PUBLIC_INTERFACE
    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * Lifecycle entry point: initializes UI, wires up click listeners,
         * prompts the user to choose X or O, and starts a new game.
         */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.tvStatus)
        resetButton = findViewById(R.id.btnReset)

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

        // Wire up cell click listeners
        cells.forEachIndexed { index, button ->
            button.setOnClickListener { onCellClicked(index) }
        }

        // Wire up reset button
        resetButton.setOnClickListener { onResetClicked(it) }

        // Start fresh and prompt for X/O
        resetGame(promptSymbol = true)
    }

    // PUBLIC_INTERFACE
    fun onResetClicked(view: View) {
        /**
         * Resets the game and prompts the user to choose X or O again.
         */
        resetGame(promptSymbol = true)
    }

    /**
     * Handles user tap on a cell, places the current player's mark,
     * checks for a win/draw, and switches turns if the game is still active.
     */
    private fun onCellClicked(index: Int) {
        if (!gameActive) return
        if (board[index] != ' ') return

        // Place current player's move
        board[index] = currentPlayer
        cells[index].text = currentPlayer.toString()
        cells[index].isEnabled = false

        // Check for a winner or a draw
        val winner = checkWinner()
        if (winner != null) {
            gameActive = false
            showWinner(winner.first, winner.second)
            return
        }

        if (isDraw()) {
            gameActive = false
            statusText.text = getString(R.string.status_draw)
            return
        }

        // Alternate turn
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
        updateStatusTurn()
    }

    /**
     * Determines if there is a winner. Returns a Pair of the winning symbol and the
     * combo indices if a win is detected, or null otherwise.
     */
    private fun checkWinner(): Pair<Char, IntArray>? {
        for (combo in winningCombos) {
            val a = combo[0]
            val b = combo[1]
            val c = combo[2]
            if (board[a] != ' ' && board[a] == board[b] && board[b] == board[c]) {
                return Pair(board[a], combo)
            }
        }
        return null
    }

    /**
     * Returns true if the board has no empty cells and there is no winner.
     */
    private fun isDraw(): Boolean {
        return board.none { it == ' ' }
    }

    /**
     * Highlights the winning combination and updates the status text.
     */
    private fun showWinner(symbol: Char, combo: IntArray) {
        statusText.text = getString(R.string.status_win, symbol.toString())

        // Highlight winning cells with accent color and make text white for contrast
        val accent = resources.getColor(R.color.accent, theme)
        val white = resources.getColor(android.R.color.white, theme)
        for (i in combo) {
            cells[i].setBackgroundColor(accent)
            cells[i].setTextColor(white)
        }

        // Disable remaining cells
        for (i in 0 until 9) {
            cells[i].isEnabled = false
        }
    }

    /**
     * Resets board UI and state. If promptSymbol is true, asks the player to choose X or O.
     */
    private fun resetGame(promptSymbol: Boolean) {
        board = CharArray(9) { ' ' }
        gameActive = true

        val defaultTextColor = resources.getColor(R.color.secondary, theme)

        for (i in 0 until 9) {
            val btn = cells[i]
            btn.text = ""
            btn.isEnabled = true
            btn.setBackgroundResource(R.drawable.cell_bg)
            btn.setTextColor(defaultTextColor)
        }

        if (promptSymbol) {
            promptPlayerChoice()
        } else {
            currentPlayer = startingSymbol
            updateStatusTurn()
        }
    }

    /**
     * Updates the status text to indicate whose turn it is.
     */
    private fun updateStatusTurn() {
        statusText.text = getString(R.string.status_turn, currentPlayer.toString())
    }

    /**
     * Prompts the user to choose X or O. The chosen symbol goes first.
     */
    private fun promptPlayerChoice() {
        val builder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.choose_symbol_title))
            .setMessage(getString(R.string.choose_symbol_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.symbol_x)) { dialog, _ ->
                startingSymbol = 'X'
                currentPlayer = 'X'
                updateStatusTurn()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.symbol_o)) { dialog, _ ->
                startingSymbol = 'O'
                currentPlayer = 'O'
                updateStatusTurn()
                dialog.dismiss()
            }

        builder.show()
    }
}
