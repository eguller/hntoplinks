package controllers;

import cache.CacheUnit;
import cache.ItemCache;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.hntoplinks.controller.HnController;

import jobs.EmailSender;
import models.*;
import play.libs.Codec;

public class Application extends HnController {

    public static void today(Integer page){
        renderArgs.put("activeTab", CacheUnit.today);
        if(page == null){
            page = 1;
        }
        List<Item> items = ItemCache.getInstance().get(CacheUnit.today, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
        render("Application/index.html");
    }
    public static void week(Integer page){
    	renderArgs.put("activeTab", CacheUnit.week);
        if(page == null){
            page = 1;
        }
    	List<Item> items = ItemCache.getInstance().get(CacheUnit.week, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html", items, page);
    }
    public static void month(Integer page){
    	renderArgs.put("activeTab", CacheUnit.month);
        if(page == null){
            page = 1;
        }
    	List<Item> items = ItemCache.getInstance().get(CacheUnit.month, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html", items, page);
    }
    public static void year(Integer page){
    	renderArgs.put("activeTab", CacheUnit.year);
        if(page == null){
            page = 1;
        }
    	List<Item> items = ItemCache.getInstance().get(CacheUnit.year, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html", items, page);
    }
    public static void all(Integer page){
    	renderArgs.put("activeTab", CacheUnit.all);
        if(page == null){
            page = 1;
        }
    	List<Item> items = ItemCache.getInstance().get(CacheUnit.all, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html",items, page);
    }

    public static void viewSubscription(String subsUUid){
        renderArgs.put("subscription", new Subscription());
        render("Application/subscription.html");
    }

    public static void doSubscribe(Subscription subscription){
        subscription.fixEmailFormat();
        validation.email(subscription.getEmail());
        validation.isTrue(subscription.isDaily() || subscription.isWeekly() || subscription.isMonthly() || subscription.isAnnually());

        subscription.setSubscriptionDate(Calendar.getInstance().getTime());
        subscription.setSubsUUID(Codec.UUID().toLowerCase());
        subscription.setActivated(false);
        subscription.setActivationDate(null);
        if (!subscription.subscribedBefore()) {
            subscription.save();
            renderArgs.put("subscription", subscription);
            render("Application/subscription_complete.html");
        } else {
            renderArgs.put("subscription", subscription);
            render("Application/subscription.html");
        }
    }

    public static void unsubscribe(String subscriptionid){
        Subscription.deleteSubscription(subscriptionid);
        render("Application/unsubscribed.html");
    }

    public static void emailTemplate(){
        Item item1 = new Item("Email template test",
                "http://www.google.com",
                "google.com", "eguller", Calendar.getInstance().getTime(), 34343, 221, 17);
        List<Item> itemList = new ArrayList<Item>();
        itemList.add(item1);
        String emailTemplate = EmailSender.createHtml(itemList);
        File file = new File("template.html");
        FileWriter fw= null;
        try {
            fw = new FileWriter(file);
            fw.write(emailTemplate);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static boolean checked(String value){
        return "on".equals(value);
    }

    public static void about(){
        render("Application/about.html");
    }

}