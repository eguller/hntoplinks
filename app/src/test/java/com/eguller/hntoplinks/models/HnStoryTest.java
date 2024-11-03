package com.eguller.hntoplinks.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HnStoryTest {
  @Test
  public void test_getDomain() {
    var hnStory = new HnStory(null, 0, 0, 0, "Tell HN: xyz", null, null);
    var domainName = hnStory.getDomainName();
    Assertions.assertTrue(domainName.isEmpty());

    domainName = "https://xyz.com";
    hnStory = new HnStory(null, 0, 0, 0, "xyz abc", domainName, null);
    Assertions.assertEquals("xyz.com", hnStory.getDomainName());
  }
}
