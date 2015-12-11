package edu.iastate.cs228.hw5;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * 
 * @author Omar Taylor
 *
 */

public class VideoStore {
	protected SplayTree<Video> inventory; // all the videos at the store

	// ------------
	// Constructors
	// ------------

	/**
	 * Default constructor sets inventory to an empty tree.
	 */
	VideoStore() {
		this.inventory = new SplayTree<Video>();
	}

	/**
	 * Constructor accepts a video file to create its inventory. Refer to
	 * Section 3.2 of the project description for details regarding the format
	 * of a video file.
	 * 
	 * The construtor works in two steps:
	 * 
	 * 1. Calls the default constructor. 2. Has the splay tree inventory call
	 * its method addBST() to add videos to the tree.
	 * 
	 * @param videoFile
	 *            no format checking on the file
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             if the number of copies of any film in videoFile is <= 0
	 */
	VideoStore(String videoFile) throws FileNotFoundException, IllegalArgumentException {
		this();
		setUpInventory(videoFile);
	}

	/**
	 * Accepts a video file to initialize the splay tree inventory. To be
	 * efficient, add videos to the inventory by calling the addBST() method,
	 * which does not splay.
	 * 
	 * Refer to Section 3.2 for the format of video file.
	 * 
	 * @param videoFile
	 *            correctly formated if exists
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             if the number of copies of any film in videoFile is <= 0
	 */
	public void setUpInventory(String videoFile) throws FileNotFoundException, IllegalArgumentException {
		bulkImport(videoFile);
	}

	// ------------------
	// Inventory Addition
	// ------------------

	/**
	 * Find a Video object by film title.
	 * 
	 * @param film
	 * @return
	 */
	public Video findVideo(String film) {
		return inventory.findElement(new Video(film));
	}

	/**
	 * Updates the splay tree inventory by adding a given number of video copies
	 * of the film. (Splaying is justified as new videos are more likely to be
	 * rented.)
	 * 
	 * Calls the add() method of SplayTree to add the video object. If true is
	 * returned, the film was not on the inventory before, and has been added.
	 * If false is returned, the film is already on the inventory. The root of
	 * the splay tree must store the corresponding Video object for the film.
	 * Calls findElement() of the SplayTree class to get this Video object,
	 * which then calls getNumCopies() and addNumCopies() of the Video class to
	 * increase the number of copies of the corresponding film by n
	 * 
	 * @param film
	 *            title of the film
	 * @param n
	 *            number of video copies
	 * @throws IllegalArgumentException
	 *             if n <= 0
	 */
	public void addVideo(String film, int n) throws IllegalArgumentException {
		if (n <= 0) {
			throw new IllegalArgumentException();
		}
		Video f = new Video(film, n);
		if (this.inventory.add(f) == false) {
			this.inventory.findElement(f).addNumCopies(n);
		}
	}

	/**
	 * Add one video copy of the film.
	 * 
	 * @param film
	 *            title of the film
	 */
	public void addVideo(String film) {
		addVideo(film, 1);
	}

	/**
	 * Update the splay trees inventory.
	 * 
	 * The videoFile format is given in Section 3.2 of the project description.
	 * 
	 * @param videoFile
	 *            correctly formated if exists
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             if the number of copies of any film in videoFile is <= 0
	 */
	public void bulkImport(String videoFile) throws FileNotFoundException, IllegalArgumentException {
		Scanner scan = new Scanner(new File(videoFile));

		while (scan.hasNextLine()) {
			String temp = scan.nextLine();
			inventory.addBST(new Video(parseFilmName(temp), parseNumCopies(temp)));
		}
		scan.close();
	}

	// ----------------------------
	// Video Query, Rental & Return
	// ----------------------------

	/**
	 * Search the splay tree inventory to determine if a video is available.
	 * 
	 * @param film
	 * @return true if available
	 */
	public boolean available(String film) {
		Video v = this.inventory.findElement(new Video(film));
		return v != null ? v.getNumAvailableCopies() > 0 : false;
	}

	/**
	 * Update inventory.
	 * 
	 * Search if the film is in inventory by calling findElement(new Video(film,
	 * 1)).
	 * 
	 * If the film is not in inventory, prints the message "Film <film> is not
	 * in inventory", where <film> shall be replaced with the string that is the
	 * value of the parameter film. If the film is in inventory with no copy
	 * left, prints the message "Film <film> has been rented out".
	 * 
	 * If there is at least one available copy but n is greater than the number
	 * of such copies, rent all available copies. In this case, no
	 * AllCopiesRentedOutException is thrown.
	 * 
	 * @param film
	 * @param n
	 * @throws IllegalArgumentException
	 *             if n <= 0
	 * @throws FilmNotInInventoryException
	 *             if film is not in the inventory
	 * @throws AllCopiesRentedOutException
	 *             if there is zero available copy for the film.
	 */
	public void videoRent(String film, int n)
			throws IllegalArgumentException, FilmNotInInventoryException, AllCopiesRentedOutException {
		Video v = this.inventory.findElement(new Video(film, 1));
		if (n <= 0) {
			throw new IllegalArgumentException("Film " + film + " has an invalid request");
		} else if (v == null) {
			throw new FilmNotInInventoryException("Film " + film + " is not in inventory");
		} else if (v.getNumAvailableCopies() == 0) {
			throw new AllCopiesRentedOutException("Film " + film + " has been rented out");
		} else {
			v.rentCopies(n);
		}
	}

	/**
	 * Update inventory.
	 * 
	 * 1. Calls videoRent() repeatedly for every video listed in the file. 2.
	 * For each requested video, do the following: a) If it is not in inventory
	 * or is rented out, an exception will be thrown from rent(). Based on the
	 * exception, prints out the following message:
	 * "Film <film> is not in inventory" or "Film <film> has been rented out."
	 * In the message, <film> shall be replaced with the name of the video. b)
	 * Otherwise, update the video record in the inventory.
	 * 
	 * @param videoFile
	 *            correctly formatted if exists
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             if the number of copies of any film is <= 0
	 * @throws FilmNotInInventoryException
	 *             if any film from the videoFile is not in the inventory
	 * @throws AllCopiesRentedOutException
	 *             if there is zero available copy for some film in videoFile
	 */
	public void bulkRent(String videoFile) throws FileNotFoundException, IllegalArgumentException,
			FilmNotInInventoryException, AllCopiesRentedOutException {
		Scanner scan = new Scanner(new File(videoFile));
		boolean[] bs = new boolean[3];
		String s = "";

		while (scan.hasNextLine()) {
			String temp = scan.nextLine();
			try {
				this.videoRent(parseFilmName(temp), parseNumCopies(temp));
			} catch (IllegalArgumentException e) {
				s += e.getMessage() + "\n";
				bs[0] = true;
			} catch (FilmNotInInventoryException e) {
				s += e.getMessage() + "\n";
				bs[1] = true;
			} catch (AllCopiesRentedOutException e) {
				s += e.getMessage() + "\n";
				bs[2] = true;
			}
		}
		if (bs[0]) {
			scan.close();
			throw new IllegalArgumentException(s.trim());
		}
		if (bs[1]) {
			scan.close();
			throw new FilmNotInInventoryException(s.trim());
		}
		if (bs[2]) {
			scan.close();
			throw new AllCopiesRentedOutException(s.trim());
		}
		scan.close();
	}

	/**
	 * Update inventory.
	 * 
	 * If n exceeds the number of rented video copies, accepts up to that number
	 * of rented copies while ignoring the extra copies.
	 * 
	 * @param film
	 * @param n
	 * @throws IllegalArgumentException
	 *             if n <= 0
	 * @throws FilmNotInInventoryException
	 *             if film is not in the inventory
	 */
	public void videoReturn(String film, int n) throws IllegalArgumentException, FilmNotInInventoryException {
		Video v = this.inventory.findElement(new Video(film, 1));
		if (n <= 0) {
			throw new IllegalArgumentException("Film " + film + " has an invalid request");
		} else if (v == null) {
			throw new FilmNotInInventoryException("Film " + film + " is not in inventory");
		} else {
			v.returnCopies(n);
		}
	}

	/**
	 * Update inventory.
	 * 
	 * Handles excessive returned copies of a film in the same way as
	 * videoReturn() does.
	 * 
	 * @param videoFile
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             if the number of return copies of any film is <= 0
	 * @throws FilmNotInInventoryException
	 *             if a film from videoFile is not in inventory
	 */
	public void bulkReturn(String videoFile)
			throws FileNotFoundException, IllegalArgumentException, FilmNotInInventoryException {
		Scanner scan = new Scanner(new File(videoFile));
		boolean[] bs = new boolean[2];
		String s = "";

		while (scan.hasNextLine()) {
			String temp = scan.nextLine();
			try {
				this.videoReturn(parseFilmName(temp), parseNumCopies(temp));
			} catch (IllegalArgumentException e) {
				s += e.getMessage() + "\n";
				bs[0] = true;
			} catch (FilmNotInInventoryException e) {
				s += e.getMessage() + "\n";
				bs[1] = true;
			}
		}
		scan.close();
		if (bs[0]) {
			throw new IllegalArgumentException(s.trim());
		}
		if (bs[1]) {
			throw new FilmNotInInventoryException(s.trim());
		}
	}

	// ------------------------
	// Methods with No Splaying
	// ------------------------

	/**
	 * Performs inorder traveral on the splay tree inventory to list all the
	 * videos by film title, whether rented or not. Below is a sample string if
	 * printed out:
	 * 
	 * 
	 * Films in inventory:
	 * 
	 * A Streetcar Named Desire (1) Brokeback Mountain (1) Forrest Gump (1)
	 * Psycho (1) Singin' in the Rain (2) Slumdog Millionaire (5) Taxi Driver
	 * (1) The Godfather (1)
	 * 
	 * 
	 * @return
	 */
	public String inventoryList() {
		String s = "Films in inventory:\n\n";
		for (Video v : this.inventory) {
			s += v.getFilm() + " " + "(" + v.getNumCopies() + ")\n";
		}
		return s;
	}

	/**
	 * Calls rentedVideosList() and unrentedVideosList() sequentially. For the
	 * string format, see Transaction 5 in the sample simulation in Section 4 of
	 * the project description.
	 * 
	 * @return
	 */
	public String transactionsSummary() {

		return rentedVideosList() + "\n" + unrentedVideosList();
	}

	/**
	 * Performs inorder traversal on the splay tree inventory. Use a splay tree
	 * iterator.
	 * 
	 * Below is a sample return string when printed out:
	 * 
	 * 
	 * Rented films:
	 * 
	 * Brokeback Mountain (1) Singin' in the Rain (2) Slumdog Millionaire (1)
	 * The Godfather (1)
	 * 
	 * 
	 * @return
	 */
	private String rentedVideosList() {
		String s = "Rented films:\n\n";
		for (Video v : this.inventory) {
			if (v.getNumRentedCopies() > 0)
				s += v.getFilm() + " " + "(" + v.getNumRentedCopies() + ")\n";
		}
		return s;
	}

	/**
	 * Performs inorder traversal on the splay tree inventory. Use a splay tree
	 * iterator. Prints only the films that have unrented copies.
	 * 
	 * Below is a sample return string when printed out:
	 * 
	 * 
	 * Films remaining in inventory:
	 * 
	 * A Streetcar Named Desire (1) Forrest Gump (1) Psycho (1) Slumdog
	 * Millionaire (4) Taxi Driver (1)
	 * 
	 * 
	 * @return
	 */
	private String unrentedVideosList() {
		String s = "Films remaining in inventory:\n\n";
		for (Video v : this.inventory) {
			if (v.getNumAvailableCopies() != 0)
				s += v.getFilm() + " " + "(" + v.getNumAvailableCopies() + ")\n";
		}
		return s;
	}

	/**
	 * Parse the film name from an input line.
	 * 
	 * @param line
	 * @return
	 */
	public static String parseFilmName(String line) {
		String temp = line.substring(line.lastIndexOf(" ")).trim();
		return line = temp.equals("(1)") || parseNumCopies(line) != 1 ? line.replace(temp, "").trim() : line;
	}

	/**
	 * Parse the number of copies from an input line.
	 * 
	 * @param line
	 * @return
	 */
	public static int parseNumCopies(String line) {
		String temp = line.substring(line.lastIndexOf(" ")).trim();
		boolean checker = false;

		if (temp.length() <= 2) {
			return 1;
		}
		checker = temp.charAt(0) == '(' ? true : false;
		checker = temp.charAt(temp.length() - 1) == ')' ? true : false;

		while (checker == true) {
			String possibleN = "";

			for (int i = 1; i < temp.length() - 1; i++)
				if (temp.charAt(i) >= '0' && temp.charAt(i) <= '9'
						|| (temp.charAt(i) == '-' && (temp.charAt(i + 1) >= '0' && temp.charAt(i + 1) <= '9'))) {
					possibleN += temp.charAt(i);
				} else {
					checker = false;
					break;
				}

			if (checker = true && !possibleN.isEmpty()) {
				return Integer.parseInt(possibleN);
			}
		}
		return 1;
	}
}
