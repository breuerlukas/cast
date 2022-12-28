package de.lukasbreuer.stockalgorithm.deploy.investopedia;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class HomePage extends Page {
  private static final String PAGE_URL = "https://www.investopedia.com/simulator/";

  public static HomePage create(WebDriver browser) {
    return new HomePage(browser, PAGE_URL);
  }

  private HomePage(WebDriver browser, String url) {
    super(browser, url);
  }

  @Override
  public void open() throws Exception {
    super.open();
    acceptRightsInformation();
    clickForwardingButton();
  }

  private void acceptRightsInformation() throws Exception {
    browser().findElement(By.id("onetrust-accept-btn-handler")).click();
    Thread.sleep(500);
  }

  private void clickForwardingButton() {
    browser().findElement(By.cssSelector("button[data-cy='landing-page-hero-login-button']"))
      .click();
  }
}
