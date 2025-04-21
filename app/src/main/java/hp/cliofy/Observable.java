package hp.cliofy;

import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {
    private List<IObserver> observers = new ArrayList<IObserver>();

    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    protected void notifyPauseChange(boolean isPaused) {
        for (IObserver observer : observers) {
            observer.pauseChange(isPaused);
        }
    }

    protected void notifyShuffleChange(boolean isShuffling) {
        for (IObserver observer : observers) {
            observer.shuffleChange(isShuffling);
        }
    }

    protected void notifyTrackChange(Track track) {
        for (IObserver observer : observers) {
            observer.trackChange(track);
        }
    }
}
