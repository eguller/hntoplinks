package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import com.hntoplinks.controller.HnController;

import models.*;

public class Application extends HnController {

    public static void today(Integer page){
        renderArgs.put("activeTab", "today");
        if(page == null){
            page = 1;
        }
        List<Item> items = Item.getDay(page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
        render("Application/index.html");
    }
    public static void week(Integer page){
    	renderArgs.put("activeTab", "week");
        if(page == null){
            page = 1;
        }
    	List<Item> items = Item.getWeek(page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html", items, page);
    }
    public static void month(Integer page){
    	renderArgs.put("activeTab", "month");
        if(page == null){
            page = 1;
        }
    	List<Item> items = Item.getMonth(page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html", items, page);
    }
    public static void year(Integer page){
    	renderArgs.put("activeTab", "year");
        if(page == null){
            page = 1;
        }
    	List<Item> items = Item.getYear(page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html", items, page);
    }
    public static void all(Integer page){
    	renderArgs.put("activeTab", "all");
        if(page == null){
            page = 1;
        }
    	List<Item> items = Item.getAll(page);
        renderArgs.put("items",items);
        renderArgs.put("page", page);
    	render("Application/index.html",items, page);
    }

    public static void about(){
        render("Application/about.html");
    }

}