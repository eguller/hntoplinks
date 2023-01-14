package com.hntoplinks.crawler

import com.typesafe.config.ConfigFactory


object Config {
  private val conf: com.typesafe.config.Config = ConfigFactory.load();
  def get() = {
    conf
  }

}
