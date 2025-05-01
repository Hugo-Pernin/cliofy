package hp.cliofy.Observer;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import hp.cliofy.Item.Track;

/**
 * Class for an observable / a subject
 */
public abstract class Observable {
    /**
     * List containing all the observers
     */
    private final List<IObserver> observers;

    /**
     * Creates a new observable
     */
    public Observable() {
        observers = new ArrayList<>();
    }

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
    public void notifyPauseChange(boolean isPaused) {
        for (IObserver observer : observers) {
            observer.pauseChange(isPaused);
        }
    }

    /**
     * Notifies observers that the shuffling state has changed
     * @param isShuffling true if the player is shuffling, false if not
     */
    public void notifyShuffleChange(boolean isShuffling) {
        for (IObserver observer : observers) {
            observer.shuffleChange(isShuffling);
        }
    }

    /**
     * Notifies observers that the track has changed
     * @param track the new track
     */
    public void notifyTrackChange(Track track) {
        for (IObserver observer : observers) {
            observer.trackChange(track);
        }
    }

    /**
     * Notifies observers that the image has changed
     * @param bitmap the new image
     */
    public void notifyImageChange(Bitmap bitmap) {
        for (IObserver observer : observers) {
            observer.imageChange(bitmap);
        }
    }
}
