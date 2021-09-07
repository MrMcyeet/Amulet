package art.arcane.amulet.concurrent;

import art.arcane.amulet.functional.Run;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class J
{
    private static final ScheduledExecutorService s = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    private static final ExecutorService e = new ThreadPoolExecutor(0, Integer.MAX_VALUE,60L,TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public static Future<?> run(Runnable r)
    {
        return e.submit(r);
    }

    public static <T> T attempt(Callable<T> c)
    {
        try {
            return c.call();
        } catch (Throwable ignored) {
        }

        return null;
    }

    public static <T> T attempt(Callable<T> c, T ifFailed)
    {
        try {
            return c.call();
        } catch (Throwable ignored) {
        }

        return ifFailed;
    }

    public static ScheduledFuture<?> runIn(long msDelay, Runnable r)
    {
        return s.schedule(r, msDelay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> runAfter(long time, Runnable r)
    {
        return runIn(Math.max(time - Math.ms(), 0), r);
    }

    public static <T> ScheduledFuture<T> getIn(long msDelay, Callable<T> r)
    {
        return s.schedule(r, msDelay, TimeUnit.MILLISECONDS);
    }

    public static <T> ScheduledFuture<T> getAfter(long time, Callable<T> r)
    {
        return getIn(Math.max(time - Math.ms(), 0), r);
    }

    public static <T> Future<T> get(Callable<T> r)
    {
        return e.submit(r);
    }
}