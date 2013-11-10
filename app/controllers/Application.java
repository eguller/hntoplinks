package controllers;

import cache.CacheUnit;
import cache.ItemCache;
import play.*;
import play.mvc.*;

import java.util.*;

import com.hntoplinks.controller.HnController;

import models.*;

public class Application extends HnController {

    public static void today(Integer page){
        renderArgs.put("activeTab", CacheUnit.TODAY);
        if(page == null){
            page = 1;
        }
        List<Item> items = ItemCache.getInstance().get(CacheUnit.TODAY, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
        render("Application/index.html");
    }
    public static void week(Integer page){
    	renderArgs.put("activeTab", CacheUnit.WEEK);
        if(page == null){
            page = 1;
        }
    	List<Item> items = ItemCache.getInstance().get(CacheUnit.WEEK, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html", items, page);
    }
    public static void month(Integer page){
    	renderArgs.put("activeTab", CacheUnit.MONTH);
        if(page == null){
            page = 1;
        }
    	List<Item> items = ItemCache.getInstance().get(CacheUnit.MONTH, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html", items, page);
    }
    public static void year(Integer page){
    	renderArgs.put("activeTab", CacheUnit.YEAR);
        if(page == null){
            page = 1;
        }
    	List<Item> items = ItemCache.getInstance().get(CacheUnit.YEAR, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html", items, page);
    }
    public static void all(Integer page){
    	renderArgs.put("activeTab", CacheUnit.ALL);
        if(page == null){
            page = 1;
        }
    	List<Item> items = ItemCache.getInstance().get(CacheUnit.ALL, page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html",items, page);
    }

    public static void about(){
        render("Application/about.html");
    }

}