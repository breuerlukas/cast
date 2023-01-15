package de.lukasbreuer.cast.core.dataset.trade;

import de.lukasbreuer.cast.core.symbol.HistoryEntry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class TradeTime {
  @Getter
  private final int year;
  @Getter
  private final int month;
  @Getter
  private final int day;

  private static final int MARKET_OPEN_HOUR = 9;
  private static final int MARKET_OPEN_MINUTE = 30;

  public int findEntryIndex(List<HistoryEntry> data) {
    var time = new DateTime(year, month, day, MARKET_OPEN_HOUR,
      MARKET_OPEN_MINUTE, DateTimeZone.forID("US/Eastern")).getMillis() / 1000;
    return data.indexOf(data.stream()
      .filter(entry -> entry.timeStep() == time)
      .findFirst().get());
  }
}
