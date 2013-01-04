package controllers;

import com.avaje.ebean.Ebean;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        String userData = session("userData");
        User user = null;
        if (userData != null) {
            Logger.debug("Starting parsing the JSON response as User object");
            user = User.newFromJson(Json.parse(userData));
            session("username", user.username);
            if (!isRegistered(user)) {
                Logger.debug("First access, registering user");
                Ebean.save(user);
            } else {
                user = User.find.where().eq("email", user.email).findUnique();
            }
        }

        return ok(
                index.render(user)
        );
    }

    private static boolean isRegistered(User user) {
        return User.find.where().eq("email", user.email).findRowCount() > 0;
    }

    public static Result logout() {
        session().clear();
        return ok(
                index.render(null)
        );
    }


}
