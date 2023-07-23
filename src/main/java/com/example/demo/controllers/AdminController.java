package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    @GetMapping("/admin/login")
    public String goSignin(@RequestParam Map<String, String> info, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_user");
        if (admin == null) {
            return "admin/login";
        } else {
            model.addAttribute("user", admin);
            model.addAttribute("username", admin.getUsername());
            return "admin/adminCentre";
        }
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam Map<String, String> info, Model model,
            HttpServletRequest request, HttpSession session) {
        List<Admin> adminList = adminRepo.findByUsernameAndPassword(info.get("username"),
                info.get("password"));
        Boolean badLogin = false;
        Boolean blankName = info.get("username").equals("");
        Boolean blankPassword = info.get("password").equals("");
        Boolean wrongPassword = adminRepo.findByUsername(info.get("username")).size() != 0 && adminList.size() == 0;
        Boolean noUser = adminRepo.findByUsername(info.get("username")).size() == 0;
        if (blankName) {
            model.addAttribute("usernameAlert", "Username required");
            badLogin = true;
        }

        if (blankPassword) {
            model.addAttribute("passwordAlert", "Password required");
            badLogin = true;
        }
        if (noUser && !blankPassword) {
            model.addAttribute("usernameAlert", "Admin not found");
            model.addAttribute("passwordAlert", "");
            badLogin = true;
        }
        if (!blankName && !blankPassword && wrongPassword) {
            model.addAttribute("passwordAlert", "Wrong password");
            badLogin = true;
        }
        if (badLogin) {
            return "admin/login";
        } else {
            Admin admin = adminList.get(0);
            request.getSession().setAttribute("session_user", admin);
            model.addAttribute("admin", admin);
            model.addAttribute("username", admin.getUsername());
            return "admin/adminCentre";
        }
    }

    // by 4
    // handle btn clicked
    @PostMapping("/admin/buttonClicked")
    public String handleButtonClick(@RequestParam("buttonValue") String buttonValue,
            @RequestParam Map<String, String> info, Model model, HttpServletRequest request, HttpSession session) {
        if (buttonValue.equals("Inbox")) {
            List<userMessage> inbox = userMsgRepo.findAll();
            model.addAttribute("inbox", inbox);
            return "admin/adminInbox";
        }
        if (buttonValue.equals("Reply")) {
            userMessage msg = userMsgRepo.findByMid(Integer.parseInt(info.get("mid"))).get(0);
            model.addAttribute("mid", info.get("mid"));
            model.addAttribute("msg", msg);
            int fromUid = userMsgRepo.findByMid(Integer.parseInt(info.get("mid"))).get(0).getFromUid();
            String fromUser = userRepo.findByUid(fromUid).get(0).getUsername();
            model.addAttribute("subject", "Re: " + msg.getSubject());
            model.addAttribute("content", "Dear " + fromUser + ",\n\n" + "Best,\n" + "Team Fury\n\n" + "[Quote: \n"
                    + msg.getContent() + "\n]");
            model.addAttribute("fromUser", fromUser);
            return "admin/reply";
        }
        if (buttonValue.equals("playerDatabase")) {
            List<User> userList = userRepo.findAll();
            model.addAttribute("userNum", userList.size());
            model.addAttribute("userList", userList);
            return "admin/playerDatabase";
        }
        if (buttonValue.equals("LogOut")) {
            request.getSession().invalidate();
            return "admin/login";
        }
        if (buttonValue.equals("trend")) {
            int currentYear = LocalDate.now().getYear();
            model.addAttribute("year", currentYear);
            model.addAttribute("data", activeUserInYear(currentYear));
            model.addAttribute("type", "Active Users");
            model.addAttribute("otherType", "Cumulative users");
            return "admin/trend";
        }
        if (buttonValue.equals("announcement")) {
            return "admin/announcement";
        }
        return "admin/adminCentre";
    }

    @PostMapping("/admin/announcement")
    public String postAnnouncement(@RequestParam Map<String, String> message, Model model) {
        boolean goodMsg = true;
        String feedback = "";
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
            model.addAttribute("mid", message.get("mid"));
            model.addAttribute("btnValue", "Reply");
            model.addAttribute("btnText", "Back to Reply");
            System.out.println("bad msg");
            return "admin/sendResult";
        }

        // None of the rows are null
        System.out.println("Good msg");
        adminMsgRepo.save(new adminMessage("Announcement", 0, message.get("subject"), message.get("content"),
                LocalDate.now(), "N"));
        model.addAttribute("feedback", "Message sent!!");
        model.addAttribute("btnValue", "back");
        model.addAttribute("btnText", "Back to admin centre");
        return "admin/sendResult";
    }

    @PostMapping("/admin/trend")
    public String switchYear(@RequestParam("buttonValue") String buttonValue, @RequestParam Map<String, String> info,
            Model model) {
        int year = Integer.parseInt(info.get("year"));
        String type = info.get("type");
        String otherType = info.get("otherType");
        String data = "";
        if (buttonValue.equals("switchType")) {
            otherType = info.get("type");
            if (type.equals("Active Users")) {
                type = "Cumulative users";
            } else {
                type = "Active Users";
            }
        } else if (buttonValue.equals("nextYear")) {
            year++;
        } else if (buttonValue.equals("previousYear")) {
            year--;
        }

        if (type.equals("Active Users")) {
            data = activeUserInYear(year);
        } else {
            data = commulativUserInYear(year);
        }
        model.addAttribute("type", type);
        model.addAttribute("otherType", otherType);
        model.addAttribute("year", year);
        model.addAttribute("data", data);
        return "admin/trend";
    }

    private String activeUserInYear(int year) {
        List<User> allUsers = userRepo.findAll();
        allUsers.sort(Comparator.comparing(User::getLastLoginDate));
        int[] dataArr = new int[12];
        String data = "";
        for (User u : allUsers) {
            LocalDate lastLogin = u.getLastLoginDate();
            if (lastLogin.getYear() == year) {
                int month = lastLogin.getMonthValue();
                dataArr[month - 1]++;
            }
        }
        for (int i : dataArr) {
            data += i + ",";
        }
        return data.substring(0, data.length() - 1);
    }

    private String commulativUserInYear(int year) {
        List<User> allUsers = userRepo.findAll();
        allUsers.sort(Comparator.comparing(User::getLastLoginDate));
        int[] dataArr = new int[12];
        String data = "";
        for (User u : allUsers) {
            LocalDate registerDate = u.getRegisterDate();
            if (registerDate.getYear() < year) {
                for (int i = 0; i < 12; i++) {
                    dataArr[i]++;
                }
            } else if (registerDate.getYear() == year) {
                int month = registerDate.getMonthValue();
                for (int i = month - 1; i < 12; i++) {
                    dataArr[i]++;
                }
            }

        }
        for (int i : dataArr) {
            data += i + ",";
        }
        return data.substring(0, data.length() - 1);
    }

    @PostMapping("/admin/readInbox")
    public String readUserInbox(@RequestParam("buttonValue") String buttonValue, @RequestParam Map<String, String> info,
            Model model) {
        List<userMessage> msg = userMsgRepo.findByMid(Integer.parseInt(buttonValue));
        model.addAttribute("msg", msg.get(0));
        String fromUser = userRepo.findByUid(msg.get(0).getFromUid()).get(0).getUsername();
        model.addAttribute("fromUser", fromUser);
        return "admin/adminReadInbox";
    }

    // by 4
    // add msg to database
    @PostMapping("/admin/reply")
    public String sendMsg(@RequestParam Map<String, String> message, Model model) {
        // return "/admin/adminCentre";
        // Check if any of the input is/are null
        boolean goodMsg = true;
        String feedback = "";
        if (message.get("subject").equals("")) {
            goodMsg = false;
            feedback = "subject is";
            System.out.println("bad subject");
        }
        if (message.get("content").equals("") || message.get("content").equals(message.get("tempContent"))) {
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
            model.addAttribute("mid", message.get("mid"));
            model.addAttribute("btnValue", "Reply");
            model.addAttribute("btnText", "Back to Reply");
            System.out.println("bad msg");
            return "admin/sendResult";
        }

        // None of the rows are null
        System.out.println("Good msg");
        userMessage msgFromUser = userMsgRepo.findByMid(Integer.parseInt(message.get("mid"))).get(0);
        int toUid = msgFromUser.getFromUid();
        msgFromUser.setSolved("Y");
        userMsgRepo.save(msgFromUser);
        adminMsgRepo.save(
                new adminMessage("Reply", toUid, message.get("subject"), message.get("content"), LocalDate.now(), "N"));
        model.addAttribute("feedback", "Message sent!!");
        model.addAttribute("btnValue", "back");
        model.addAttribute("btnText", "Back to admin centre");
        return "admin/sendResult";
    }

    @PostMapping("/admin/handleUser")
    public String handleUser(HttpServletRequest request, Model model) {
        String[] selectedUsers = request.getParameterValues("userRow");
        String uidList = "";
        if (selectedUsers != null) {
            for (String uid : selectedUsers) {
                uidList += uid + ",";
            }
        }
        System.out.println(uidList);
        return "admin/adminCentre";
    }

}
