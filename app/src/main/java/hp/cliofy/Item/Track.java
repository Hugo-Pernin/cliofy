package hp.cliofy.Item;

public class Track extends Item {
    private Album album;
    private Artist artist;
    private int discNumber;
    private int durationMs;
    private int trackNumber;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public int getDiscNumber() {
        return discNumber;
    }

    public void setDiscNumber(int discNumber) {
        this.discNumber = discNumber;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getId() {
        return this.getUri().substring(14);
    }

    /**
     * Creates a track
     * @param name name of the track
     * @param uri  uri of the track
     */
    public Track(String name, String uri) {
        super(name, uri);
    }
}
