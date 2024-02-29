package com.example.demo.feed;


import com.example.demo.user.User;
import com.example.demo.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("feed")
@Controller
@RequiredArgsConstructor
@Slf4j
public class FeedController {
    private final FeedService feedService;
    private final UserService userService;
    @GetMapping("")
    public String feed(Model model){
        List<Feed> feedList =feedService.findAll();
        model.addAttribute("feedList",feedList);
        return "/feed/feed";
    }
    @GetMapping("/create")
    public String create(FeedForm feedForm){
        return "/feed/create";
    }
//    public String googleLogin(@AuthenticationPrincipal OAuth2User principal,
//                              HttpServletResponse response,
//                              Model model) {
//        // OAuth2 로그인을 통해 가져온 사용자 정보
//        String loginId = principal.getAttribute("email");
//        String nickname = principal.getAttribute("name");

    @PostMapping("/create")
    public String create(Authentication auth, @Valid FeedForm feedForm, BindingResult bindingResult, @RequestParam("file") MultipartFile file) throws IOException {
        User loginUser = userService.getLoginUserByLoginId(auth.getName());
        log.info("유저정보"+loginUser);
//        if (loginUser == null) {
//            User Email = userService.getLoginUserByEmail(auth.getName());
//            model.addAttribute("user",Email);
//            log.info("이프 유저정보"+auth.getName());
//            log.info("이프 유저정보"+Email);
//        } else  {
//            model.addAttribute("user", loginUser);
//            log.info("엘쓰 유저정보"+loginUser);
//        }
        if (loginUser == null) {
            User Email =userService.getLoginUserByEmail(auth.getName());
            this.feedService.create(feedForm.getTitle(),feedForm.getContent(),feedForm.getArea(),feedForm.getAddress(),Email,file);
            log.info("loginUser 널 "+loginUser);
        } else  {
            log.info("엘쓰 유저정보"+loginUser);
            this.feedService.create(feedForm.getTitle(),feedForm.getContent(),feedForm.getArea(),feedForm.getAddress(),loginUser,file);
        }
        if(bindingResult.hasErrors()){
            return "/feed/create";
        }

        return "redirect:/feed";
    }

//    @GetMapping(value = "/detail/{id}")
//    public String detail(@PathVariable("id") Long id, Model model, ReviewForm reviewForm){
//        ShopEntity shopEntity = this.shopService.getItem(id); // 변수 이름을 shopEntity로 변경
//        // 리뷰 평균 별점을 계산하여 모델에 추가
//        Double avgRating = this.reviewService.calculateAverageRating(shopEntity);
//        model.addAttribute("avgRating", avgRating);
//        model.addAttribute("item", shopEntity);
//        return "list/Shop/itemDetail";
//    }
    @GetMapping(value = "/detail/{id}")
    public String detail(@PathVariable("id") Long id , Model model){
        Feed feed = this.feedService.getFeed(id);
        model.addAttribute("feed",feed);

        return "/feed/feedDetail";
    }
    @GetMapping("/seoul")
    public String seoul(){
        return "/feed/seoul";
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
