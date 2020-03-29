package general;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LobbyDeserializer implements JsonDeserializer<Lobby> {

    @Override
    public Lobby deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject lobbyObject = jsonElement.getAsJsonObject();

        // Read the lobby name and code
        String name = lobbyObject.get("name").getAsString();
        int code = lobbyObject.get("code").getAsInt();

        // Read the people in the lobby and who is the lobby owner.
        List<User> usersInLobby = new ArrayList<>();
        User lobbyOwner = null;

        JsonArray users = lobbyObject.get("users").getAsJsonArray();
        for (int i = 0; i < users.size(); i++) {
            JsonObject user = users.get(i).getAsJsonObject();
            String firstName = user.get("firstName").getAsString();
            String lastName = user.get("lastName").getAsString();

            // Create simplified User objects for displaying in clients.
            User lobbyUser = new User(firstName, lastName);

            // If this user is the lobby owner, mark it as so.
            boolean isLobbyOwner = user.get("isLobbyOwner").getAsBoolean();
            if (isLobbyOwner) {
                lobbyOwner = lobbyUser;
            }

            // Add the user to lobby users list.
            usersInLobby.add(lobbyUser);
        }

        return new Lobby(name, code, usersInLobby, lobbyOwner);

    }
}
