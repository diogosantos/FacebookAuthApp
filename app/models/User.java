package models;

import org.codehaus.jackson.JsonNode;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
public class User extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String gender;

    @Constraints.Required
    public String name;

    @Constraints.Required
    @Formats.DateTime(pattern = "MM/dd/yyyy")
    public Date birthday;

    @Constraints.Required
    @Constraints.Email
    public String email;

    @Constraints.Required
    @Formats.DateTime(pattern = "MM/dd/yyyy HH:mm:ss")
    public Date createdAt;

    @Constraints.Required
    public Long fbId;

    @Constraints.Required
    public String location;

    public static User newFromJson(JsonNode json) {
        User user = new User();
        user.fbId = json.get("id").asLong();
        user.username = json.get("username").asText();
        user.gender = json.get("gender").asText();
        user.name = json.get("name").asText();
        user.email = json.get("email").asText();
        user.birthday = parseDate(json.get("birthday").asText());
        user.createdAt = new Date();
        user.location = extractLocation(json, user);
        return user;
    }

    private static String extractLocation(JsonNode json, User user) {
        if (json.get("location") == null) {
            return null;
        }
        return json.get("location").get("name").asText();
    }

    public static Finder<Long, User> find = new Finder<Long, User>(
            Long.class, User.class
    );

    public String getPicture() {
        String id = username == null? String.valueOf(fbId) : username;
        return "https://graph.facebook.com/" + id + "/picture";
    }

    public static boolean isFirstAccess(String email) {
        return User.find.where().eq("email", email).findRowCount() == 0;
    }

    private static Date parseDate(String birthday) {
        try {
            return new SimpleDateFormat("MM/dd/yyyy").parse(birthday);
        } catch (ParseException e) {
            Logger.debug("Error getting date from json", e);
            return null;
        }
    }

    public static User findByUsername(String username) {
        return User.find.where().eq("username", username).findUnique();
    }

    public static Map<String, String> getFieldsFromJson(JsonNode jsonNode) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("email", jsonNode.get("email").asText());
        fields.put("birthday", jsonNode.get("birthday").asText());
        fields.put("name", jsonNode.get("name").asText());
        fields.put("gender", jsonNode.get("gender").asText());
        fields.put("createdAt", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));
        fields.put("fbId", jsonNode.get("id").asText());
        fields.put("username", jsonNode.get("username").asText());
        if (jsonNode.get("location") != null) {
            fields.put("location", jsonNode.get("location").get("name").asText());
        }
        return fields;
    }
}