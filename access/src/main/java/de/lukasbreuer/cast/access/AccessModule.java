package de.lukasbreuer.cast.access;

import de.lukasbreuer.cast.access.account.AccountModule;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.DeployModule;

public final class AccessModule extends DeployModule {
  public static AccessModule create() {
    return new AccessModule();
  }

  @Override
  protected void configure() {
    super.configure();
    install(AccountModule.create());
  }

  @Override
  protected void configureLog() {
    try {
      bind(Log.class).toInstance(Log.create("Access", "/logs/access/"));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
