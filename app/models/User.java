package models;

import org.codehaus.jackson.JsonNode;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;
import java.lang.annotation.Target;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    @Formats.DateTime(pattern = "dd/MM/yyyy")
    public Date birthday;

    @Constraints.Required
    public String email;

    @Constraints.Required
    public Date createdAt;

    public static User newFromJson(JsonNode json) {
        User user = new User();
        user.username = json.get("username").asText();
        user.gender = json.get("gender").asText();
        user.name = json.get("name").asText();
        user.email = json.get("email").asText();
        user.birthday = parseDate(json.get("birthday").asText());
        user.createdAt = new Date();
        return user;
    }

    public static Finder<Long,User> find = new Finder<Long,User>(
            Long.class, User.class
    );

    private static Date parseDate(String birthday) {
        try {
            return new SimpleDateFormat("MM/dd/yyyy").parse(birthday);
        } catch (ParseException e) {
            Logger.debug("Error getting date from json", e);
            return null;
        }
    }
}