package hp.cliofy.Model.Service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Playlist;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.Model.Observer.Observable;
import hp.cliofy.Model.Service.AlbumService.IAlbumService;
import hp.cliofy.Model.Service.ArtistService.IArtistService;
import hp.cliofy.Model.Service.AuthenticationService.IAuthenticationService;
import hp.cliofy.Model.Service.PlayerService.IPlayerService;
import hp.cliofy.Model.Service.PlaylistService.IPlaylistService;
import hp.cliofy.Model.Service.ServiceFactory.IServiceFactory;
import hp.cliofy.Model.Service.ServiceFactory.ServiceFactory;
import hp.cliofy.Model.Service.TrackService.ITrackService;
import hp.cliofy.Model.Service.UserService.IUserService;

/**
 * Facade service that calls other services depending on the called function
 */
public class FacadeService extends Observable {
    private static FacadeService instance;
    private final AndroidSDKDAO androidSDKDAO;
    private String accessToken;
    private final IServiceFactory serviceFactory;
    private IAlbumService albumService;
    private IArtistService artistService;
    private IAuthenticationService authenticationService;
    private IPlayerService playerService;
    private IPlaylistService playlistService;
    private ITrackService trackService;
    private IUserService userService;

    /**
     * Creates a facade service
     */
    private FacadeService(Context context) {
        serviceFactory = new ServiceFactory(); // TODO respecter D
        authenticationService = serviceFactory.createAuthenticationService(context);
        androidSDKDAO = new AndroidSDKDAO();
    }

    /**
     * Gets the singleton instance
     * @return the singleton instance
     */
    public static FacadeService getInstance(Context context) {
        if (instance == null) {
            instance = new FacadeService(context);
        }
        return instance;
    }

    /**
     * Connects the web API DAO
     * @param context TODO expliquer
     */
    public void connect(Context context) {
        authenticationService.requestAuthorizationCode(context);
    }

    /**
     * Connects the Android SDK DAO
     * @param context TODO expliquer
     */
    private void connectAndroidSDKDAO(Context context) {
        androidSDKDAO.connect(context, this);
    }

    /**
     * Stores the authorization code and then connects the Android SDK DAO
     * @param authorizationCode authorization code to store
     */
    public void requestAccessToken(String authorizationCode, Context context) {
        authenticationService.requestAccessToken(authorizationCode);
        accessToken = authenticationService.getAccessToken();
        ApiClient.setAccessToken(accessToken);
        // Peut être enlevé
        albumService = serviceFactory.createAlbumService();
        artistService = serviceFactory.createArtistService();
        playerService = serviceFactory.createPlayerService();
        playlistService = serviceFactory.createPlaylistService();
        trackService = serviceFactory.createTrackService();
        userService = serviceFactory.createUserService();
        connectAndroidSDKDAO(context); // TODO why here?
    }

    /**
     * Disconnects all the DAOs
     */
    public void disconnect() {
        androidSDKDAO.disconnect();
        // No need to disconnect the Web API DAO
    }

    /**
     * Pauses the player if it's playing, or resumes it if it's paused
     */
    public void pauseResume() {
        androidSDKDAO.pauseResume();
    }

    /**
     * Enables shuffle if it's disabled, or disables it if it's enabled
     */
    public void shuffleSwitch() {
        androidSDKDAO.shuffleSwitch();
    }

    /**
     * Skips to the previous track
     */
    public void skipPrevious() {
        androidSDKDAO.skipPrevious();
    }

    /**
     * Skips to the next track
     */
    public void skipNext() {
        androidSDKDAO.skipNext();
    }

    /**
     * Plays a Spotify resource from its uri. Can be a title, an album or a podcast for example
     * @param uri uri of the resource to play
     */
    public void play(String uri) {
        androidSDKDAO.play(uri);
    }

    public void playWithOffset(String uri, int offset) {
        playerService.playWithOffset(uri, offset);
    }

    /**
     * Disables shuffle mode
     */
    public void disableShuffle() {
        androidSDKDAO.disableShuffle();
    }

    /**
     * Enables shuffle mode
     */
    public void enableShuffle() {
        androidSDKDAO.enableShuffle();
    }

    /**
     * Gets the playlists list of the current user
     * @return playlists list of the current user
     */
    public CompletableFuture<List<Playlist>> getPlaylistsList() {
        return userService.getPlaylistsList();
    }

    /**
     * Gets the top artists of the current user
     * @return top artists of the current user
     */
    public CompletableFuture<List<Artist>> getTopArtists() {
        return userService.getTopArtists();
    }

    public void hydrateTrack(Track track) {
        trackService.hydrateTrack(track);
    }

    public void hydrateArtist(Artist artist) {
        artistService.hydrateArtist(artist);
    }

    public CompletableFuture<List<Album>> getArtistAlbums(Artist artist, String type) {
        return artistService.getArtistAlbums(artist, type);
    }

    public CompletableFuture<List<Track>> getArtistTopTracks(Artist artist) {
        return artistService.getArtistTopTracks(artist);
    }

    public CompletableFuture<List<Track>> getAlbumTracks(Album album) {
        return albumService.getAlbumTracks(album);
    }

    public CompletableFuture<List<Track>> getPlaylistTracks(Playlist playlist) {
        return playlistService.getPlaylistTracks(playlist);
    }

    // uri ou Item ?
    public void addItemToQueue(String uri) {
        androidSDKDAO.addItemToQueue(uri);
    }

    public Bitmap getBitmapImageFromUrl(String url) {
        final Bitmap[] image = new Bitmap[1]; // A one-entry array is necessary

        Thread thread = new Thread(() -> {
            try {
                URL urlObject = new URL(url);
                image[0] = BitmapFactory.decodeStream(urlObject.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return image[0];
    }
}
