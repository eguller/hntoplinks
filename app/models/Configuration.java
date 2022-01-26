package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: eguller
 * Date: 4/4/14
 * Time: 6:38 AM
 */
@Entity
public class Configuration extends Model {
  @Column(name = "CGROUP")
  String  group;
  @Column(name = "CKEY")
  String  key;
  @Column(name = "CVALUE")
  String  value;
  @Column(name = "OVERRIDE_PLAY_CONFIG")
  boolean overridePlayConfig;

  public static Map<String, String> getConfigurationByGroup(ConfigGroup configGroup) {
    List<Configuration> configurationList = Configuration.find("byGroup", configGroup.name()).fetch();
    Map<String, String> configMap = new HashMap<String, String>();
    for (Configuration configuration : configurationList) {
      configMap.put(configuration.getKey(), configuration.getValue());
    }
    return configMap;
  }

  public static int getConfigurationGroupSize(ConfigGroup configGroup) {
    return Configuration.find("byGroup", configGroup.name()).fetch().size();
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isOverridePlayConfig() {
    return overridePlayConfig;
  }

  public void setOverridePlayConfig(boolean overridePlayConfig) {
    this.overridePlayConfig = overridePlayConfig;
  }
}
