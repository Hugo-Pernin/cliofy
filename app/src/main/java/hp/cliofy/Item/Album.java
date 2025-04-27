package hp.cliofy.Item;

/**
 * A simplified album
 */
public class Album extends Item {
    private String albumType; // album, single or compilation
    private int totalTracks;
    private String imageUrl;
    private String releaseDate;

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return this.getUri().substring(15);
    }

    /**
     * Creates an album
     * @param name name of the album
     * @param uri  uri of the album
     */
    public Album(String name, String uri) {
        super(name, uri);
    }
}
