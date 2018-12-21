package sample;


/**
 * Interface with methods for manipulation of observers.
 */
public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void updateObservers();
}
