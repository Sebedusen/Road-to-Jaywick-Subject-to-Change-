package UI;

import model.Game;
import model.Player;

import java.util.Scanner;

/**
 * Simple launcher / UI for the console game.
 */
public class Test {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Welcome to Viborg (console prototype)!");
        System.out.print("Enter your name: ");
        String name = s.nextLine().trim();
        Game game = new Game();

        // create players: 1 human + 3 AIs
        Player human = new Player(name.isEmpty() ? "You" : name, true, 4);
        game.addPlayer(human);
        game.addPlayer(new Player("AI-1", false, 4));
        game.addPlayer(new Player("AI-2", false, 4));
        game.addPlayer(new Player("AI-3", false, 4));

        game.play();
    }
}
