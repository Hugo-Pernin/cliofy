package hp.cliofy;

import com.spotify.protocol.types.Track;

public interface IObserver {
    public void pauseChange(boolean isPaused);
    public void shuffleChange(boolean isShuffling);
    public void trackChange(Track track);
}
