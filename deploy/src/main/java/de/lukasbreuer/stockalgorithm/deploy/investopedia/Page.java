package de.lukasbreuer.stockalgorithm.deploy.investopedia;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Page {
  private final WebDriver browser;
  private final String url;

  public void open() {
    browser.get(url);
  }
}
