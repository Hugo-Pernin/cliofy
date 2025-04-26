package hp.cliofy.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * A simplified artist
 */
public class Artist extends Item {
    private int followersTotal;
    private List<String> genres;
    private String imageUrl;

    public int getFollowersTotal() {
        return followersTotal;
    }

    public List<String> getGenres() {
        return new ArrayList<>(genres);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Creates an artist
     * @param name name of the artist
     * @param uri  uri of the artist
     * @param followersTotal number of followers of the artist
     * @param genres list of genres of the artist
     * @param imageUrl url of the image of the artist
     */
    public Artist(String name, String uri, int followersTotal, List<String> genres, String imageUrl) {
        super(name, uri);
        this.followersTotal = followersTotal;
        this.genres = genres;
        this.imageUrl = imageUrl;
    }
}
