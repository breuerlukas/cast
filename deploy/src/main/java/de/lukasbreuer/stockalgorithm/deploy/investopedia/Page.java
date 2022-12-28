package de.lukasbreuer.stockalgorithm.deploy.investopedia;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Page {
  private final WebDriver browser;
  private final String url;

  public void open() throws Exception {
    browser.get(url);
    Thread.sleep(2000);
  }

  protected WebDriver browser() {
    return browser;
  }
}
