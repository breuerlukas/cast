package de.lukasbreuer.stockalgorithm.core.database;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@RequiredArgsConstructor(staticName = "of")
public class SingleSubscriber<T> implements Subscriber<T> {
  public interface Callback<T> {
    void call(T t) throws Exception;
  }

  private enum Status {
    PENDING,
    DELIVERED
  }

  private final Callback<T> callback;
  private Status status = Status.PENDING;

  @Override
  public void onSubscribe(Subscription subscription) {
    subscription.request(1);
  }

  @Override
  public void onNext(T document) {
    try {
      callback.call(document);
      status = Status.DELIVERED;
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void onError(Throwable t) {
    t.printStackTrace();
  }

  @Override
  public void onComplete() {
    if (status == Status.PENDING) {
      try {
        callback.call(null);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}
