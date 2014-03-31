package controllers;

import cache.CacheUnit;
import cache.ItemCache;

import java.util.*;

import com.hntoplinks.controller.HnController;

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

    public static void viewSubscribe(){
        renderArgs.put("subscription", new Subscriber());
        render("Application/subscribe.html");
    }

    public static void doSubscribe(Subscriber subscription){
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
            render("Application/subscribe.html");
        }
    }

    private static boolean checked(String value){
        return "on".equals(value);
    }

    public static void about(){
        render("Application/about.html");
    }

}