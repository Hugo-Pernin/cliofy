package hp.cliofy;

import androidx.annotation.NonNull;

/**
 * A simplified playlist
 */
public class Playlist {
    /**
     * Name of the playlist
     */
    private final String name;

    /**
     * uri of the playlist
     */
    private final String uri;

    /**
     * Returns the uri of the playlist
     * @return the uri of the playlist
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * Creates a playlist
     * @param name name of the playlist
     * @param uri uri of the playlist
     */
    public Playlist(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
