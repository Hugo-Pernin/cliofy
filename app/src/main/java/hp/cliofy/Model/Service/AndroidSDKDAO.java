package hp.cliofy.Model.Service;

import android.content.Context;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;

import hp.cliofy.Model.Item.Track;

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
     * Shuffling state of the player: true if it's shuffling, false if not
     */
    private boolean isShuffling = false;

    /**
     * Connects the DAO to the Spotify Android app
     * @param context TODO expliquer
     * @param facadeService TODO expliquer
     * TODO commenter
     */
    public void connect(Context context, FacadeService facadeService) {
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
                                .setEventCallback(playerState -> refreshPlayerState(playerState, facadeService));
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

    public void shuffleSwitch() {
        if (isShuffling) {
            disableShuffle();
        }
        else {
            enableShuffle();
        }
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
     * @param facadeService TODO expliquer
     * TODO commenter
     */
    private void refreshPlayerState(PlayerState playerState, FacadeService facadeService) {
        isPaused = playerState.isPaused;
        isShuffling = playerState.playbackOptions.isShuffling;

        String name = playerState.track.name;
        String uri = playerState.track.uri;

        Track track = new Track(name, uri);

        facadeService.notifyPauseChange(isPaused);
        facadeService.notifyShuffleChange(isShuffling);
        facadeService.notifyTrackChange(track);
    }

    public void addItemToQueue(String uri) {
        spotifyAppRemote.getPlayerApi().queue(uri);
    }
}
