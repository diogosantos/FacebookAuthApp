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

    private Token token;

    public FacebookConnect(String[] codeComplex) {
        if (codeComplex == null || codeComplex.length == 0) {
            throw new InvalidParameterException("The code array must have one entry at least.");
        }
        Logger.debug("Code received from fb:" + codeComplex[0]);

        Token accessToken = service.getAccessToken(EMPTY_TOKEN, new Verifier(codeComplex[0]));
        Logger.debug("Got the Access Token: " + accessToken);

        this.token = accessToken;
    }

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public String getUserData() {
        Logger.debug("Fetch the user information now...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(token, request);
        org.scribe.model.Response response = request.send();

        String body = response.getBody();
        Logger.debug("User data received: " + body);
        return body;
    }

    public boolean isAuthorized() {
        return token != null;
    }
}
