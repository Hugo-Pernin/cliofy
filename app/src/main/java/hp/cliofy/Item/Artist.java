package hp.cliofy.Item;

import java.util.List;

/**
 * A simplified artist
 */
public class Artist extends Item {
    private int followersTotal;
    private List<String> genres;

    public int getFollowersTotal() {
        return followersTotal;
    }

    public void setFollowersTotal(int followersTotal) {
        this.followersTotal = followersTotal;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getId() {
        return this.getUri().substring(15);
    }

    /**
     * Creates an artist
     * @param name name of the artist
     * @param uri  uri of the artist
     * @param imageUrl url of the image of the artist
     */
    public Artist(String name, String uri, String imageUrl) {
        super(name, uri, imageUrl);
    }
}
