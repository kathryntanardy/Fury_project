package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.User;
import com.example.demo.model.UserRepository;
import com.example.demo.model.userMessage;
import com.example.demo.model.userMessageRepository;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private userMessageRepository userMsgRepo;

    @GetMapping("/signUp")
    public String signUp(@RequestParam Map<String, String> account, HttpServletResponse response, Model model) {
        System.out.println("sampe sini");
        List<User> alreadyExist = userRepo.findByUsername(account.get("username"));
        if (alreadyExist.isEmpty()) {
            String username = account.get("username");
            String password = account.get("password");
            userRepo.save(new User(username, password));
            response.setStatus(201);
            return "/user/signin";
        }
        return "/user/usernameTaken";
    }

    //Arthur note: add model to the parameters, for user centre purpose
    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> info,Model model) {
        List<User> user = userRepo.findByUsernameAndPassword(info.get("username"), info.get("password"));
        if (user.isEmpty()) {
            return "/user/loginFailed";
        }
        // added by Arthur
        model.addAttribute("userKey", user.get(0).getUid());
        return "user/userCentre";
    }


    //by Arthur
    //handle btn clicked in user centre
    @PostMapping("/buttonClicked")
    public String handleButtonClick(@RequestParam("buttonValue") String buttonValue,@RequestParam Map<String, String> uid, Model model) {
        model.addAttribute("userKey",uid.get("userKey"));
        if (buttonValue.equals("Game")) {
            return "user/game";
        } 
        if(buttonValue.equals("ContactAdmin")){
            return "user/ContactAdmin";
        }
        else {
            return "user/userCentre";
        }
    }

    //by Arthur
    //add msg to database
    @PostMapping("/userSendMsg")
    public String sendMsg(@RequestParam Map<String, String> message, Model model) {
        //Check if any of the input is/are null
        boolean goodMsg=true;
        String feedback="";
        model.addAttribute("userKey", message.get("userKey"));
        if(message.get("subject").equals("")){
            goodMsg=false;
            feedback="subject is";
            System.out.println("bad subject");
        }
        if(message.get("content").equals("")){
            goodMsg=false;
            if(feedback.equals("")){
                feedback="content is";
            }
            else{
                feedback="subject and content are";
            }
            System.out.println("bad content");
        }
        if(!goodMsg){
            feedback+=" null";
            feedback="Error: "+feedback;
            System.out.println(feedback);
            model.addAttribute("feedback", feedback);
            System.out.println("bad msg");
            return "user/sendResult";
        }

        //None of the rows are null
        System.out.println("Good msg");
        userMsgRepo.save(new userMessage(Integer.parseInt(message.get("userKey")), message.get("content"), message.get("subject"), 0));
        model.addAttribute("feedback", "Message sent!!");
        return "user/sendResult";
    }
}
