package hp.cliofy;

import android.graphics.Bitmap;

import com.spotify.protocol.types.Track;

public interface IObserver {
    void pauseChange(boolean isPaused);
    void shuffleChange(boolean isShuffling);
    void trackChange(Track track);
    void imageChange(Bitmap bitmap);
}
