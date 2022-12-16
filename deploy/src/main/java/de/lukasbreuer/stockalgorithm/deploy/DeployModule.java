package de.lukasbreuer.stockalgorithm.deploy;

import com.google.inject.AbstractModule;
import de.lukasbreuer.stockalgorithm.core.CoreModule;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class DeployModule extends AbstractModule {
  @Override
  protected void configure() {
    install(CoreModule.create());
  }
}
