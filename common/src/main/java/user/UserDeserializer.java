package user;

import com.google.gson.*;

import java.lang.reflect.Type;

public class UserDeserializer implements JsonDeserializer<User> {
    @Override
    public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject userObject = jsonElement.getAsJsonObject();
        int id = userObject.get("id").getAsInt();
        String username = userObject.get("username").getAsString();
        String nickname = userObject.get("nickname").getAsString();

        return new User(id, username, nickname);
    }
}
