package hp.cliofy.Model.Item;

/**
 * A simplified album
 */
public class Album extends Item {
    private final String albumType; // album, single or compilation
    private final int totalTracks;
    private final String releaseDate;

    public String getAlbumType() {
        return albumType;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getId() {
        return this.getUri().substring(14);
    }

    /**
     * Creates an album
     * @param name name of the album
     * @param uri  uri of the album
     * @param imageUrl url of the image of the album
     */
    public Album(String name, String uri, String imageUrl, String albumType, int totalTracks, String releaseDate) {
        super(name, uri, imageUrl);
        this.albumType = albumType;
        this.totalTracks = totalTracks;
        this.releaseDate = releaseDate;
    }
}
