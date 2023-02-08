package de.lukasbreuer.cast.core.dataset;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.cast.core.configuration.Configuration;
import de.lukasbreuer.cast.core.trade.TradeTime;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import java.util.List;

@Accessors(fluent = true)
public final class DatasetConfiguration extends Configuration {
  private static final String CONFIGURATION_PATH_FORMAT = "/configurations/dataset/%s.json";

  public static DatasetConfiguration createAndLoad(String stock) throws Exception {
    var configuration = new DatasetConfiguration(String.format(
      CONFIGURATION_PATH_FORMAT, stock));
    configuration.load();
    return configuration;
  }

  @Getter
  private List<TradeTime> buyTradeTimes;
  @Getter
  private List<TradeTime> sellTradeTimes;

  private DatasetConfiguration(String path) {
    super(path);
  }

  @Override
  protected void deserialize(JSONObject json) {
    buyTradeTimes = deserializeTradeTimes(json, "buyTradeTimes");
    sellTradeTimes = deserializeTradeTimes(json, "sellTradeTimes");
  }

  private List<TradeTime> deserializeTradeTimes(JSONObject json, String key) {
    var tradeTimes = Lists.<TradeTime>newArrayList();
    var jsonTradeTimes = json.getJSONArray(key);
    for (var i = 0; i < json.length(); i++) {
      var tradeTime = jsonTradeTimes.getJSONObject(i);
      tradeTimes.add(TradeTime.create(tradeTime.getInt("year"),
        tradeTime.getInt("month"), tradeTime.getInt("day")));
    }
    return tradeTimes;
  }
}
