package interview;

import java.io.Closeable;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author hsestupin
 */
class EventDispatcher implements Runnable, Closeable {

  private final Queue<Event> eventQueue = new PriorityQueue<>();
  private final ExecutorService executor = Executors.newFixedThreadPool(
      Runtime.getRuntime().availableProcessors() + 1);

  public <T> void submit(long dateTime, Callable<T> callable) {
    synchronized (eventQueue) {
      eventQueue.offer(new Event<>(dateTime, callable, System.currentTimeMillis()));
      eventQueue.notify();
    }
  }

  @Override
  public void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        synchronized (eventQueue) {
          // delay between now and dateTime of first event from priority queue
          long delay = getDelay();
          while (delay > 0) {
            eventQueue.wait(delay);
            delay = getDelay();
          }
          System.out.println("eventQueue.size() = " + eventQueue.size());
          executor.submit(eventQueue.poll());
        }
      }
    } catch (InterruptedException ignored) {
    }
  }

  private long getDelay() {
    final Event event = eventQueue.peek();
    return event == null ? Long.MAX_VALUE : event.getDateTime() - System.currentTimeMillis();
  }

  @Override
  public void close() throws IOException {
    executor.shutdownNow();
  }
}
