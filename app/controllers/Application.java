package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	renderArgs.put("activeTab", "today");
    	List<Item> items = Item.getDay();
    	render("Application/index.html",items);
    }
    
    public static void today(){
    	index();
    }
    public static void week(){
    	renderArgs.put("activeTab", "week");
    	List<Item> items = Item.getWeek();
    	render("Application/index.html", items);
    }
    public static void month(){
    	renderArgs.put("activeTab", "month");
    	List<Item> items = Item.getMonth();
    	render("Application/index.html", items);
    }
    public static void year(){
    	renderArgs.put("activeTab", "year");
    	List<Item> items = Item.getYear();
    	render("Application/index.html", items);
    }
    public static void all(){
    	renderArgs.put("activeTab", "all");
    	List<Item> items = Item.getAll();
    	render("Application/index.html",items);
    }

    public static void about(){
        render("Application/about.html");
    }

}