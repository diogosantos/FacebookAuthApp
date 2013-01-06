package controllers;

import models.FacebookConnect;
import models.User;
import org.codehaus.jackson.JsonNode;
import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.Json;
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
                return handleAuthorizedUser();
            } else {
                Logger.debug("I should not be here => Code received is invalid or empty");
                return badRequest("Facebook Authorization Error");
            }
        } catch (Exception e) {
            Logger.error("Sorry, an error has occurred.", e);
            return badRequest(e.getMessage() == null ? "Unknown Error" : e.getMessage());
        }
    }

    private static Result handleAuthorizedUser() {
        Logger.debug("Starting parsing the JSON response");
        JsonNode jsonNode = Json.parse(facebook.getUserData());

        if (User.isFirstAccess(jsonNode.get("email").asText())) {
            Logger.debug("First access, registering user");

            Form<User> userForm = getUserForm(jsonNode);
            if (userForm.hasErrors()) {
                Logger.debug("User doesn't contain complete data: " + Json.stringify(userForm.errorsAsJson()));
                return unauthorized(getErrorMessage(userForm));
            } else {
                userForm.get().save();
                Logger.debug("User registered");
            }
        }

        session("username", jsonNode.get("username").asText());
        Logger.debug("Session initiated");

        return redirect(
                routes.Application.index()
        );
    }

    private static String getErrorMessage(Form<User> userForm) {
        StringBuilder errors = new StringBuilder();
        for (ValidationError ve : userForm.globalErrors()) {
            errors.append(ve.message() + "\n");
        }
        return "Facebook uer data is incomplete. Please fill in the required information on your facebook account: \n\t" + Json.stringify(userForm.errorsAsJson());
    }

    private static Form<User> getUserForm(JsonNode jsonNode) {
        return form(User.class).bind(User.getFieldsFromJson(jsonNode));
    }

    private static String getCode(String[] codeComplex) {
        if (codeComplex == null || codeComplex.length == 0) {
            throw new InvalidParameterException("The code array must have one entry at least.");
        }
        return codeComplex[0];
    }


}
