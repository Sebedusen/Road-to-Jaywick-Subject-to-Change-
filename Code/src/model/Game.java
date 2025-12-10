package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Main game logic implementing the gameplay loop.
 */
public class Game {
    private final List<Player> players = new ArrayList<>();
    private Bid currentBid;
    private int currentPlayerIndex = 0; // whose turn it is
    private final Random rng = new Random();
    private final Scanner scanner = new Scanner(System.in);

    public Game() {}

    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * Start and run the game loop until only one player remains (or user quits).
     */
    public void play() {
        // initial starter
        currentPlayerIndex = rng.nextInt(players.size());
        while (playersAliveCount() > 1) {
            startRound();
            playTurnsUntilChallenge();
            // after resolution, check eliminations and optionally shop - simplified here
            // ensure currentPlayerIndex points to loser of resolution (handled in resolveChallenge)
        }
        System.out.println("Game over! Winner:");
        for (Player p : players) if (!p.isEliminated()) System.out.println("- " + p.getName());
    }

    private int playersAliveCount() {
        int c = 0;
        for (Player p : players) if (!p.isEliminated()) c++;
        return c;
    }

    private void startRound() {
        System.out.println("\n--- NEW ROUND ---");
        currentBid = null;
        // roll for everyone alive
        for (Player p : players) {
            if (!p.isEliminated()) {
                p.getHand().rollDice();
                if (p.isHuman()) {
                    System.out.println("Your hand: " + p.getHand().getDiceValues());
                } else {
                    // hidden for AI
                }
            }
        }
        System.out.println("Starting player: " + players.get(currentPlayerIndex).getName());
    }

    private void playTurnsUntilChallenge() {
        boolean resolved = false;
        while (!resolved) {
            Player active = players.get(currentPlayerIndex);
            if (active.isEliminated()) {
                currentPlayerIndex = nextPlayerIndex(currentPlayerIndex);
                continue;
            }

            System.out.println("\nCurrent bid: " + (currentBid == null ? "None" : currentBid));
            System.out.println("It's " + active.getName() + "'s turn.");

            if (active.isHuman()) {
                resolved = humanTurn(active);
            } else {
                resolved = aiTurn(active);
            }

            if (!resolved) currentPlayerIndex = nextPlayerIndex(currentPlayerIndex);
        }
    }

    private boolean humanTurn(Player p) {
        while (true) {
            System.out.println("Your options: [b]id, [c]all bullshit, [s]how hand (debug), [q]uit game");
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("b")) {
                System.out.print("Enter amount: ");
                int amount = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter face (1-6): ");
                int face = Integer.parseInt(scanner.nextLine().trim());
                Bid bid = new Bid(amount, face);
                if (bid.isHigherThan(currentBid)) {
                    currentBid = bid;
                    System.out.println("You bid: " + bid);
                    return false;
                } else {
                    System.out.println("Invalid bid. Must be higher than current bid.");
                }
            } else if (line.equals("c")) {
                if (currentBid == null) {
                    System.out.println("No bid to call.");
                } else {
                    resolveChallenge(p);
                    return true;
                }
            } else if (line.equals("s")) {
                System.out.println("Your dice: " + p.getHand().getDiceValues());
            } else if (line.equals("q")) {
                System.out.println("Quitting game.");
                System.exit(0);
            } else {
                System.out.println("Unknown option.");
            }
        }
    }

    /**
     * Simple AI:
     * - If no bid exists, start with a reasonable bid based on its hand.
     * - If a bid exists, estimate total likelihood and either raise or call bullshit.
     */
    private boolean aiTurn(Player ai) {
        System.out.println("(AI) " + ai.getName() + " is thinking...");
        // compute simple heuristics
        int aliveDiceTotal = totalDiceInPlay();
        // choose a target face to bid on based on AI's own hand: pick the face it has most of (excluding 1s)
        int bestFace = 2;
        int bestCount = 0;
        for (int f = 2; f <= 6; f++) {
            int c = 0;
            for (int v : ai.getHand().getDiceValues()) if (v == f) c++;
            if (c > bestCount) {
                bestCount = c;
                bestFace = f;
            }
        }
        // incorporate wilds into ownCount
        int ownForBest = ai.getHand().countWithWilds(bestFace);

        if (currentBid == null) {
            // open with a conservative amount
            int amount = Math.max(1, Math.min(aliveDiceTotal, ownForBest + 1));
            currentBid = new Bid(amount, bestFace);
            System.out.println(ai.getName() + " bids " + currentBid);
            return false;
        } else {
            // estimate expected total for current bid face:
            int face = currentBid.getFace();
            // AI's own count for that face:
            int ownCount = ai.getHand().countWithWilds(face);
            // expected other dice showing that face = (otherDice * 1/6)
            int otherDice = aliveDiceTotal - ai.getHand().getDiceAmount();
            double expectedOthers = otherDice * (1.0 / 6.0) + (otherDice * (1.0 / 6.0)); // rough
            double estimate = ownCount + expectedOthers;
            // if estimate is well below bid, call bullshit with some probability
            if (estimate + 0.8 < currentBid.getAmount()) {
                System.out.println(ai.getName() + " calls bullshit on " + currentBid + "!");
                resolveChallenge(ai);
                return true;
            } else {
                // raise the bid modestly
                int newAmount = currentBid.getAmount();
                int newFace = currentBid.getFace();
                // try to increase face if possible
                if (newFace < 6 && rng.nextBoolean()) {
                    newFace++;
                } else {
                    newAmount++;
                }
                Bid next = new Bid(newAmount, newFace);
                // ensure it's valid - if not, bump amount
                if (!next.isHigherThan(currentBid)) {
                    next = new Bid(currentBid.getAmount() + 1, currentBid.getFace());
                }
                currentBid = next;
                System.out.println(ai.getName() + " bids " + currentBid);
                return false;
            }
        }
    }

    /**
     * Resolve a bullshit call made by caller. Determines loser and updates dice.
     * The caller is the player who called bullshit. The bidder is the previous player.
     */
    private void resolveChallenge(Player caller) {
        // find the bidder (the player immediately before caller who made last bid)
        int bidderIndex = previousPlayerIndex(currentPlayerIndex);
        Player bidder = players.get(bidderIndex);

        System.out.println("\n--- CHALLENGE: reveal all hands ---");
        int totalMatching = 0;
        for (Player p : players) {
            if (p.isEliminated()) continue;
            int ladderBonus = p.getHand().detectLadderLength(); // simple ladder bonus
            int count = p.getHand().countWithWilds(currentBid.getFace());
            System.out.println(p.getName() + ": " + p.getHand().getDiceValues() +
                    "  match=" + count + " ladderBonus=" + ladderBonus);
            totalMatching += count + ladderBonus;
        }
        System.out.println("Total matching (including ladders/wilds): " + totalMatching);
        System.out.println("Bid was: " + currentBid);

        if (totalMatching >= currentBid.getAmount()) {
            // bid was true -> caller loses a die
            System.out.println("Bid is valid. " + caller.getName() + " loses a die.");
            caller.loseOneDie();
            currentPlayerIndex = indexOf(caller); // loser starts next round
        } else {
            // bid false -> bidder loses a die
            System.out.println("Bid is NOT valid. " + bidder.getName() + " loses a die.");
            bidder.loseOneDie();
            currentPlayerIndex = indexOf(bidder); // loser starts next round
        }

        // announce eliminations
        for (Player p : players) {
            if (p.isEliminated()) {
                System.out.println(p.getName() + " has been eliminated!");
            } else {
                System.out.println(p.getName() + " now has " + p.getHand().getDiceAmount() + " dice.");
            }
        }

        // small point reward for eliminating someone (not fully used here)
        List<Player> alive = new ArrayList<>();
        for (Player p : players) if (!p.isEliminated()) alive.add(p);
        if (playersAliveCount() <= 1) {
            // game will end soon
        }
    }

    private int nextPlayerIndex(int idx) {
        int start = idx;
        do {
            idx = (idx + 1) % players.size();
            if (!players.get(idx).isEliminated()) return idx;
        } while (idx != start);
        return idx;
    }

    private int previousPlayerIndex(int idx) {
        int start = idx;
        do {
            idx = (idx - 1 + players.size()) % players.size();
            if (!players.get(idx).isEliminated()) return idx;
        } while (idx != start);
        return idx;
    }

    private int indexOf(Player p) {
        return players.indexOf(p);
    }

    private int totalDiceInPlay() {
        int total = 0;
        for (Player p : players) if (!p.isEliminated()) total += p.getHand().getDiceAmount();
        return total;
    }
}
