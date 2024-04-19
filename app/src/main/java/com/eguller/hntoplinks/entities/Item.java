package com.eguller.hntoplinks.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
public class Item {
  private Long id;
  private String by;
  private Integer descendants;
  private Integer score;
  private Long time;
  private String title;
  private String type;
  private String url;

  public Integer getDescendants() {
    var nonNull = descendants == null ? 0 : descendants;
    if (nonNull < 0) {
      return 0;
    } else {
      return nonNull;
    }
  }

  public Integer getScore() {
    var nonNull = score == null ? 0 : score;
    if (nonNull < 0) {
      return 0;
    } else {
      return nonNull;
    }
  }
}
