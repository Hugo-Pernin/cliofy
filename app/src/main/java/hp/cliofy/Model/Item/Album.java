package hp.cliofy.Model.Item;

/**
 * A simplified album
 */
public class Album extends Item {
    private String albumType; // album, single or compilation
    private int totalTracks;
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
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
    public Album(String name, String uri, String imageUrl) {
        super(name, uri, imageUrl);
    }
}
