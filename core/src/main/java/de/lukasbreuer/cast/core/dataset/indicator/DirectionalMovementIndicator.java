 package de.lukasbreuer.cast.core.dataset.indicator;

import de.lukasbreuer.cast.core.symbol.HistoryEntry;

import java.util.List;

public final class DirectionalMovementIndicator extends ReviewIndicator {
  public static DirectionalMovementIndicator create(List<HistoryEntry> data) {
    var indicator = new DirectionalMovementIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private DirectionalMovementIndicator(List<HistoryEntry> data) {
    super(data);
  }

  private static final int AVERAGE_REVIEW = 5;

  @Override
  public double calculate(int index, int review) {
    var directionalIndexSum = 0D;
    for (var i = 0; i < AVERAGE_REVIEW; i++) {
      directionalIndexSum += calculateDirectionalIndex(index - i, review);
    }
    return directionalIndexSum / AVERAGE_REVIEW;
  }

  private double calculateDirectionalIndex(int index, int review) {
    var trueRangeSum = calculateTrueRangeSum(index, review);
    var positiveDirectionalMovementSum = calculateDirectionalMovementSum(index, review, +1);
    var negativeDirectionalMovementSum = calculateDirectionalMovementSum(index, review, -1);
    var positiveDirectionalIndicator = positiveDirectionalMovementSum / trueRangeSum;
    var negativeDirectionalIndicator = negativeDirectionalMovementSum / trueRangeSum;
    var directionalIndicatorDifference = Math.abs(positiveDirectionalIndicator -
      negativeDirectionalIndicator);
    var directionalIndicatorSum = positiveDirectionalMovementSum + negativeDirectionalMovementSum;
    if (directionalIndicatorSum == 0) {
      return 0;
    }
    return directionalIndicatorDifference / directionalIndicatorSum;
  }

  private double calculateDirectionalMovementSum(
    int currentIndex, int review, int direction
  ) {
    var sum = 0D;
    for (var i = 0; i < review; i++) {
      sum += calculateDirectionalMovement(currentIndex - i, direction);
    }
    return sum;
  }

  private double calculateTrueRangeSum(int currentIndex, int review) {
    var sum = 0D;
    for (var i = 0; i < review; i++) {
      sum += calculateTrueRange(currentIndex - i);
    }
    return sum;
  }

  private double calculateDirectionalMovement(int currentIndex, int direction) {
    var positiveDirectionalMovement = data().get(currentIndex).high() -
      data().get(currentIndex - 1).high();
    var negativeDirectionalMovement = data().get(currentIndex - 1).low() -
      data().get(currentIndex).low();
    if (direction > 1 && negativeDirectionalMovement > positiveDirectionalMovement) {
      return 0;
    }
    if (direction < 1 && positiveDirectionalMovement > negativeDirectionalMovement) {
      return 0;
    }
    if (direction > 1) {
      return Math.max(0, positiveDirectionalMovement);
    }
    return Math.max(0, negativeDirectionalMovement);
  }

  private double calculateTrueRange(int currentIndex) {
    return Math.max(data().get(currentIndex).high() - data().get(currentIndex).low(),
      Math.max(Math.abs(data().get(currentIndex).high() - data().get(currentIndex - 1).close()),
        Math.abs(data().get(currentIndex).low() - data().get(currentIndex - 1).close())));
  }
}
