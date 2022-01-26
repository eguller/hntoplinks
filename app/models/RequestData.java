package models;

import java.io.Serializable;

/**
 * User: eguller
 * Date: 4/19/14
 * Time: 12:36 AM
 */
public class RequestData implements Serializable {
  boolean captchaRequired = false;
  String  captchaText     = "";

  public RequestData(String captchaText) {
    this.captchaText     = captchaText;
    this.captchaRequired = true;
  }

  public RequestData() {
    this.captchaRequired = false;
  }

  public boolean isCaptchaRequired() {
    return captchaRequired;
  }

  public String getCaptchaText() {
    return captchaText;
  }
}
