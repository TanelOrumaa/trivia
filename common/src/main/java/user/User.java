package user;

public class User {

    private long id;
    private String username;
    private String nickname;

    public User(long id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }

    // This constructor is here to create a simplified user object for clients. Server should never use this.
    // This way we can reduce the data we need to send to clients when sending data about lobbies.
    public User(String nickname) {
        this.nickname = nickname;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

}
