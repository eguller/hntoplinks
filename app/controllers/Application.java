package controllers;

import cache.CacheUnit;
import cache.ItemCache;

import java.util.*;

import com.hntoplinks.controller.HnController;

import models.*;

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

    public static void about(){
        render("Application/about.html");
    }

}