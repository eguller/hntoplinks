package com.eguller.hntoplinks.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("statistic")
public class StatisticEntity implements HnEntity {
  @Id
  @Column("id")
  private Long   id;
  @Column("stat_key")
  private String statKey;
  @Column("stat_value")
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
