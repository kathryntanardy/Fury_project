package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Admin;
import com.example.demo.model.AdminRepository;
import com.example.demo.model.User;
import com.example.demo.model.UserRepository;
import com.example.demo.model.adminMessage;
import com.example.demo.model.adminMessageRepository;
import com.example.demo.model.userMessage;
import com.example.demo.model.userMessageRepository;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private userMessageRepository userMsgRepo;

    @Autowired 
    private UserRepository userRepo;

    @Autowired
    private adminMessageRepository adminMsgRepo;

    @PostMapping("/adminLogin")
    public String adminLogin(@RequestParam Map<String, String> info) {
        List<Admin> admin = adminRepo.findByUsernameAndPassword(info.get("username"), info.get("password"));

        //By A
        if(admin.size()==0){
            return "admin/failed";
        }
        else{
            return "admin/adminCentre";
        }



        // System.out.println(info.get("username"));

        // if (!info.get("username").equals("adminaccount")) {
        //     return "/admin/failed";
        // }
        // if (!info.get("password").equals("furygroup18")) {
        //     return "/admin/failed";
        // } else{
        //     return "/admin/dashboard";
        // }
            
    }

    //by 4
    //handle btn clicked
    @PostMapping("/admin/buttonClicked")
    public String handleButtonClick(@RequestParam("buttonValue") String buttonValue,@RequestParam Map<String, String> info, Model model) {
        if (buttonValue.equals("Inbox")) {
            List<userMessage> inbox=userMsgRepo.findAll();
            model.addAttribute("inbox", inbox);
            return "admin/adminInbox";
        } 
        if(buttonValue.equals("Reply")){
            model.addAttribute("mid", info.get("mid"));
            model.addAttribute("msg", userMsgRepo.findByMid(Integer.parseInt(info.get("mid"))).get(0));
            int fromUid=userMsgRepo.findByMid(Integer.parseInt(info.get("mid"))).get(0).getFromUid();
            model.addAttribute("fromUser", userRepo.findByUid(fromUid).get(0).getUsername());
            return "admin/reply";
        }
        return "admin/adminCentre";
    }

    @PostMapping("/readAdminInbox")
    public String readUserInbox(@RequestParam("buttonValue") String buttonValue,@RequestParam Map<String, String> info, Model model) {
        List<userMessage> msg =userMsgRepo.findByMid(Integer.parseInt(buttonValue));
        model.addAttribute("msg",msg.get(0));
        String fromUser=userRepo.findByUid(msg.get(0).getFromUid()).get(0).getUsername();
        model.addAttribute("fromUser", fromUser);
        return "admin/adminReadInbox";
    }

    
    //by 4
    //add msg to database
    @PostMapping("/admin/reply")
    public String sendMsg(@RequestParam Map<String, String> message, Model model) {
        //Check if any of the input is/are null
        boolean goodMsg=true;
        String feedback="";
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
        int toUid=userMsgRepo.findByMid(Integer.parseInt(message.get("mid"))).get(0).getFromUid();
        adminMsgRepo.save(new adminMessage("reply",toUid, message.get("subject"), message.get("content"),LocalDate.now()));
        model.addAttribute("feedback", "Message sent!!");
        return "admin/sendResult";
    }
}
