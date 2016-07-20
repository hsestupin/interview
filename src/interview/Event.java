package interview;

import java.util.concurrent.Callable;

/**
 * @author hsestupin
 */
class Event<T> implements Callable<T>, Comparable<Event> {

  private final long dateTime;
  private final Callable<T> callable;
  private final long arrivalTime;

  Event(long dateTime, Callable<T> callable, long arrivalTime) {
    this.dateTime = dateTime;
    this.callable = callable;
    this.arrivalTime = arrivalTime;
  }

  public long getDateTime() {
    return dateTime;
  }

  @Override
  public int compareTo(Event other) {
    if (other == null) {
      throw new NullPointerException();
    }
    if (dateTime != other.dateTime) {
      return dateTime < other.dateTime ? -1 : 1;
    }
    if (arrivalTime == other.arrivalTime) {
      // non-deterministic order for PriorityQueue
      return 0;
    }
    return arrivalTime < other.arrivalTime ? -1 : 1;
  }

  @Override
  public T call() throws Exception {
    return callable.call();
  }
}
