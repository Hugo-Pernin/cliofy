package hp.cliofy;

import android.content.Context;

import java.util.List;

public class GeneralDAO extends Observable {
    private AndroidSDKDAO androidSDKDAO = new AndroidSDKDAO();
    private WebAPIDAO webAPIDAO = new WebAPIDAO();

    public void connect(Context context) {
        webAPIDAO.connect(context);
        androidSDKDAO.connect(context, this);
    }

    public void storeAuthorizationCode(String authorizationCode) {
        webAPIDAO.storeAuthorizationCode(authorizationCode);
    }

    public void disconnect() {
        androidSDKDAO.disconnect();
    }

    public void pauseResume() {
        androidSDKDAO.pauseResume();
    }

    public void skipPrevious() {
        androidSDKDAO.skipPrevious();
    }

    public void skipNext() {
        androidSDKDAO.skipNext();
    }

    public void play(String uri) {
        androidSDKDAO.play(uri);
    }

    public void disableShuffle() {
        androidSDKDAO.disableShuffle();
    }

    public void enableShuffle() {
        androidSDKDAO.enableShuffle();
    }

    public List<Playlist> getPlaylistsList() {
        return webAPIDAO.getPlaylistsList();
    }
}
