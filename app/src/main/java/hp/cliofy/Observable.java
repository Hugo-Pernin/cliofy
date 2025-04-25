package hp.cliofy;

import android.graphics.Bitmap;

import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for an observable / a subject
 */
public abstract class Observable {
    /**
     * List containing all the observers
     */
    private List<IObserver> observers = new ArrayList<IObserver>();

    /**
     * Adds an observer to the observers list
     * @param observer observer to add
     */
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from the observers list
     * @param observer observer to remove
     */
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies observers that the pause state has changed
     * @param isPaused true if the player is paused, false if not
     */
    protected void notifyPauseChange(boolean isPaused) {
        for (IObserver observer : observers) {
            observer.pauseChange(isPaused);
        }
    }

    /**
     * Notifies observers that the shuffling state has changed
     * @param isShuffling true if the player is shuffling, false if not
     */
    protected void notifyShuffleChange(boolean isShuffling) {
        for (IObserver observer : observers) {
            observer.shuffleChange(isShuffling);
        }
    }

    /**
     * Notifies observers that the track has changed
     * @param track the new track
     */
    protected void notifyTrackChange(Track track) {
        for (IObserver observer : observers) {
            observer.trackChange(track);
        }
    }

    /**
     * Notifies observers that the image has changed
     * @param bitmap the new image
     */
    protected void notifyImageChange(Bitmap bitmap) {
        for (IObserver observer : observers) {
            observer.imageChange(bitmap);
        }
    }
}
