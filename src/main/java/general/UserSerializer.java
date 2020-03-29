package general;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class UserSerializer implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("username", user.getUsername());
        jsonObject.addProperty("firstName", user.getFirstName());
        jsonObject.addProperty("lastName", user.getLastName());
        jsonObject.addProperty("id", user.getId());

        return jsonObject;
    }
}
