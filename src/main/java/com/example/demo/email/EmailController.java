package com.example.demo.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class EmailController{

    private final EmailService emailService;
    @PostMapping("/emailConfirm")
    @ResponseBody
    public String emailConfirm(@RequestParam String email) throws Exception {

        String confirm = emailService.sendSimpleMessage(email);

        return confirm;
    }
}
