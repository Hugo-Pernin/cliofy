package hp.cliofy.Item;

/**
 * A simplified playlist
 */
public class Playlist extends Item {
    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {
        return this.getUri().substring(17);
    }

    /**
     * Creates a playlist
     * @param name name of the playlist
     * @param uri  uri of the playlist
     */
    public Playlist(String name, String uri) {
        super(name, uri);
    }
}
