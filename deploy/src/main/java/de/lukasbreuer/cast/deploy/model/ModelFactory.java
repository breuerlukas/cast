package de.lukasbreuer.cast.deploy.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.lukasbreuer.cast.core.log.Log;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.UUID;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class ModelFactory {
  @Inject
  private final Log log;

  public Model of(Document document) {
    return Model.of(log, document);
  }

  public Model create(
    UUID id, String stock, String buyModelPath, String sellModelPath,
    int buyReviewPeriod, int sellReviewPeriod, double buyTradePredictionMinimum,
    double sellTradePredictionMinimum
  ) {
    return Model.create(log, id, stock, buyModelPath, sellModelPath,
      buyReviewPeriod, sellReviewPeriod, buyTradePredictionMinimum,
      sellTradePredictionMinimum);
  }
}