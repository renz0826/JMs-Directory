// Base class account
class Account{
    // Sets username and password's max size to 128 characters
    final protected int MAX_USERNAME_SIZE = 128;
    final protected int MAX_PASSWORD_SIZE = 128;

    protected String username;
    protected String name;
    protected String password;

    // Base class constructor
    private Account(String username, String password){
        this.username = username;
        this.password = password;
    }

    // Login method
    public void login(){

    }

    // Logout method
    public void logout(){

    }

    // Credential validation method
    public boolean isCredentialsCorrect(){
        return false;
    }

}