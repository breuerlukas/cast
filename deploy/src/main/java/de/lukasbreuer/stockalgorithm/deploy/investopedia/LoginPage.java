package de.lukasbreuer.stockalgorithm.deploy.investopedia;

public class LoginPage {
}

/*
OLD IMPLEMENTATION OF LoginPage

package de.lukasbreuer.stockstrategy.investopedia;

import com.jauntium.Browser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class LoginPage implements Page {
    private final Browser browser;
    private final String username;
    private final String password;

    private static final String INVESTOPEDIA_LOGIN_URL = "https://www.investopedia.com/auth/realms/investopedia/protocol/openid-connect/auth?client_id=finance-simulator&redirect_uri=https%3A%2F%2Fwww.investopedia.com%2Fsimulator%2Fportfolio";

    @Override
    public void open() throws Exception {
        browser.visit(INVESTOPEDIA_LOGIN_URL);
        Thread.sleep(1000);
        browser.visit(browser.getLocation());
        Thread.sleep(1000);
        authenticate();
    }

    private static final String SIGN_IN_BUTTON_INPUT = "<input tabindex=\"4\" class=\"btn btn-primary\" name=\"login\" id=\"login\" type=\"submit\" value=\"Sign In\">";

    private void authenticate() throws Exception {
        browser.doc.apply(username, password);
        browser.doc.findFirst(SIGN_IN_BUTTON_INPUT).click();
        Thread.sleep(1000);
    }
}
 */