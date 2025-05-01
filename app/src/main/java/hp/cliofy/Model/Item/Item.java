package hp.cliofy.Model.Item;

import androidx.annotation.NonNull;

/**
 * Class for an item
 */
public abstract class Item {
    private final String name;
    private final String uri;
    private String imageUrl;

    /**
     * Returns the uri of the item
     * @return the uri of the item
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * Gets the image url of the item
     * @return the image url of the item
     */
    public String getImageUrl() {
        return this.imageUrl;
    }

    /**
     * Creates an item
     * @param name name of the item
     * @param uri uri of the item
     * @param imageUrl url of the image of the item
     */
    public Item(String name, String uri, String imageUrl) {
        this.name = name;
        this.uri = uri;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
