package controllers;

import models.User;
import play.*;
import play.api.templates.Html;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        String userData = session("userData");
        User user = null;
        if(userData != null) {
            Logger.debug("Starting parsing the JSON response as User object");
            user = User.newFromJson(Json.parse(userData));
        }

        return ok(
                index.render(user)
        );
    }

    public static Result logout() {
        session().clear();
        return ok(
                index.render(null)
        );
    }


}
