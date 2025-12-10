package model;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;


public class Cup {
	public int diceAmount;
	public ArrayList<Integer> dice = new ArrayList<Integer>();
	Random r = new Random();
	
	public Cup(int diceAmount) {
		this.diceAmount = diceAmount;
		this.dice = new ArrayList<>();
	}

	public void rollDice() {
		for (int i = 0; i < diceAmount; i++) {

			int curRoll = r.nextInt(6) + 1;
			dice.add(curRoll);
		}
		Collections.sort(dice);
		System.out.println(dice);
	}
	
	public ArrayList<Integer> getDiceValues() {
        return new ArrayList<>(dice);
    }

	public int countWithWilds(int face) {
        int count = 0;
        for (int v : dice) {
            if (v == face || v == 1) {
            	count++;
            }
        }
        return count;
    }
	
	public int detectLadderLength() {
        if (dice.isEmpty()) {
        	return 0;
        }
        // Unique sorted values
        ArrayList<Integer> uniq = new ArrayList<>();
        for (int v : dice) {
        	if (!uniq.contains(v)) uniq.add(v);
        }
        int best = 1;
        int current = 1;
        for (int i = 1; i < uniq.size(); i++) {
            if (uniq.get(i) == uniq.get(i - 1) + 1) {
                current++;
            } else {
                if (current > best) best = current;
                current = 1;
            }
        }
        if (current > best) best = current;
        // only ladders of length >= 3 count for bonus
        return best >= 3 ? best : 0;
    }

    @Override
    public String toString() {
        return getDiceValues().toString() + " (count=" + diceAmount + ")";
    }
	
	public int getDiceAmount() {
		return diceAmount;
	}

	public void setDiceAmount(int diceAmount) {
		this.diceAmount = diceAmount;
	}

	public ArrayList<Integer> getDice() {
		return dice;
	}

	public void setDice(ArrayList<Integer> dice) {
		this.dice = dice;
	}

	public void removeDie() {
		dice.remove(diceAmount);
		diceAmount--;
	}
}