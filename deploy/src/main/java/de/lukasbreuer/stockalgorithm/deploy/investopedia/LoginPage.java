package de.lukasbreuer.stockalgorithm.deploy.investopedia;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class LoginPage extends Page {
  private static final String PAGE_URL = "https://www.investopedia.com/auth/realms/investopedia/protocol/openid-connect/auth?client_id=finance-simulator&redirect_uri=https%3A%2F%2Fwww.investopedia.com%2Fsimulator%2Fportfolio";

  public static LoginPage create(
    WebDriver browser, String username, String password
  ) {
    return new LoginPage(browser, PAGE_URL, username, password);
  }

  private final String username;
  private final String password;

  private LoginPage(
    WebDriver browser, String url, String username, String password
  ) {
    super(browser, url);
    this.username = username;
    this.password = password;
  }

  @Override
  public void open() throws Exception {
    super.open();
    browser().findElement(By.id("username")).sendKeys(username);
    browser().findElement(By.id("password")).sendKeys(password);
    browser().findElement(By.id("login")).click();
  }
}
