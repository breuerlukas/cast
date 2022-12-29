package de.lukasbreuer.cast.access;

import com.google.inject.AbstractModule;
import de.lukasbreuer.cast.access.account.AccountModule;
import de.lukasbreuer.cast.deploy.DeployModule;

public final class AccessModule extends AbstractModule {
  @Override
  protected void configure() {
    install(DeployModule.create());
    install(AccountModule.create());
  }
}
