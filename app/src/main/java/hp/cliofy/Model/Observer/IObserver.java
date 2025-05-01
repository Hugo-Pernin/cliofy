package hp.cliofy.Model.Observer;

import android.graphics.Bitmap;

import hp.cliofy.Model.Item.Track;

/**
 * Interface for an observer / a subscriber
 */
public interface IObserver {
    /**
     * Function called when the pause state has changed
     * @param isPaused true if the player is paused, false if not
     */
    void pauseChange(boolean isPaused);

    /**
     * Function called when the shuffling state has changed
     * @param isShuffling true if the player is shuffling, false if not
     */
    void shuffleChange(boolean isShuffling);

    /**
     * Function called when the track has changed
     * @param track the new track
     */
    void trackChange(Track track);

    /**
     * Function called when the image has changed
     * @param bitmap the new image
     */
    void imageChange(Bitmap bitmap);
}
