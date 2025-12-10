package Logic;

import model.Dice;
import UI.GameFrameUI;
import java.util.Random;

import javax.swing.JOptionPane;

public class Game {

    private GameFrameUI ui;

    private Dice playerDice = new Dice(4);
    private Dice enemyDice = new Dice(4);

    private boolean playerTurn = true;
    private boolean roundStarted = false;

    private int guessAmount = 0;
    private int guessValue = 0;

    public Game(GameFrameUI ui) {
        this.ui = ui;
    }

    public void playerRolls() {
        if (roundStarted) {
            ui.append("You already rolled this round.");
            return;
        }

        playerDice.rollDice();
        enemyDice.rollDice();

        ui.append("Your dice: " + playerDice.dice + "\n");
        ui.append("Enemies rolled (hidden).\n");

        roundStarted = true;
    }

    public void playerMakesGuess() {
        if (!roundStarted) {
            ui.append("You must roll first.");
            return;
        }

        if (!playerTurn) {
            ui.append("It's not your turn.");
            return;
        }

        String amtStr = JOptionPane.showInputDialog("Guess amount:");
        String valStr = JOptionPane.showInputDialog("Guess value (2-6):");

        try {
            int amt = Integer.parseInt(amtStr);
            int val = Integer.parseInt(valStr);

            if (amt < guessAmount || (amt == guessAmount && val <= guessValue)) {
                ui.append("Guess must be HIGHER than the last.");
                return;
            }

            guessAmount = amt;
            guessValue = val;

            ui.append("You guess: " + amt + " × " + val);

            playerTurn = false;
            enemyTurn();

        } catch (Exception e) {
            ui.append("Invalid guess.");
        }
    }

    public void enemyTurn() {
        ui.append("Enemy thinking...");

        Random r = new Random();
        int decision = r.nextInt(3);

        if (decision == 0) {
            enemyCallsBullshit();
        } else {
            int newAmt = guessAmount + 1;
            int newVal = guessValue == 6 ? 6 : guessValue + 1;
            guessAmount = newAmt;
            guessValue = newVal;

            ui.append("Enemy guesses: " + guessAmount + " × " + guessValue);

            playerTurn = true;
        }
    }

    public void playerCallsBullshit() {
        if (!roundStarted) {
            ui.append("You must roll first.");
            return;
        }
        if (!playerTurn) {
            ui.append("It's not your turn.");
            return;
        }

        resolveBullshit(true);
    }

    public void enemyCallsBullshit() {
        ui.append("Enemy calls bullshit!");
        resolveBullshit(false);
    }

    private void resolveBullshit(boolean playerCalled) {
        int total = countTotalDice(guessValue);

        ui.append("Revealing all dice...");
        ui.append("Your dice: " + playerDice.dice);
        ui.append("Enemy dice: " + enemyDice.dice);
        ui.append("Total " + guessValue + "'s = " + total + " (1s count as wild)");

        boolean guessIsTrue = total >= guessAmount;

        if (guessIsTrue) {
            ui.append("The guess was TRUE!");
            if (playerCalled)
                ui.append("You lose the round.");
            else
                ui.append("Enemy loses the round.");
        } else {
            ui.append("The guess was FALSE!");
            if (playerCalled)
                ui.append("Enemy loses the round.");
            else
                ui.append("You lose the round.");
        }

        resetRound();
    }

    private int countTotalDice(int face) {
        int count = 0;

        for (int d : playerDice.dice) {
            if (d == face || d == 1) count++;
        }
        for (int d : enemyDice.dice) {
            if (d == face || d == 1) count++;
        }

        return count;
    }

    private void resetRound() {
        guessAmount = 0;
        guessValue = 0;
        roundStarted = false;
        playerTurn = true;

        ui.append("\n--- New Round ---\n");
    }
}
