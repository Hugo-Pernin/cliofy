package hp.cliofy;

import androidx.annotation.NonNull;

public class Artist {
    private final String name;
    private final String uri;

    /**
     * Returns the uri of the artist
     * @return the uri of the artist
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * Creates an artist
     * @param name name of the artist
     * @param uri uri of the artist
     */
    public Artist(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
