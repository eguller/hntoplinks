package com.eguller.hntoplinks.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class StatsPage extends Page {
  private final Statistics statistics;
}
