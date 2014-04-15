package controllers;

import cache.CacheUnit;
import cache.ItemCache;
import com.hntoplinks.controller.HnController;
import models.Item;
import models.Subscription;
import org.apache.commons.mail.EmailException;
import play.libs.Codec;
import utils.EmailUtil;

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
        Subscription subscription = new Subscription();
        renderArgs.put("subscription", subscription);
        render("Application/subscription.html");
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

    public static void doSubscribe(Subscription newSubscription) {
        newSubscription.fixEmailFormat();
        validation.required(newSubscription.getEmail()).message("validation.required.email");
        validation.email(newSubscription.getEmail());
        validation.isTrue(newSubscription.isDaily() || newSubscription.isWeekly() || newSubscription.isMonthly() || newSubscription.isAnnually()).message("validation.isTrue.timeperiod");
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
                renderArgs.put("subscription", newSubscription);
                render("Application/subscription_complete.html");
            }

        } else {
            renderArgs.put("subscription", newSubscription);
            render("Application/subscription.html");
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

}