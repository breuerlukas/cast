package de.lukasbreuer.stockalgorithm.deploy.investopedia;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class LoginPage extends Page {
  public static LoginPage create(
    WebDriver browser, String username, String password
  ) {
    return new LoginPage(browser, "", username, password);
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
    browser().findElement(By.id("login")).click();
    browser().findElement(By.id("username")).sendKeys(username);
    browser().findElement(By.id("password")).sendKeys(password);
    browser().findElement(By.id("login")).click();
  }
}
