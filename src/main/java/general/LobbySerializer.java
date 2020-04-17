package general;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.List;

public class LobbySerializer implements JsonSerializer<Lobby> {

    @Override
    public JsonElement serialize(Lobby lobby, Type type, JsonSerializationContext jsonSerializationContext) {

        // Create new lobby object.
        JsonObject lobbyObject = new JsonObject();

        // Add the lobby name and code to the object.
        lobbyObject.addProperty("name", lobby.getName());
        lobbyObject.addProperty("code", lobby.getCode());

        // Create a new array of users.
        JsonArray usersObject = new JsonArray();
        List<User> users = lobby.getConnectedUsers();

        // For each connected user add a user object.
        for (User user : users) {
            JsonObject userObject = new JsonObject();
            userObject.addProperty("username", user.getUsername());
            userObject.addProperty("firstName", user.getFirstName());
            userObject.addProperty("lastName", user.getLastName());
            userObject.addProperty("isLobbyOwner", user.getId() == lobby.getLobbyOwnerId());
            usersObject.add(userObject);
        }

        lobbyObject.add("users", usersObject);
        return lobbyObject;
    }
}
