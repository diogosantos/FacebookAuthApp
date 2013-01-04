package controllers;

import models.FacebookConnect;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.security.InvalidParameterException;

public class Facebook extends Controller {

    private static FacebookConnect facebook = new FacebookConnect();

    public static Result fbConnect() {
        String authorizationUrl = facebook.getAuthorizationUrl();

        Logger.debug("Now redirecting to " + authorizationUrl + " for authorization.......");
        return redirect(authorizationUrl);
    }

    public static Result fbLoginCode() {
        try {
            String[] codeComplex = request().queryString().get("code");
            if (facebook.isAuthorized(getCode(codeComplex))) {
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

    private static String getCode(String[] codeComplex) {
        if (codeComplex == null || codeComplex.length == 0) {
            throw new InvalidParameterException("The code array must have one entry at least.");
        }
        return codeComplex[0];
    }


}
