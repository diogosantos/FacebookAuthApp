package controllers;

import models.FacebookConnect;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import play.Configuration;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class Facebook extends Controller {

    private static FacebookConnect facebook;

    public static Result fbConnect() {
        facebook = new FacebookConnect(request().queryString().get("code"));
        String authorizationUrl = facebook.getAuthorizationUrl();

        Logger.debug("Now redirecting to " + authorizationUrl + " for authorization.......");
        return redirect(authorizationUrl);
    }

    public static Result fbLoginCode() {
        try {
            if (facebook.isAuthorized()) {
                session("userData", facebook.getUserData());

                return redirect(
                        routes.Application.index()
                );
            } else {
                Logger.debug("I should not be here => Code received is invalid or empty");
                return badRequest("Facebook Authorization Error");
            }
        } catch (Exception e) {
            Logger.error("Sorry, an error has occurred.", e);
            return badRequest(e.getMessage() == null ? "Unknown Error" : e.getMessage());
        }
    }


}
