package hp.cliofy.Model.Service.AuthenticationService;

import android.content.Context;

public interface IAuthenticationService {
    void requestAuthorizationCode(Context context);
    void requestAccessToken(String authorizationCode);
    String getAccessToken();
}
