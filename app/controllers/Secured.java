package controllers;

import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {


    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.Application.index());
    }

}
