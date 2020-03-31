package general;

public class User {

    private int id;
    private String username;
    private String firstName;
    private String lastName;

    public User(int id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // This constructor is here to create a simplified user object for clients. Server should never use this.
    // This way we can reduce the data we need to send to clients when sending data about lobbies.
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
