package de.lukasbreuer.stockalgorithm.core.database;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

@RequiredArgsConstructor(staticName = "of")
public class FullSubscriber<T> implements Subscriber<T> {
  public interface Callback<T> {
    void call(T t) throws Exception;
  }

  private final Callback<List<T>> callback;
  private final List<T> documents = Lists.newArrayList();

  @Override
  public void onSubscribe(Subscription subscription) {
    subscription.request(Integer.MAX_VALUE);
  }

  @Override
  public void onNext(T document) {
    documents.add(document);
  }

  @Override
  public void onError(Throwable t) {
    t.printStackTrace();
  }

  @Override
  public void onComplete() {
    try {
      callback.call(documents);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
