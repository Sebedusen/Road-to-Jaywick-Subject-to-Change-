package model;

import java.util.ArrayList;
import java.util.Random;

public class Cup {
	public int diceAmount;
	public ArrayList<Integer> dice = new ArrayList<Integer>();

	public Cup(int diceAmount) {
		this.diceAmount = diceAmount;
		this.dice = new ArrayList<>();
	}

	public void rollDice() {
		Random r = new Random();
		for (int i = 0; i < diceAmount; i++) {

			int curRoll = r.nextInt(6) + 1;
			dice.add(curRoll);
		}
		System.out.println(dice);
	}
}