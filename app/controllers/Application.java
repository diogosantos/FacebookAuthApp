package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;


public class Application extends Controller {

    public static Result index() {
        User user = isUserLoggedOn() ? User.findByUsername(session("username")) : null;
        return ok(
                index.render(user)
        );
    }

    private static boolean isUserLoggedOn() {
        return session("username") != null;
    }

    public static Result logout() {
        session().clear();
        return ok(
                index.render(null)
        );
    }


}
