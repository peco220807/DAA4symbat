package metrics;

public class SimpleMetrics implements Metrics {
    private long t0;
    private long t1;
    private long a;
    private long b;
    private long c;

    public void reset() {
        t0 = 0L; t1 = 0L;
        a = 0L; b = 0L; c = 0L;
    }

    public void startTimer() { t0 = System.nanoTime(); }
    public void stopTimer() { t1 = System.nanoTime(); }
    public long getElapsedNanos() { return t1 - t0; }

    public void incA() { a = a + 1; }
    public void incB() { b = b + 1; }
    public void incC() { c = c + 1; }

    public long getA() { return a; }
    public long getB() { return b; }
    public long getC() { return c; }
}
