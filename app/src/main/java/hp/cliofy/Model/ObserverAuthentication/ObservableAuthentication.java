package hp.cliofy.Model.ObserverAuthentication;

import java.util.ArrayList;
import java.util.List;

public abstract class ObservableAuthentication {
    private final List<IObserverAuthentication> observers;

    public ObservableAuthentication() {
        observers = new ArrayList<>();
    }

    public void addObserver(IObserverAuthentication observer) {
        observers.add(observer);
    }

    public void removeObserver(IObserverAuthentication observer) {
        observers.remove(observer);
    }

    public void notifyAccessTokenRefreshed() {
        for (IObserverAuthentication observer : observers) {
            observer.accessTokenRefreshed();
        }
    }
}
