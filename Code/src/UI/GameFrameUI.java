package UI;

import Logic.Game;
import javax.swing.*;
import java.awt.*;

public class GameFrameUI extends JFrame {

	private JTextArea logArea;
	private JButton rollButton;
	private JButton guessButton;
	private JButton bullshitButton;
	private Game game;

	public GameFrameUI() {
		super("Viborg Roguelite");

		game = new Game(this);

		setLayout(new BorderLayout());
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// --- Text Log ---
		logArea = new JTextArea();
		logArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(logArea);
		add(scroll, BorderLayout.CENTER);

		// --- Buttons ---
		JPanel buttonPanel = new JPanel();
		rollButton = new JButton("Roll Dice");
		guessButton = new JButton("Make Guess");
		bullshitButton = new JButton("Call Bullshit");

		buttonPanel.add(rollButton);
		buttonPanel.add(guessButton);
		buttonPanel.add(bullshitButton);

		add(buttonPanel, BorderLayout.SOUTH);

		// --- Button Listeners ---
		rollButton.addActionListener(e -> game.playerRolls());
		guessButton.addActionListener(e -> game.playerMakesGuess());
		bullshitButton.addActionListener(e -> game.playerCallsBullshit());

		append("Welcome to Viborg!\nPress 'Roll Dice' to start.\n");

		setVisible(true);
	}

	public void append(String msg) {
		logArea.append(msg + "\n");
	}
}
