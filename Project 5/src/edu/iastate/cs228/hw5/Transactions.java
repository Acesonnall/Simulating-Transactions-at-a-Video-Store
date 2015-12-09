package edu.iastate.cs228.hw5;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *  
 * @author Omar Taylor
 *
 */

/**
 * 
 * The Transactions class simulates video transactions at a video store.
 *
 */
public class Transactions {

	/**
	 * The main method generates a simulation of rental and return activities.
	 * 
	 * @param args
	 * @throws AllCopiesRentedOutException
	 * @throws FilmNotInInventoryException
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException,
			FilmNotInInventoryException, AllCopiesRentedOutException {
		initialize();
		begin();
	}

	private static void initialize() {
		System.out.println(
				"Transactions at a Video Store\nkeys:\t1 (rent)\t2 (bulk rent) \n \t3 (return)\t4 (bulk return) \n \t5 (summary)\t6 (exit)\n");
	}

	private static void begin() throws FileNotFoundException, IllegalArgumentException, FilmNotInInventoryException,
			AllCopiesRentedOutException {
		VideoStore vs = new VideoStore("videoList1.txt");
		int key = 0;
		boolean exit = false;
		Scanner scan = new Scanner(System.in);

		while (!exit) {
			System.out.println("Transaction: ");
			key = Integer.parseInt(scan.nextLine());
			switch (key) {
			case 1:
				rent(scan, vs);
				break;
			case 2:
				bulk_rent(vs);
				break;
			case 3:
				ret_urn(scan, vs);
				break;
			case 4:
				bulk_return(scan, vs);
				break;
			case 5:
				summary(vs);
				break;
			default:
				scan.close();
				exit();
			}
		}
	}

	private static void rent(Scanner scan, VideoStore vs)
			throws IllegalArgumentException, FilmNotInInventoryException, AllCopiesRentedOutException {
		System.out.println("Film to rent: ");
		try {
			String filmToRent = scan.nextLine();
			vs.videoRent(VideoStore.parseFilmName(filmToRent), VideoStore.parseNumCopies(filmToRent));
		} catch (IllegalArgumentException | FilmNotInInventoryException | AllCopiesRentedOutException e) {
			System.out.println(e.getMessage());
		}
		System.out.println();
	}

	private static void bulk_rent(VideoStore vs) throws FileNotFoundException, IllegalArgumentException,
			FilmNotInInventoryException, AllCopiesRentedOutException {
		System.out.println("Video file (rent): ");
		try {
			vs.bulkRent(new Scanner(System.in).next());
		} catch (IllegalArgumentException | FilmNotInInventoryException | AllCopiesRentedOutException e) {
			System.out.println(e.getMessage());
		}
		System.out.println();
	}

	private static void ret_urn(Scanner scan, VideoStore vs)
			throws IllegalArgumentException, FilmNotInInventoryException, AllCopiesRentedOutException {
		System.out.println("Film to return: ");
		try {
			String filmToReturn = scan.nextLine();
			vs.videoRent(VideoStore.parseFilmName(filmToReturn), VideoStore.parseNumCopies(filmToReturn));
		} catch (IllegalArgumentException | FilmNotInInventoryException | AllCopiesRentedOutException e) {
			System.out.println(e.getMessage());
		}
		System.out.println();
	}

	private static void bulk_return(Scanner scan, VideoStore vs)
			throws FileNotFoundException, IllegalArgumentException, FilmNotInInventoryException {
		System.out.println("Video file (return): ");
		try {
			vs.bulkReturn(new Scanner(System.in).next());
		} catch (IllegalArgumentException | FilmNotInInventoryException e) {
			System.out.println(e.getMessage());
		}
		System.out.println();
	}

	private static void summary(VideoStore vs) {
		System.out.println(vs.transactionsSummary());
		System.out.println();
	}

	private static void exit() {
		System.exit(0);
	}
}
