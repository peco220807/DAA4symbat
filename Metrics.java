package metrics;

public interface Metrics {
    void reset();
    void startTimer();
    void stopTimer();
    long getElapsedNanos();

    void incA();
    void incB();
    void incC();

    long getA();
    long getB();
    long getC();
}
