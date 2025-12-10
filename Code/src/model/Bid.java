package model;

public class Bid {
	private final int amount;
    private final int face; // 1,2,3,4,5,6
    
    public Bid(int amount, int face) {
        if (face < 1 || face > 6) throw new IllegalArgumentException("face must be 1..6");
        if (amount < 1) throw new IllegalArgumentException("amount must be >=1");
        this.amount = amount;
        this.face = face;
    }

	public int getAmount() {
		return amount;
	}

	public int getFace() {
		return face;
	}

	public boolean isHigherThan(Bid other) {
        if (other == null) return true;
        if (this.amount > other.amount) return true;
        return this.amount == other.amount && this.face > other.face;
    }

    @Override
    public String toString() {
        return amount + " x " + face + (face == 1 ? " (wild)" : "");
    }
}
