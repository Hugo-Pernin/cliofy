package hp.cliofy;

public class Playlist {
    private String name;
    private String uri;

    public String getUri() {
        return this.uri;
    }

    public Playlist(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
