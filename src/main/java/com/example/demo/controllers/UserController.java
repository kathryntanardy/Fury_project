package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.User;
import com.example.demo.model.UserRepository;
import com.example.demo.model.adminMessage;
import com.example.demo.model.adminMessageRepository;
import com.example.demo.model.userMessage;
import com.example.demo.model.userMessageRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private userMessageRepository userMsgRepo;

    @Autowired
    private adminMessageRepository adminMsgRepo;

    @GetMapping("/signUp")
    public String signUp(@RequestParam Map<String, String> account, HttpServletResponse response, Model model) {
        System.out.println("sampe sini");
        List<User> alreadyExist = userRepo.findByUsername(account.get("username"));
        if (alreadyExist.isEmpty()) {
            String username = account.get("username");
            String password = account.get("password");
            userRepo.save(new User(username, password));
            response.setStatus(201);
            return "user/signin";
        }
        return "user/usernameTaken";
    }

 @GetMapping("/login")
    public String getLogin(Model model, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "/user/loginFailed";
        } else {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("uid", user.getUid());
            return "/user/userCentre";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> info, Model model,
            HttpServletRequest request, HttpSession session) {
        List<User> userList = userRepo.findByUsernameAndPassword(info.get("username"),
                info.get("password"));
        if (userList.isEmpty()) {
            return "/user/loginFailed";
        } else {
            User user = userList.get(0);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("uid", user.getUid());
            return "user/userCentre";
        }
    }
  

    // by 4
    // handle btn clicked
    @PostMapping("/user/buttonClicked")
    public String handleButtonClick(@RequestParam("buttonValue") String buttonValue,
            @RequestParam Map<String, String> info, Model model,
            HttpServletRequest request, HttpSession session) {
        model.addAttribute("uid", info.get("uid"));
        if (buttonValue.equals("Game")) {
            return "user/game";
        }
        if (buttonValue.equals("ContactAdmin")) {
            return "user/ContactAdmin";
        }
        if (buttonValue.equals("Inbox")) {
            List<adminMessage> inbox = adminMsgRepo.findByToUid(Integer.parseInt(info.get("uid")));
            System.out.println("Number of msg: " + inbox.size());
            model.addAttribute("inbox", inbox);
            return "user/userInbox";
        }
        if (buttonValue.equals("Statistic")) {
            List<User> user = userRepo.findByUid(Integer.parseInt(info.get("uid")));
            ArrayList<Float> userRecords = user.get(0).getRecords();
            // Handle avg and best records
            Float sum = 0f;
            for (Float f : userRecords) {
                sum += f;
            }
            int recordSize = userRecords.size();
            user.get(0).setAverageRecord((sum * 1f) / recordSize);
            if (recordSize > 0) {
                model.addAttribute("avgRecord", user.get(0).getAverageRecord() + " cpm");
                model.addAttribute("bestRecord", user.get(0).getBestRecord() + " cpm");
            } else {
                model.addAttribute("avgRecord", "N/A");
                model.addAttribute("bestRecord", "N/A");
                model.addAttribute("recordSize", 0);
            }

            // Handle stat for line graph
            String lastTen = "";
            if (recordSize > 0 && recordSize <= 10) {
                for (Float f : userRecords) {
                    lastTen += f + ",";
                }
                model.addAttribute("recordSize", recordSize);
            } else if (recordSize > 10) {
                for (int k = 0; k < 10; k++) {
                    lastTen = userRecords.get(recordSize - 1 - k) + "," + lastTen;
                }
                model.addAttribute("recordSize", 10);
            }
            model.addAttribute("userRecords", lastTen);
            return "user/statistic";
        }
        if (buttonValue.equals("LogOut")) {
            request.getSession().invalidate();
            return "user/logoutSuccess";
        } else {
            List<User> user = userRepo.findByUid(Integer.parseInt(info.get("uid")));
            model.addAttribute("username", user.get(0).getUsername());
            return "user/userCentre";
        }
    }

    // by 4
    // read msg from admin by its md in table: admin_message
    @PostMapping("/user/readInbox")
    public String readUserInbox(@RequestParam("buttonValue") String buttonValue, @RequestParam Map<String, String> info,
            Model model) {
        model.addAttribute("uid", info.get("uid"));
        adminMessage msg = adminMsgRepo.findByMid(Integer.parseInt(buttonValue)).get(0);
        msg.setRead("Y");
        adminMsgRepo.save(msg);
        model.addAttribute("msg", msg);
        return "user/userReadInbox";
    }

    // by 4
    // add msg to database
    @PostMapping("/userSendMsg")
    public String sendMsg(@RequestParam Map<String, String> message, Model model) {
        // Check if any of the input is/are null
        boolean goodMsg = true;
        String feedback = "";
        model.addAttribute("uid", message.get("uid"));
        if (message.get("subject").equals("")) {
            goodMsg = false;
            feedback = "subject is";
            System.out.println("bad subject");
        }
        if (message.get("content").equals("")) {
            goodMsg = false;
            if (feedback.equals("")) {
                feedback = "content is";
            } else {
                feedback = "subject and content are";
            }
            System.out.println("bad content");
        }
        if (!goodMsg) {
            feedback += " null";
            feedback = "Error: " + feedback;
            System.out.println(feedback);
            model.addAttribute("feedback", feedback);
            System.out.println("bad msg");
            return "user/sendResult";
        }

        // None of the rows are null
        System.out.println("Good msg");
        userMsgRepo.save(new userMessage(Integer.parseInt(message.get("uid")), message.get("subject"),
                message.get("content"), "N", LocalDate.now()));
        model.addAttribute("feedback", "Message sent!!");
        return "user/sendResult";
    }

    @PostMapping("/submitWPM")
    public String addRecord(@RequestParam Map<String, String> wpm, Model model, HttpSession session,
            HttpServletRequest request) {
        User user = (User) session.getAttribute("session_user");
        user.addRecords(Float.parseFloat(wpm.get("wpm")));
        userRepo.save(user);
        return "/user/game";
    }
}
