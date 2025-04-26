package hp.cliofy.Item;

import androidx.annotation.NonNull;

/**
 * Class for an item
 */
public abstract class Item {
    private final String name;
    private final String uri;

    /**
     * Returns the uri of the item
     * @return the uri of the item
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * Creates an item
     * @param name name of the item
     * @param uri uri of the item
     */
    public Item(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
