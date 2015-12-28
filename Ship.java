public class Ship {
	
	public String name;
	public int length;
	public char display;
	public boolean afloat;
	
	public Ship(String name, int length, char display, boolean afloat) {
		this.name = name;
		this.length = length;
		this.display = display;
		this.afloat = afloat;
		if (Game.debug) {
			System.out.println("Ship Created - " + name);
		}
	}
}