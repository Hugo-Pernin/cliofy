package hp.cliofy.DAO;

import android.content.Context;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

/**
 * DAO communicating with the Spotify Android app
 */
class AndroidSDKDAO {
    /**
     * Client ID found in the Spotify developer dashboard
     */
    private final String CLIENT_ID = "6837605e645041288ee6e45da7e46ff6";

    /**
     * Redirect URL entered in the Spotify developer dashboard
     */
    private final String REDIRECT_URI = "http://com.hp.cliofy/callback";

    /**
     * TODO expliquer
     */
    private SpotifyAppRemote spotifyAppRemote;

    /**
     * Pause state of the player: true if it's paused, false if it's playing
     */
    private boolean isPaused = false;

    /**
     * Connects the DAO to the Spotify Android app
     * @param context TODO expliquer
     * @param generalDAO TODO expliquer
     * TODO commenter
     */
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
                        Toast.makeText(context, "Connecté au SDK", Toast.LENGTH_SHORT).show();
                        AndroidSDKDAO.this.spotifyAppRemote.getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(playerState -> refreshPlayerState(playerState, generalDAO));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Toast.makeText(context, "Erreur lors de la connexion au SDK : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Disconnects the DAO from the Spotify Android app
     */
    public void disconnect() {
        SpotifyAppRemote.disconnect(spotifyAppRemote);
    }

    /**
     * Pauses the player if it's playing, or resumes it if it's paused
     */
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

    /**
     * Skips to the previous track
     */
    public void skipPrevious() {
        spotifyAppRemote.getPlayerApi().skipPrevious();
    }

    /**
     * Skips to the next track
     */
    public void skipNext() {
        spotifyAppRemote.getPlayerApi().skipNext();
    }

    /**
     * Plays a Spotify resource from its uri. Can be a title, an album or a podcast for example
     * @param uri uri of the resource to play
     */
    public void play(String uri) {
        spotifyAppRemote.getPlayerApi().play(uri);
    }

    /**
     * Disables shuffle mode
     */
    public void disableShuffle() {
        spotifyAppRemote.getPlayerApi().setShuffle(false);
    }

    /**
     * Enables shuffle mode
     */
    public void enableShuffle() {
        spotifyAppRemote.getPlayerApi().setShuffle(true);
    }

    /**
     * Refreshes the state of the player (pause state, shuffling state, track, image)
     * @param playerState TODO expliquer
     * @param generalDAO TODO expliquer
     * TODO commenter
     */
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
