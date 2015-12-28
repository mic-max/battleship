//import java.util.Comparator;

public class Grid{ //implements Comparable<Ship>
	
	public static final char WATER = ' ';
	public static final char MISS = '.';
	public static final char HIT = 'X';

	public static String message;
	public static String instruction = " - Enter torpedo launch coordinates: ";
	
	public int width, height;
	public char[] cell;
	public Ship[] ship = { // holds all the ships in the game
		new Ship("Aircraft Carrier", 5, 'A', false),
		new Ship("Battleship", 4, 'B', false),
		new Ship("Submarine", 3, 'S', false),
		new Ship("Destroyer", 3, 'D', false),
		new Ship("Patrol Boat", 2, 'P', false)
		//new Ship("Cruiser", 2, 'C', false)
	};
	
	public Grid(int width, int height) { // grid constructor
		this.width = width;
		this.height = height;
		cell = new char[width * height]; // sets cell to the size of the grid
		placeShips(); // auto places ships
		if (Game.debug) { // debug notice
			System.out.println("Grid Created - " + width + " by " + height);
		}
	}
	
	// sorting objects with comparator attempt
	/*public int compareTo(Ship o) {
		int compareLength = ((Ship) o).length;
		return o.length - compareLength;
	}
	
	public static Comparator<Ship> shipLengthComparator = new Comparator<Ship>() {
		public int compare(Ship s1, Ship s2) {
			int length1 = s1.length;
			int length2 = s2.length;
			return length1.compareTo(length2);
		}
	};*/
	
	private void empty() { // sets the entire cell array to the water character
		for (int i = 0; i < cell.length; i++) {
			cell[i] = WATER;
		}
	}
	
	private String generatePad() { // generates the padding that looks like ___|___|___| on every 2nd row of rendered grid
		String pad = "";
		for (int x = 0; x < width + 1; x++) {
			for (int i = 0; i < (int) Math.log10(x - 1); i++) {
				pad += "_";
			}
			pad += "___|";
		}
		return pad;
	}
	
	public void render() { // renders the grid to the console
		char row = 'A';
		String view = "\n\n   |";
		final String pad = generatePad(); //stores the pad in this final, so it's only made once
		for (int i = 0; i < width; i++) { //prints column numbers
			view += " " + i + " |";
		}
		view += "\n" + pad + "\n";
		for (int y = 0; y < height; y++) {
			view += " " + row++ + " |";
			for (int x = 0; x < width; x++) {
				if (cell[x + y * width] == MISS || cell[x + y * width] == HIT) { // if it's not a boat print actual data
					view += " " + cell[x + y * width];
				} else {
					view += " " + WATER; // if it is a boat print water since the player shouldn't be able to see it
				}
				for (int k = 0; k < (int) Math.log10(x); k++) { // increases the box width when the column number exceeds single digits
					view += " ";
				}
				view += " |";
			}
			view += "\n" + pad + "\n";
		}
		System.out.println(view); // finally printing it to the screen
	}
	
	public void comms() { // method that notifies player of previous shots and servers instructions
		if (message != null) {
			System.out.println(message);
		}
		if (Game.running) {
			System.out.print(Game.turn++ + instruction);
		}
	}
	
	private Ship getHit(int target) { // this returns the ship in the target location of the cell array
		for (int i = 0; i < ship.length; i++) {
			if (cell[target] == ship[i].display) {
				return ship[i];
			}
		}
		return null;
	}
	
	private boolean checkSunk(Ship s) { // this checks if a ship has sunk by searching the whole grid for it's display character
		for (int i = 0; i < cell.length; i++) {
			if (cell[i] == s.display) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkWin() { // checks if all boats have been sunk
		for (int i = 0; i < ship.length; i++) {
			if (ship[i].afloat) {
				return false;
			}
		}
		return true;
	}
	
	public int convertTarget(String input) { // converts input example: a5 into a coordinate on the grid --> 4
		//turns input into a number
		if (input.length() < 2) { // do error checking in this if else statement
			return -1; // returns -1 because it's guarenteed to be outside of the grid and won't matter
		} else {
			int xFire = getXFire(input);
			int yFire = getYFire(input);
			int target = xFire + yFire * width;
			return target;
		}
	}
	
	private int getYFire(String input) {
		return input.toUpperCase().charAt(0) - 65; //removes 65 because that is the decimal unicode for capital 'A'
	}
	
	private int getXFire(String input) {
		try {
			return Integer.parseInt(input.substring(1).toLowerCase()); //returns the integer value of everything entered after the first character
		} catch(NumberFormatException nfe) {
			return -1;
		}
	}
	
	public boolean aim(String input) { // checks if shot doesn't make sense example: negative coordinates or coordinates greater than width/height
		int xFire = getXFire(input);
		int yFire = getYFire(input);
		int target = convertTarget(input);
		if (xFire < 0 || xFire >= width || yFire < 0 || yFire >= height || target < yFire * width || target > (yFire + 1) * width + 1) {
			return false;
		} else {
			return true;
		}
	}
	
	private void shooting() { // method to add suspense to the shot
		if (Game.debug) {
			System.out.println(); // includes an extra empty line not missing in debug due to player not hitting enter key after input
		}
		System.out.print("Calculating torpedo's launch trajectory ");
		for (int i = 0; i < 3; i++) {
			Game.sleep(200);
			System.out.print(" .");
		}
		Game.sleep(200);
	}
	
	public void shoot(int target) { // shoots the entered target on the grid
		shooting();
		Ship s = getHit(target);
		switch(cell[target]) { // displays message depending on what was in the location you shot at
		case MISS:
			message = "You already shot there and missed. Don't waste torpedos.";
			break;
		case HIT:
			message = "You already hit that location. Stop wasting torpedos.";
			break;
		case WATER:
			message = "Miss.";
			cell[target] = MISS;
			break;
		default: // hitting a ship, since the only other possible grid values were already called above
			cell[target] = HIT;
			if (checkSunk(s)) { // checks if boat is sunk
				s.afloat = false;
				message = "You sunk their " + s.name + "!";
				if (checkWin()) { // only checks if game is one if last move was a hit and sink since it's nested
					message += "\nCongratulations! You sank the entire enemy fleet.";
					Game.running = false; // ends the game
				}
			} else {
				message = "You hit their " + s.name + "."; // if it wasn't a sinking hit, may remove name from this message...
			}
		}
	}

	private void placeShips() {// places the ships on the grid randomly and prevents them from overlapping, splitting into two rows, going of of bounds...
		empty();
		//sort ships by length Arrays.sort(ship
		for (int s = 0; s < ship.length; s++) {
			while (!ship[s].afloat) {
				boolean empty = true;
				int p = Game.random.nextInt(cell.length); // random number within grid's bounds
				int d = Game.random.nextInt(2); // horizontal or vertical
				int v = Game.random.nextInt(2); // negative or positive
				if (v == 0) {
					v--;
				}
				switch (d) {
				case 0: // Horizontal
					if (p + ship[s].length * v < 0 || p + ship[s].length * v >= cell.length || p / width != (p + (ship[s].length - 1) * v) / width) {
						break;
					} else {
						for (int i = 0; i < ship[s].length; i++) { // check if another ship is in any of the locations
							if (cell[p + i * v] != WATER) {
								empty = false;
								break;
							}
						}
						if (empty) {
							for (int i = 0; i < ship[s].length; i++) { // setting the ships location on grid
								cell[p + i * v] = ship[s].display;
							}
							ship[s].afloat = true;
							break;
						} else {
							break;
						}
					}
				case 1: // Vertical
					if (p + (ship[s].length - 1) * v * width < 0 || p + (ship[s].length - 1) * v * width >= cell.length) {
						break;
					} else {
						for (int i = 0; i < ship[s].length * width; i += width) { // check if another ship is in any of the locations
							if (cell[p + i * v] != WATER) {
								empty = false;
								break;
							}
						}
						if (empty) {
							for (int i = 0; i < ship[s].length * width; i += width) { // setting the ships location on grid
								cell[p + i * v] = ship[s].display;
							}
							ship[s].afloat = true;
							break;
						} else {
							break;
						}
					}
				default:
					break;
				}
			}
		}
	}
}