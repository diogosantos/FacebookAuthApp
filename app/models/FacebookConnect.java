package models;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import play.Configuration;
import play.Logger;

import java.security.InvalidParameterException;

public class FacebookConnect {

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

    Token token;

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public String getUserData() {
        Logger.debug("Fetch the user information now...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(token, request);
        org.scribe.model.Response response = request.send();

        Logger.debug("User data received");
        return response.getBody();
    }

    private Token getToken(String code) {
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, new Verifier(code));
        Logger.debug("Got the Access Token: " + accessToken);
        return token = accessToken;
    }

    public boolean isAuthorized(String code) {
        token = getToken(code);
        return token != null;
    }

}
