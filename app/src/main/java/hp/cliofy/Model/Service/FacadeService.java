package hp.cliofy.Model.Service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;
import java.util.List;

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
    private FacadeService() {
        serviceFactory = new ServiceFactory(); // TODO respecter D
        authenticationService = serviceFactory.createAuthenticationService();
        androidSDKDAO = new AndroidSDKDAO();
    }

    /**
     * Gets the singleton instance
     * @return the singleton instance
     */
    public static FacadeService getInstance() {
        if (instance == null) {
            instance = new FacadeService();
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
    public void storeAuthorizationCode(String authorizationCode, Context context) {
        authenticationService.storeAuthorizationCode(authorizationCode);
        accessToken = authenticationService.getAccessToken();
        ApiClient.setAccessToken(accessToken);
        // Peut être enlevé
        albumService = serviceFactory.createAlbumService(accessToken);
        artistService = serviceFactory.createArtistService(accessToken);
        playerService = serviceFactory.createPlayerService(accessToken);
        playlistService = serviceFactory.createPlaylistService(accessToken);
        trackService = serviceFactory.createTrackService(accessToken);
        userService = serviceFactory.createUserService(accessToken);
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
    public List<Playlist> getPlaylistsList() {
        return userService.getPlaylistsList();
    }

    /**
     * Gets the top artists of the current user
     * @return top artists of the current user
     */
    public List<Artist> getTopArtists() {
        return userService.getTopArtists();
    }

    public void hydrateTrack(Track track) {
        trackService.hydrateTrack(track);
    }

    public void hydrateAlbum(Album album) {
        albumService.hydrateAlbum(album);
    }

    public void hydrateArtist(Artist artist) {
        artistService.hydrateArtist(artist);
    }

    public void hydratePlaylist(Playlist playlist) {
        playlistService.hydratePlaylist(playlist);
    }

    public List<Album> getArtistAlbums(Artist artist, String type) {
        return artistService.getArtistAlbums(artist, type);
    }

    public List<Track> getArtistTopTracks(Artist artist) {
        return artistService.getArtistTopTracks(artist);
    }

    public List<Track> getAlbumTracks(Album album) {
        return albumService.getAlbumTracks(album);
    }

    public List<Track> getPlaylistTracks(Playlist playlist) {
        return playlistService.getPlaylistTracks(playlist);
    }

    public Bitmap getBitmapImageFromUrl(String url) {
        final Bitmap[] image = new Bitmap[1]; // A one-entry array is necessary

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL urlObject = new URL(url);
                    image[0] = BitmapFactory.decodeStream(urlObject.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
