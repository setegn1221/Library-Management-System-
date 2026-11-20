package librarymanagementsystem13;

import java.util.ArrayList; // Used for dynamic lists
import java.util.List; // Interface for list collections
import java.util.Scanner; // Used for reading user input from the console

// Represents a single book in the library
class Book {
    private final String title; // The title of the book (cannot be changed after creation)
    private final String isbn; // The ISBN of the book (unique identifier, cannot be changed)
    private boolean isAvaila  ble; // Tracks if the book is currently available for borrowing

    // Constructor to create a new Book object
    public Book(String title, String isbn) {
        this.title = title; // Initialize title
        this.isbn = isbn;   // Initialize isbn
        this.isAvailable = true; // New books are available by default
    }

    // Getter method for title
    public String getTitle() { return title; }
    // Getter method for isbn
    public String getIsbn() { return isbn; }
    // Getter method to check availability status
    public boolean isAvailable() { return isAvailable; }

    // Method to mark the book as borrowed
    public void borrowBook() {
        if (isAvailable) { // Check if the book is available before borrowing
            isAvailable = false; // Mark as unavailable
            System.out.println("\"" + title + "\" borrowed successfully."); // Confirmation message
        } else {
            System.out.println("\"" + title + "\" is currently unavailable."); // Message if already borrowed
        }
    }

    // Method to mark the book as returned
    public void returnBook() {
        if (!isAvailable) { // Check if the book was actually borrowed
            isAvailable = true; // Mark as available
            System.out.println("\"" + title + "\" returned successfully."); // Confirmation message
        } else {
            System.out.println("\"" + title + "\" was not borrowed."); // Message if not borrowed
        }
    }

    // Method to display the book's information
    public void displayInfo() {
        System.out.println("Title: " + title + ", ISBN: " + isbn + ", Available: " + isAvailable);
    }
}

// Abstract base class for all users of the system
abstract class User {
    private final String userID; // Unique identifier for the user
    private final String name; // The name of the user

    // Constructor for User
    public User(String userID, String name) {
        this.userID = userID; // Initialize user ID
        this.name = name;     // Initialize name
    }

    // Getter method for user ID
    public String getUserID() { return userID; }
    // Getter method for name
    public String getName() { return name; }

    // Abstract method that must be implemented by subclasses (Librarian, Member)
    // Defines the specific actions a user type can perform
    public abstract void performAction(Library library, Scanner scanner);
}

// Represents a Librarian, who manages the library's collection
class Librarian extends User {
    // Constructor for Librarian, calls the parent (User) constructor
    public Librarian(String userID, String name) {
        super(userID, name);
    }

    // Method for Librarian to add a new book to the library
    public void addBook(Library library, Scanner scanner) {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine();
        Book newBook = new Book(title, isbn); // Create a new Book object
        library.addBook(newBook); // Add the book to the Library's collection
    }

    // Method for Librarian to remove a book from the library by ISBN
    public void removeBook(Library library, Scanner scanner) {
        System.out.print("Enter ISBN of the book to remove: ");
        String isbnToRemove = scanner.nextLine();
        library.removeBook(isbnToRemove); // Call the Library's removeBook method
    }

    // Implementation of the abstract performAction for Librarian
    @Override
    public void performAction(Library library, Scanner scanner) {
        // This loop keeps the librarian in their specific menu until they choose to go back
        while (true) {
            System.out.println("\nLibrarian Actions:");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. List All Books");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine(); // Read librarian's choice

            // Handle librarian's choice using a switch statement
            switch (choice) {
                case "1":
                    addBook(library, scanner); // Call addBook method
                    break;
                case "2":
                    removeBook(library, scanner); // Call removeBook method
                    break;
                case "3":
                    library.listAllBooks(); // Call the Library's listAllBooks method
                    break;
                case "4":
                    System.out.println("Returning to Main Menu.");
                    return; // Exit the performAction method, going back to the main loop
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}

// Represents a Member, who can borrow and return books
class Member extends User {
    private final List<Book> borrowedBooks; // List to keep track of books borrowed by this member

    // Constructor for Member, calls the parent (User) constructor
    public Member(String userID, String name) {
        super(userID, name);
        this.borrowedBooks = new ArrayList<>(); // Initialize the list of borrowed books
    }

    // Method for Member to borrow a book
    public void borrowBook(Library library, Scanner scanner) {
        System.out.print("Enter ISBN of the book to borrow: ");
        String isbnToBorrow = scanner.nextLine();
        // Search for the book in the main library collection
        Book book = library.searchBook(isbnToBorrow);
        if (book != null) { // Check if the book was found
            if (book.isAvailable()) { // Check if the found book is available
                book.borrowBook(); // Call the Book's borrow method (marks it unavailable in the library)
                borrowedBooks.add(book); // Add the book to the member's borrowed list
            }
            // Book is found but not available - message handled by Book.borrowBook()
        } else {
            System.out.println("Book with ISBN " + isbnToBorrow + " not found."); // Message if book not in library
        }
    }

    // Method for Member to return a book
    public void returnBook(Library library, Scanner scanner) {
        System.out.print("Enter ISBN of the book to return: ");
        String isbnToReturn = scanner.nextLine();
        Book bookToReturn = null;
        // Search the member's *borrowed* list for the book
        for (Book book : borrowedBooks) {
            if (book.getIsbn().equals(isbnToReturn)) {
                bookToReturn = book; // Found the book in the borrowed list
                break;
            }
        }
        if (bookToReturn != null) { // Check if the book was found in the member's borrowed list
            bookToReturn.returnBook(); // Call the Book's return method (marks it available in the library)
            borrowedBooks.remove(bookToReturn); // Remove the book from the member's borrowed list
        } else {
            System.out.println("You did not borrow a book with ISBN " + isbnToReturn + "."); // Message if book not in member's list
        }
    }

    // Implementation of the abstract performAction for Member
    @Override
    public void performAction(Library library, Scanner scanner) {
        // This loop keeps the member in their specific menu until they choose to go back
        while (true) {
            System.out.println("\nMember Actions:");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. Search Book"); // Members can search all books in the library
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine(); // Read member's choice

            // Handle member's choice using a switch statement
            switch (choice) {
                case "1":
                    borrowBook(library, scanner); // Call borrowBook method
                    break;
                case "2":
                    returnBook(library, scanner); // Call returnBook method
                    break;
                case "3":
                    System.out.print("Enter title or ISBN to search: ");
                    String query = scanner.nextLine();
                    // Call the Library's searchBook method
                    Book foundBook = library.searchBook(query);
                    if (foundBook != null) {
                        foundBook.displayInfo(); // Display info if book found
                    } else {
                        System.out.println("No book found matching your query."); // Message if not found
                    }
                    break;
                case "4":
                    System.out.println("Returning to Main Menu.");
                    return; // Exit the performAction method, going back to the main loop
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}

// Represents the central library containing all books
class Library {
    private final List<Book> books; // The list holding all Book objects in the library

    // Constructor for Library
    public Library() {
        this.books = new ArrayList<>(); // Initialize the main book collection
    }

    // Method to add a book to the library's collection
    public void addBook(Book book) {
        books.add(book); // Add the book object to the list
        System.out.println("\"" + book.getTitle() + "\" added to the library."); // Confirmation message
    }

    // Method to remove a book from the library by its ISBN
    public void removeBook(String isbn) {
        Book bookToRemove = null;
        // Iterate through the books list to find the book by ISBN
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                bookToRemove = book; // Found the book
                break; // Exit the loop once found
            }
        }
        if (bookToRemove != null) { // Check if a book was found with the given ISBN
            books.remove(bookToRemove); // Remove the book object from the list
            System.out.println("\"" + bookToRemove.getTitle() + "\" removed from the library."); // Confirmation message
        } else {
            System.out.println("Book with ISBN " + isbn + " not found."); // Message if no book found
        }
    }

    // Method to search for a book by title (partial, case-insensitive) or ISBN
    public Book searchBook(String query) {
        // Iterate through all books in the library
        for (Book book : books) {
            // Check if title contains the query (case-insensitive) OR if ISBN exactly matches the query
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) || book.getIsbn().equals(query)) {
                return book; // Return the found book object
            }
        }
        return null; // Return null if no book matches the query
    }

    // Method to list information about all books currently in the library
    public void listAllBooks() {
        if (books.isEmpty()) { // Check if the library is empty
            System.out.println("The library has no books.");
        } else {
            System.out.println("\nAll Books in the Library:");
            // Use forEach to call displayInfo() for each book in the list
            books.forEach(Book::displayInfo);
        }
    }
}

// The main class where the application starts
public class LibraryManagementSystem8 {
    public static void main(String[] args) {
        // Create a single Library instance
        Library library = new Library();
        // Create a single Scanner object for reading input throughout the application
        Scanner scanner = new Scanner(System.in);

        // Adding initial books to the library when the system starts
        System.out.println("--- Adding Initial Books ---");
        library.addBook(new Book("The Lord of the Rings", "978-0618260274"));
        library.addBook(new Book("Pride and Prejudice", "978-0141439518"));
        library.addBook(new Book("Fikir eske Mekabr", "12345"));
        library.addBook(new Book("Ziguara", "67890"));
        library.addBook(new Book("Merbebt", "09876"));
        library.addBook(new Book("Emegua", "54321"));
        System.out.println("--- Initial Books Added ---");

        // Creating the predefined users (Librarians and Members)
        List<User> users = new ArrayList<>();
        users.add(new Librarian("LIB001", "Alice"));
        users.add(new Member("MEM001", "Bob"));
        users.add(new Librarian("LIB002", "Salah"));
        users.add(new Member("MEM002", "Diaz"));
        users.add(new Librarian("LIB003", "Konate"));
        users.add(new Member("MEM003", "Arnold"));
        System.out.println("--- Users Created ---");

        // Variable to hold the currently logged-in user (null initially)
        User currentUser = null;

        // The main application loop. Keeps running until the user chooses to exit.
        while (true) {
            System.out.println("\n--- Library Management System ---");

            // Check if a user is currently logged in
            if (currentUser == null) {
                // Display the main menu options when no user is logged in
                System.out.println("Not logged in.");
                System.out.println("1. Login as Librarian");
                System.out.println("2. Login as Member");
                System.out.println("3. List All Books"); // Available without login
                System.out.println("4. Search Book"); // Available without login
                System.out.println("5. Logout"); // This option is only meaningful when logged in, but included in menu
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine(); // Read the choice from the main menu

                // Handle choices from the main menu
                switch (choice) {
                    case "1": { // Login as Librarian
                        System.out.print("Enter Librarian ID: ");
                        String libID = scanner.nextLine();
                        boolean found = false;
                        // Iterate through users to find the Librarian with matching ID
                        for (User user : users) {
                            // Check if the user is a Librarian AND their ID matches
                            if (user instanceof Librarian && user.getUserID().equals(libID)) {
                                currentUser = user; // Set the logged-in user
                                found = true;
                                System.out.println("Login successful. Welcome, " + currentUser.getName() + "!");
                                break; // Exit loop once user is found
                            }
                        }
                        if (!found) {
                             System.out.println("Invalid Librarian ID."); // Message if ID not found
                        }
                        // If login successful, currentUser is set, the loop continues, and the 'else' block will run next
                        break;
                    }
                    case "2": { // Login as Member
                        System.out.print("Enter Member ID: ");
                        String memID = scanner.nextLine();
                         boolean found = false;
                        // Iterate through users to find the Member with matching ID
                        for (User user : users) {
                             // Check if the user is a Member AND their ID matches
                            if (user instanceof Member && user.getUserID().equals(memID)) {
                                currentUser = user; // Set the logged-in user
                                 found = true;
                                System.out.println("Login successful. Welcome, " + currentUser.getName() + "!");
                                break; // Exit loop once user is found
                            }
                        }
                         if (!found) {
                            System.out.println("Invalid Member ID."); // Message if ID not found
                         }
                         // If login successful, currentUser is set, the loop continues, and the 'else' block will run next
                        break;
                    }
                    case "3":{ // List All Books (from main menu)
                        library.listAllBooks(); // Call the Library method to list books
                        break;
                    }
                    case "4":{ // Search Book (from main menu)
                        System.out.print("Enter title or ISBN to search: ");
                        String query = scanner.nextLine();
                        Book foundBook = library.searchBook(query); // Call the Library's search method
                        if (foundBook != null) {
                            foundBook.displayInfo(); // Display info if book found
                        } else {
                            System.out.println("No book found matching your query."); // Message if not found
                        }
                        break;
                    }
                    case "5":{ // Logout (from main menu) - when not logged in
                         System.out.println("You are not logged in."); // Message indicating no active login
                        break;
                    }
                    case "6":{ // Exit the application
                        System.out.println("Exiting the Library Management System. Goodbye!");
                        scanner.close(); // Close the scanner resource
                        return; // Terminate the main method and the program
                    }
                    default:
                        System.out.println("Invalid choice. Please try again."); // Message for invalid input
                }
            } else {
                // This block runs when a user IS logged in
                System.out.println("Logged in as: " + currentUser.getName() + " (" + currentUser.getClass().getSimpleName() + ")");
                // Call the specific performAction method for the logged-in user type
                currentUser.performAction(library, scanner); // This method contains the user's specific sub-menu loop

                // The performAction method returns when the user chooses 'Back to Main Menu'.
                // Set currentUser back to null to indicate that the user has logged out (returned to main menu state).
                currentUser = null;
            }
        }
    }
}