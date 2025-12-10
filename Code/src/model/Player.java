package model;

public class Player {
    private final String name;
    private final boolean human;
    private final Cup cup;
    private boolean eliminated = false;
    private int points = 0; // points for shop / scoring

    public Player(String name, boolean human, int startingDice) {
        this.name = name;
        this.human = human;
        this.cup = new Cup(startingDice);
    }

    public String getName() {
        return name;
    }

    public boolean isHuman() {
        return human;
    }

    public Cup getHand() {
        return cup;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void eliminate() {
        eliminated = true;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int n) {
        points += n;
    }

    public void loseOneDie() {
        cup.removeDie();
        if (cup.getDiceAmount() <= 0) {
            eliminated = true;
        }
    }

    /*public void gainOneDie() {
        cup.addDie();
    }*/ // maybe not needed
}
