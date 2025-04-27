package hp.cliofy.DAO;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;
import java.util.List;

import hp.cliofy.Item.Album;
import hp.cliofy.Item.Artist;
import hp.cliofy.Item.Playlist;
import hp.cliofy.Item.Track;
import hp.cliofy.Observable;

/**
 * General DAO that serves as a facade to call other DAOs depending on the called function
 */
public class GeneralDAO extends Observable {
    /**
     * DAO communicating with the Spotify Android app
     */
    private final AndroidSDKDAO androidSDKDAO;

    /**
     * DAO communicating with the Spotify Web API
     */
    private final WebAPIDAO webAPIDAO;

    private static GeneralDAO instance;

    /**
     * Creates a general DAO
     */
    private GeneralDAO() {
        androidSDKDAO = new AndroidSDKDAO();
        webAPIDAO = new WebAPIDAO();
    }

    /**
     * Gets the singleton instance
     * @return the singleton instance
     */
    public static GeneralDAO getInstance() {
        if (instance == null) {
            instance = new GeneralDAO();
        }
        return instance;
    }

    /**
     * Connects the web API DAO
     * @param context TODO expliquer
     */
    public void connect(Context context) {
        webAPIDAO.connect(context);
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
        webAPIDAO.storeAuthorizationCode(authorizationCode);
        connectAndroidSDKDAO(context);
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
        return webAPIDAO.getPlaylistsList();
    }

    /**
     * Gets the top artists of the current user
     * @return top artists of the current user
     */
    public List<Artist> getTopArtists() {
        return webAPIDAO.getTopArtists();
    }

    public void hydrateTrack(Track track) {
        webAPIDAO.hydrateTrack(track);
    }

    public void hydrateAlbum(Album album) {
        webAPIDAO.hydrateAlbum(album);
    }

    public void hydrateArtist(Artist artist) {
        webAPIDAO.hydrateArtist(artist);
    }

    public void hydratePlaylist(Playlist playlist) {
        webAPIDAO.hydratePlaylist(playlist);
    }

    public List<Album> getArtistAlbums(Artist artist) {
        return webAPIDAO.getArtistAlbums(artist);
    }

    public List<Track> getArtistTopTracks(Artist artist) {
        return webAPIDAO.getArtistTopTracks(artist);
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
