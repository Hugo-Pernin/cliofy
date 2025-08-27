package hp.cliofy.Model.Service.AuthenticationService;

import android.content.Context;

public interface IAuthenticationService {
    void connect(Context context);
    void requestAccessToken(String authorizationCode);
    void refreshAccessToken();
    String getAccessToken();
}
