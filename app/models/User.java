package models;

import org.codehaus.jackson.JsonNode;

public class User {

    public String username;
    public String gender;
    public String name;

    public static User newFromJson(JsonNode json) {
        User user = new User();
        user.username = json.get("username").asText();
        user.gender = json.get("gender").asText();
        user.name = json.get("name").asText();
        return user;
    }
}