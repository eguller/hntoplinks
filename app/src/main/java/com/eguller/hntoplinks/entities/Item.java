package com.eguller.hntoplinks.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
  private Long id;
  private String by;
  @Builder.Default
  private int descendants = 0;
  @Builder.Default
  private int score = 0;
  private Long time;
  private String title;
  private String type;
  private String url;
  private Long parent;
  private boolean dead;
}
