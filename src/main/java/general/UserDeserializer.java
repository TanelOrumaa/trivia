package general;

import com.google.gson.*;

import java.lang.reflect.Type;

public class UserDeserializer implements JsonDeserializer<User> {
    @Override
    public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject userObject = jsonElement.getAsJsonObject();
        int id = userObject.get("id").getAsInt();
        String username = userObject.get("username").getAsString();
        String firstName = userObject.get("firstName").getAsString();
        String lastName = userObject.get("lastName").getAsString();

        return new User(id, username, firstName, lastName);
    }
}
