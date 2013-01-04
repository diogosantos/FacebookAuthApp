package controllers;

import models.User;
import org.codehaus.jackson.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import play.Configuration;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Facebook extends Controller {

    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;

    private static Configuration config = Configuration.root();
    private static String fbAppId = config.getString("fb.app.id");
    private static String fbAppSecret = config.getString("fb.app.secretKey");
    private static String fbCallback = config.getString("fb.app.callback");

    private static OAuthService service = new ServiceBuilder()
            .provider(FacebookApi.class)
            .apiKey(fbAppId)
            .apiSecret(fbAppSecret)
            .callback(fbCallback)
            .scope("email,user_birthday,user_location")
            .build();


    public static Result fbConnect() {
        Logger.debug("Fetching the authorization URL...");
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);

        Logger.debug("Got the Authorization URL!:" + authorizationUrl);
        Logger.debug("Now redirecting to the URL for authorization.......");
        return redirect(authorizationUrl);
    }

    public static Result fbLoginCode() {
        //TODO refactor: move all the facebook access code to an object like 'FacebookConnect'
        try {
            Logger.info("Parsing returned code from fb...");
            String[] code = request().queryString().get("code");
            if (code != null && code.length > 0) {
                Logger.debug("Code received from db:" + code[0]);
                Verifier verifier = new Verifier(code[0]);

                Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
                Logger.debug("Got the Access Token!");
                Logger.debug("Access token is: " + accessToken + " )");

                Logger.debug("Fetch the user information now...");
                OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
                service.signRequest(accessToken, request);
                org.scribe.model.Response response = request.send();

                Logger.debug("Code received is:" + response.getCode());
                String body = response.getBody();
                Logger.debug("Response data received is:" + body);

                session("userData", body);
                return redirect(
                        routes.Application.index()
                );

            } else {
                Logger.debug("I should not be here => Code received is invalid or empty");
                return badRequest("Unknown Error");
            }

        } catch (Exception e) {
            String message = e.getMessage() == null ? "Unknown Error" : e.getMessage();
            Logger.error("Sorry, an error has occurred. Message from server is: " + message);
            e.printStackTrace();
            return badRequest(message);
        }
    }


}
