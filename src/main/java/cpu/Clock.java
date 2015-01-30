package cpu;

/**
 * Static class that exports a single method--waitUntil, which takes a start time
 * in nanoseconds (collected using System.nanoTime()), and a number of
 * nanoseconds after that start point. The method will not return until that
 * many nanoseconds have passed.
 */
public class Clock {
    public static void waitUntil(long start, long duration) {
        int sleepNs = (int)(start + duration - System.nanoTime());

        // TODO(ddoucet): I should revisit this when things are looking a
        // little more fleshed out. It seems like we never sleep--that the
        // instructions are taking too long to execute. I wonder if that'll
        // actually be a problem when actually playing the games?
        if (sleepNs < 0)
            return;

        try {
            Thread.sleep(0, sleepNs);
        } catch (InterruptedException e) {
        }
    }
}
