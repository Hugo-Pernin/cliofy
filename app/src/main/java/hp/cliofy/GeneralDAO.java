package hp.cliofy;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class GeneralDAO {
    private final String CLIENT_ID = "6837605e645041288ee6e45da7e46ff6";
    private final String REDIRECT_URI = "http://com.hp.cliofy/callback";

    private SpotifyAppRemote mSpotifyAppRemote;

    public void connect(Context context) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        resume();
                        mSpotifyAppRemote.getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(playerState -> {
                                    refreshPlayerState(playerState);
                                });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    public void disconnect() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    public void pause() {
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    public void resume() {
        mSpotifyAppRemote.getPlayerApi().resume();
    }

    public void skipPrevious() {
        mSpotifyAppRemote.getPlayerApi().skipPrevious();
    }

    public void skipNext() {
        mSpotifyAppRemote.getPlayerApi().skipNext();
    }

    public void play(String uri) {
        mSpotifyAppRemote.getPlayerApi().play(uri);
    }

    private void refreshPlayerState(PlayerState playerState) {
        final Track track = playerState.track;
        if (track != null) {
            Log.d("MainActivity", "Nom : " + track.name);
            Log.d("MainActivity", "Artiste : " + track.artist.name);
            Log.d("MainActivity", "Album : " + track.album.name);
            Log.d("MainActivity", "Uri de l'image : " + track.imageUri.raw);
            //Log.d("MainActivity", "Position : " + playerState.playbackPosition);
            Log.d("MainActivity", "Durée totale : " + track.duration);
            Log.d("MainActivity", "Est en pause : " + playerState.isPaused);
            Log.d("MainActivity", "Est en aléatoire : " + playerState.playbackOptions.isShuffling);
        }
    }
}
