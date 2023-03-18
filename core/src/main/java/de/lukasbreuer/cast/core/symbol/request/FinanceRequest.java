package de.lukasbreuer.cast.core.symbol.request;

import java.util.concurrent.CompletableFuture;

public interface FinanceRequest<T> {
  CompletableFuture<T> send();
}
