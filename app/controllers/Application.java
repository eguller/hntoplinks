package controllers;

import cache.CacheUnit;
import cache.IPCache;
import cache.ItemCache;
import com.hntoplinks.controller.HnController;
import models.Item;
import models.RequestData;
import models.Subscription;
import org.apache.commons.mail.EmailException;
import play.cache.Cache;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Http;
import utils.EmailUtil;
import utils.TimeUtil;

import java.util.Calendar;
import java.util.List;

public class Application extends HnController {

    public static void today(Integer page) {
        renderArgs.put("activeTab", CacheUnit.today);
        if (page == null) {
            page = 1;
        }
        List<Item> items = ItemCache.getInstance().get(CacheUnit.today, page);
        renderArgs.put("items", items);
        renderArgs.put("page", page);
        render("Application/index.html");
    }

    public static void week(Integer page) {
        renderArgs.put("activeTab", CacheUnit.week);
        if (page == null) {
            page = 1;
        }
        List<Item> items = ItemCache.getInstance().get(CacheUnit.week, page);
        renderArgs.put("items", items);
        renderArgs.put("page", page);
        render("Application/index.html");
    }

    public static void month(Integer page) {
        renderArgs.put("activeTab", CacheUnit.month);
        if (page == null) {
            page = 1;
        }
        List<Item> items = ItemCache.getInstance().get(CacheUnit.month, page);
        renderArgs.put("items", items);
        renderArgs.put("page", page);
        render("Application/index.html");
    }

    public static void year(Integer page) {
        renderArgs.put("activeTab", CacheUnit.year);
        if (page == null) {
            page = 1;
        }
        List<Item> items = ItemCache.getInstance().get(CacheUnit.year, page);
        renderArgs.put("items", items);
        renderArgs.put("page", page);
        render("Application/index.html");
    }

    public static void all(Integer page) {
        renderArgs.put("activeTab", CacheUnit.all);
        if (page == null) {
            page = 1;
        }
        List<Item> items = ItemCache.getInstance().get(CacheUnit.all, page);
        renderArgs.put("items", items);
        renderArgs.put("page", page);
        render("Application/index.html");
    }

    public static void viewSubscription() {
        String randomId = Codec.UUID();
        String ip = getClientIp();
        boolean captchaRequired = IPCache.getInstance().checkRequired(ip);

        Subscription subscription = new Subscription();
        renderArgs.put("subscription", subscription);
        renderArgs.put("randomId", randomId);
        if (captchaRequired) {
            renderArgs.put("captchaRequired", true);
        } else {
            Cache.set(randomId, new RequestData(), "10mn");
        }
        render("Application/subscription.html");
    }

    public static void captcha(String randomId) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#000000");
        Cache.set(randomId, new RequestData(code), "10mn");
        renderBinary(captcha);
    }

    public static void viewModifySubscription(String subscriptionid) {
        Subscription existingSubscription = Subscription.findBySubscriptionId(subscriptionid);
        if (existingSubscription == null) {
            renderArgs.put("message", String.format("Subscription for id %s was not found", subscriptionid));
            render("Application/message.html");
        } else {
            renderArgs.put("existingSubscription", existingSubscription);
            render("Application/modify_subscription.html");
        }
    }

    public static void doSubscribe(Subscription newSubscription, String randomId, String captchaText, int timeZoneOffSet) {
        newSubscription.fixEmailFormat();
        validation.required(newSubscription.getEmail()).message("validation.required.email");
        validation.email(newSubscription.getEmail());
        validation.isTrue(newSubscription.isDaily() || newSubscription.isWeekly() || newSubscription.isMonthly() || newSubscription.isAnnually()).message("validation.isTrue.timeperiod");

        if (randomId == null) {
            renderArgs.put("message", "Form data manually edited. Please open subscription page again.");
            render("Application/message.html");
        } else {
            RequestData requestData = (RequestData) Cache.get(randomId);
            if (requestData != null) {
                if (requestData.isCaptchaRequired()) {
                    renderArgs.put("captchaRequired", true);
                    if (captchaText == null || captchaText.trim().length() == 0) {
                        validation.addError("captchaText", "Code field cannot be empty.");
                    } else if (!requestData.getCaptchaText().equalsIgnoreCase(captchaText)) {
                        validation.addError("captchaText", "Your input does not match with code.");
                    }
                }
            } else {
                renderArgs.put("message", "Session expired. Please open subscription page again.");
                render("Application/message.html");
            }

            if (newSubscription.subscribedBefore()) {
                validation.addError("subscription.email", "This email address is already registered");
            }
            if (!validation.hasErrors()) {
                newSubscription.setSubscriptionDate(Calendar.getInstance().getTime());
                newSubscription.setSubsUUID(Codec.UUID().toLowerCase());
                newSubscription.setActivationDate(null);

                try {
                    EmailUtil.sendActivationEmail(newSubscription, newSubscription.getEmail());
                    newSubscription.setActivated(false);
                } catch (EmailException e) {
                    newSubscription.setActivated(true);
                    e.printStackTrace();
                } finally {
                    newSubscription.save();
                    IPCache.getInstance().addIp(getClientIp());
                    Cache.delete(randomId);
                    renderArgs.put("subscription", newSubscription);
                    render("Application/subscription_complete.html");
                }

            } else {
                renderArgs.put("subscription", newSubscription);
                renderArgs.put("randomId", randomId);
                render("Application/subscription.html");
            }
        }
    }

    public static void modifySubscription(Subscription subscription) {
        Subscription subscriptionFromDB = Subscription.findBySubscriptionId(subscription.getSubsUUID());
        if (subscriptionFromDB == null) {
            renderArgs.put("message", String.format("Subscription for id %s was not found", subscription.getSubsUUID()));
            render("Application/message.html");
        } else {
            subscriptionFromDB.update(subscription);
            renderArgs.put("message", "Your subscription was updated.");
            render("Application/message.html");
        }
    }

    public static void unsubscribe(String subscriptionid) {
        Subscription.deleteSubscription(subscriptionid);
        String message = "You have unsubscribed. Bye...";
        renderArgs.put("message", message);
        render("Application/message.html");
    }


    public static void activate(String subscriptionid) {
        Subscription subscription = Subscription.findBySubscriptionId(subscriptionid);
        if (subscription == null) {
            renderArgs.put("message", String.format("Error!</br> Subscription id %s does not exist in our system.", subscriptionid));
            render("Application/message.html");
        } else {
            if (!subscription.isActivated()) {
                subscription.setActivated(true);
                subscription.setActivationDate(Calendar.getInstance().getTime());
                subscription.save();
                renderArgs.put("message", "Congratulations! <br/> Your subscription has been activated. <br/> You will receive periodic e-mail from now on.");
                render("Application/message.html");
            } else {
                renderArgs.put("message", "Your subscription has already been activated.");
                render("Application/message.html");
            }
        }
    }

    private static boolean checked(String value) {
        return "on".equals(value);
    }

    public static void about() {
        render("Application/about.html");
    }

    public static String getClientIp() {
        Http.Header xForwardedForHeader = request.headers.get("x-forwarded-for");
        if (xForwardedForHeader != null) {
            List<String> values = xForwardedForHeader.values;
            String lastValue = values.get(values.size() - 1);
            if (lastValue == null || lastValue.trim().length() == 0) {
                return request.remoteAddress;
            } else {
                String[] lastValArr = lastValue.split(",");
                return lastValArr[lastValArr.length - 1].trim();
            }
        } else {
            return request.remoteAddress;
        }

    }

    public static void timeZone(){
        TimeUtil.findTimeZoneFromOffset(600);
    }

}