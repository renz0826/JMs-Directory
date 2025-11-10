import java.util.Scanner;

// Base class account
public class Account{
    // Sets username and password's max size to 128 characters
    final protected int MAX_USERNAME_SIZE = 128;
    final protected int MAX_PASSWORD_SIZE = 128;

    protected String username;
    protected String name;
    protected String password;

    // Base class constructor
    protected Account(){}

    // Login method
    public void login() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("====== Login System ======");

        boolean isValid;
        do {
            // Ask for username
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            
            // Ask for password
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            isValid = isCredentialsCorrect(username, password); 
            if (!isValid) {
                System.out.println("Credentials are not correct, Try again!");
            }
        } while (!isValid);

        sc.close();
    };

    // Logout method
    public void logout() {};

    // Credential validation method
    public boolean isCredentialsCorrect(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    };
}