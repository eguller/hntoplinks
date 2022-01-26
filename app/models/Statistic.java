package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: eguller
 * Date: 5/1/14
 * Time: 5:40 PM
 */
@Entity
@Table(name = "STATISTIC")
public class Statistic extends Model {
  @Enumerated(EnumType.STRING)
  @Column(name = "STAT_KEY")
  StatKey key;
  @Column(name = "STAT_VALUE")
  String  value;

  public Statistic() {
  }

  public Statistic(StatKey key, String value) {
    this.key   = key;
    this.value = value;
  }

  public static Map<StatKey, Statistic> getAllStatMap() {
    Map<StatKey, Statistic> statisticsMap = new HashMap<StatKey, Statistic>();
    List<Statistic> statisticList = findAll();
    for (Statistic statistic : statisticList) {
      statisticsMap.put(statistic.getKey(), statistic);
    }
    return statisticsMap;
  }

  public StatKey getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
