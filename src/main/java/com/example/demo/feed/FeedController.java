package com.example.demo.feed;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("feed")
@Controller
public class FeedController {
    @GetMapping("")
    public String feed(){
        return "/feed/feed";
    }
    @GetMapping("/gangwon")
    public String gangwon(){
        return "/feed/gangwon";
    }
    @GetMapping("/daegu")
    public String daegu(){
        return "feed/daegu";
    }
    @GetMapping("/daejeon")
    public String Daejeon(){
        return "feed/daejeon";
    }
    @GetMapping("/busan")
    public String busan(){
        return "/feed/busan";
    }
}
