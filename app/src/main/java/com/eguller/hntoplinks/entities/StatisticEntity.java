package com.eguller.hntoplinks.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class StatisticEntity implements HnEntity {
  private Long   id;
  private String statKey;
  private String statValue;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStatKey() {
    return statKey;
  }

  public void setStatKey(String statKey) {
    this.statKey = statKey;
  }

  public String getStatValue() {
    return statValue;
  }

  public void setStatValue(String statValue) {
    this.statValue = statValue;
  }
}
