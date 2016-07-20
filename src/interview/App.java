package interview;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author hsestupin
 */
public class App {

  public static final int PRODUCER_THREADS = 50;
  public static final int EVENT_SPAWN_INTERVAL = 50;

  public static void main(String[] args) throws IOException, InterruptedException {
    try (EventDispatcher dispatcher = new EventDispatcher()) {

      // start dispatching
      new Thread(dispatcher).start();

      final ExecutorService producers = Executors.newCachedThreadPool();
      System.out.println("Invoking producers");
      producers.invokeAll(
          IntStream.range(0, PRODUCER_THREADS)
              .mapToObj(i -> new TestProducer(dispatcher))
              .collect(Collectors.toList()));
      System.out.println("All producers invoked");

    }
  }

  private static class TestProducer implements Callable<Void> {

    private final EventDispatcher dispatcher;

    public TestProducer(final EventDispatcher dispatcher) {
      this.dispatcher = dispatcher;
    }

    @Override
    public Void call() throws Exception {
      while (!Thread.currentThread().isInterrupted()) {
        Thread.sleep(ThreadLocalRandom.current().nextLong(EVENT_SPAWN_INTERVAL));
        long now = System.currentTimeMillis();
        long delay = ThreadLocalRandom.current().nextLong(50);
        final long dateTime = now + delay;
        dispatcher.submit(dateTime, new TestCallable(dateTime));
      }
      return null;
    }
  }

  private static class TestCallable implements Callable<Void> {

    private final long dateTime;

    public TestCallable(final long dateTime) {
      this.dateTime = dateTime;
    }

    @Override
    public Void call() throws Exception {
      final long error = System.currentTimeMillis() - dateTime;
      if (Math.abs(error) > 10) {
        System.err.println("TOO HIGH ERROR: " + error);
        System.exit(1);
      }
      System.out.println("Call time error: " + error);
      return null;
    }
  }
}
