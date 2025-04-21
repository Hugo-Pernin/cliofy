package hp.cliofy;

import android.content.Context;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class AndroidSDKDAO {
    private final String CLIENT_ID = "6837605e645041288ee6e45da7e46ff6";
    private final String REDIRECT_URI = "http://com.hp.cliofy/callback";
    private SpotifyAppRemote spotifyAppRemote;
    private boolean isPaused = false;

    public void connect(Context context, GeneralDAO generalDAO) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        AndroidSDKDAO.this.spotifyAppRemote = spotifyAppRemote;
                        Toast toast = Toast.makeText(context, "Connecté", Toast.LENGTH_SHORT);
                        toast.show();
                        //resume();
                        AndroidSDKDAO.this.spotifyAppRemote.getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(playerState -> refreshPlayerState(playerState, generalDAO));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Toast toast = Toast.makeText(context, "Erreur lors de la connexion : " + throwable.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    public void disconnect() {
        SpotifyAppRemote.disconnect(spotifyAppRemote);
    }

    public void pauseResume() {
        if (isPaused) {
            resume();
        }
        else {
            pause();
        }
    }

    private void pause() {
        spotifyAppRemote.getPlayerApi().pause();
    }

    private void resume() {
        spotifyAppRemote.getPlayerApi().resume();
    }

    public void skipPrevious() {
        spotifyAppRemote.getPlayerApi().skipPrevious();
    }

    public void skipNext() {
        spotifyAppRemote.getPlayerApi().skipNext();
    }

    public void play(String uri) {
        spotifyAppRemote.getPlayerApi().play(uri);
    }

    public void disableShuffle() {
        spotifyAppRemote.getPlayerApi().setShuffle(false);
    }

    public void enableShuffle() {
        spotifyAppRemote.getPlayerApi().setShuffle(true);
    }

    private void refreshPlayerState(PlayerState playerState, GeneralDAO generalDAO) {
        isPaused = playerState.isPaused;
        boolean isShuffling = playerState.playbackOptions.isShuffling;
        Track track = playerState.track;

        generalDAO.notifyPauseChange(isPaused);
        generalDAO.notifyShuffleChange(isShuffling);
        generalDAO.notifyTrackChange(track);

        spotifyAppRemote
                .getImagesApi()
                .getImage(track.imageUri, Image.Dimension.LARGE)
                .setResultCallback(generalDAO::notifyImageChange);
    }
}
