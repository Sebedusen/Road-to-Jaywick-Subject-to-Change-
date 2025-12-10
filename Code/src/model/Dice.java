package model;

import java.util.ArrayList;
import java.util.Random;

public class Dice {
    public int diceAmount;
    public ArrayList<Integer> dice;

    public Dice(int diceAmount) {
        this.diceAmount = diceAmount;
        this.dice = new ArrayList<>();
    }

    public void rollDice() {
        dice.clear();
        Random r = new Random();

        for (int i = 0; i < diceAmount; i++) {
            dice.add(r.nextInt(6) + 1);
        }
    }
}
