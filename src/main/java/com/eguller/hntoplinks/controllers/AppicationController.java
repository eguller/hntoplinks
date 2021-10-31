package com.eguller.hntoplinks.controllers;

import com.eguller.hntoplinks.models.PageTab;
import com.eguller.hntoplinks.models.Story;
import com.eguller.hntoplinks.models.StoryPage;
import com.eguller.hntoplinks.services.StoryCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestScope
public class AppicationController {
    private static final int MAX_PAGES = 10;
    private static final int STORY_PER_PAGE = 30;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private StoryCacheService storyCacheService;

    @GetMapping("/")
    public String index(Model model){
        return today(model, "1");
    }
    @GetMapping("/{page}")
    public String index(Model model, @PathVariable("page") String page){
        return today(model, page);
    }

    @GetMapping("/today/{page}")
    public String today(Model model, String page){
        StoryPage storyPage = getStoryPage(PageTab.today, page);
        model.addAttribute("page", storyPage);
        return view("index");
    }

    @GetMapping("/week/{page}")
    public String week(Model model, String page){
        StoryPage storyPage = getStoryPage(PageTab.week, page);
        model.addAttribute("page", storyPage);
        return view("index");
    }


    private StoryPage getStoryPage(PageTab pageTab, String pageStr){
        int page = getPage(pageStr);
        List<Story> storyList;
        if(PageTab.today == pageTab){
            storyList = storyCacheService.getDailyTop();
        } else if (PageTab.week == pageTab){
            storyList = storyCacheService.getWeeklTop();
        } else {
            storyList = storyCacheService.getDailyTop();
        }

        int from = Math.min(storyList.size() - 1, (page - 1) * STORY_PER_PAGE);
        from = Math.max(0, from);
        int to = Math.min(storyList.size() - 1, page * STORY_PER_PAGE);
        to = Math.max(0, to);
        storyList = storyList.subList(from, to);
        boolean hasMoreStories = to >= storyList.size() - 1;
        StoryPage storyPage = StoryPage.builder()
                .title("Today - Hacker News Top Links")
                .activeTab(PageTab.today)
                .currentPage(page)
                .storyList(storyList)
                .hasMoreStories(hasMoreStories)
                .storyPerPage(STORY_PER_PAGE)
                .build();
        return storyPage;
    }

    private String view(String view){
        Device device = DeviceUtils.getCurrentDevice(httpServletRequest);
        if(device.isNormal()){
            return view;
        } else {
            return view + "_mobile";
        }
    }

    public int getPage(String pageStr){
        try{
            int page = Integer.parseInt(pageStr);
            if(page < 1){
                return 1;
            } else if(page > MAX_PAGES){
                return MAX_PAGES;
            } else {
                return page;
            }

        } catch (Exception ex){

        }
        return 1;
    }
}
