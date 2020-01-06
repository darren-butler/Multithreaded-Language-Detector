package ie.gmit.sw;

import java.util.Scanner;

/**
 * Menu class responsible for handling most of the i/o for the application
 * @author Darren
 *
 */
public class Menu {
	private int option = 0;
	private Scanner console = new Scanner(System.in);
	private boolean keepGoing = true;

	public int getOption() {
		return option;
	}

	public boolean getKeepGoing() {
		return keepGoing;
	}

	public void setKeepGoing(boolean keepGoing) {
		this.keepGoing = keepGoing;
	}

	public int runMenu() {

		Menu.printHeader();
		Menu.printMenu();

		try {
			option = Integer.parseInt(console.next());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public String inputFile() {
		System.out.print("\tFile: ");
		String str = console.next();
		return str;
	}

	private static void printHeader() {
		System.out.println("\nText Language Detector!");
	}

	private static void printMenu() {
		System.out.println("1.Enter WiLI Dataset Location");
		System.out.println("2.Enter Query File Location");
		System.out.println("0.Quit");
		System.out.print(">");
	}
}
