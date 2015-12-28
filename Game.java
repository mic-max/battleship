/* 	Fixes
 * 
 * 	Typing spaces into input, outputs error multiple times
 * 	Input still works when calculating shot, not a major issue
 *	Sort ships by length
 * 
 * */

import java.util.Random;
import java.util.Scanner;

public class Game {
	
	static Random random = new Random(); //used for random number generating
	static Scanner scanner = new Scanner(System.in); // used for keyboard input
	
	static Grid grid;
	
	static boolean running = false; // whether the game is running or not
	static boolean debug = false; // change this to true for debug, no input mode
	static int turn = 1; // keeps track of how many turns the player has taken
	
	public static final void sleep(int ms) { // this method delays the game for the entered amount of milliseconds
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	// would have cleared the screen after each update
	//private static final void clear() {}
	
	private static final void welcome() { // welcome message to player with instructions
		//instructions, boats, story, title
		String instructions = "Briefing:\n"
				+ "   Make strategic shots into the enemies known location.\n"
				+ "   Enter coordinates into the control panel to shoot torpedos.\n"
				+ "   All of them must be sent to Davy Jones' Locker.\n";
		String story = "Story:\n"
				+ "   You are the commodore of a naval fleet.\n"
				+ "   Your patrol boat's radar has spotted nearby enemy vessels.\n"
				+ "   Sink them before they beat you to it.\n";
		String boatInfo = "Boats:\n   They have " + grid.ship.length + " boats:\n";
		String[] title = new String[5];
		title[0] = "   ___       __  __  __        __   _        _______  ___  ___";
		title[1] = "  / _ )___ _/ /_/ /_/ /__ ___ / /  (_)__    /_  / _ \\/ _ \\/ _ \\";
		title[2] = " / _  / _ `/ __/ __/ / -_|_-</ _ \\/ / _ \\    / / // / // / // /";
		title[3] = "/____/\\_,_/\\__/\\__/_/\\__/___/_//_/_/ .__/   /_/\\___/\\___/\\___/";
		title[4] = "                                  /_/";

		for (int i = 0; i < grid.ship.length; i++) { // string creation of the boats' lengths, names and displays
			boatInfo += "   ";
			for (int j = 0; j < grid.ship[i].length; j++) {
				boatInfo += grid.ship[i].display;
			}
			boatInfo += " - " + grid.ship[i].name + "\n";
		}
		
		for (int i = 0; i < title.length; i++) { // prints the battleship 7000 title
			System.out.println(title[i]);
		}
		System.out.println(instructions);
		System.out.println(story);
		System.out.println(boatInfo);
		
		System.out.print("Would you like to play? (Y/N): ");
		char play = scanner.next().toUpperCase().charAt(0);
		if (play == 'Y') { // if the first letter of the entered string is a y play, otherwise exit
			running = true;
			System.out.println("Game On!");
		} else {
			running = false;
			System.out.println("Game Over!");
		}
		
	}
	
	public static void main(String[] args) {
		// ask user for grid size?
		grid = new Grid(9, 9); //creates a playing grid taking width and height
		welcome();
		int target = 0;
		if (running) { // displays information to screen at the start of the game
			grid.render();
			grid.comms();
		}
		while (running) { // whenever the game is being player this loops
			if (debug) { // debuging mode will have the computer shoot every location from 0 to the end
				while (target < grid.cell.length) {
					if (running) {
						//grid.shoot(random.nextInt(grid.cell.length)); if wanted random shooting
						grid.shoot(target);
						target++;
						break;
					}
				}
			} else { // when game mode is set to normal
				String position = "!0"; // starting string that makes sure the current position is invalid
				while (!grid.aim(position)) { // whenever the target entered isn't valid user is asked to enter again
					if (position != "!0") {
						System.out.print("Invalid Launch Coordinates. Try Again: ");
					}
					position = scanner.next(); // stores the entered string
				}
				grid.shoot(grid.convertTarget(position)); // shoots at the location only after it has confirmed it is on the grid
			}
			grid.render(); // continuously drawing this to screen when game is running despite mode 
			grid.comms();
		}
	}
}