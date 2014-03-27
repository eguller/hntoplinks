package controllers;

import cache.CacheUnit;
import cache.ItemCache;

import java.util.*;

import com.hntoplinks.controller.HnController;

import models.*;
import play.data.validation.Required;

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
        renderArgs.put("daily", "");
        renderArgs.put("weekly", "checked");
        renderArgs.put("annually", "");
        renderArgs.put("email", "");
        render("Application/subscribe.html");
    }

    public static void doSubscribe(){
        String email = params.get("email");
        boolean daily = checked(params.get("daily"));
        boolean weekly = checked(params.get("weekly"));
        boolean monthly = checked(params.get("monthly"));
        boolean annually = checked(params.get("annually"));
        validation.email(email);
        validation.isTrue(daily || weekly || monthly || annually);
        System.out.println("stop");
    }

    private static boolean checked(String value){
        return "on".equals(value);
    }

    public static void about(){
        render("Application/about.html");
    }

}