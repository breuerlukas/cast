package de.lukasbreuer.cast.core.symbol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class SymbolProfile {
  private final String abbreviation;
  private final String company;
  private final String industry;
  private final String website;
}
