package com.example.demo.controllers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.example.demo.model.activeUsers;
import com.example.demo.model.activeUsersRepository;
import com.example.demo.model.adminMessage;
import com.example.demo.model.adminMessageRepository;
import com.example.demo.model.ladderBoard;
import com.example.demo.model.ladderRepository;
import com.example.demo.model.userMessage;
import com.example.demo.model.userMessageRepository;
import com.example.demo.service.EmailSenderService;

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

    @Autowired
    private ladderRepository ladderRepo;

    @Autowired
    private activeUsersRepository activeUsersRepo;

    @Autowired
    private EmailSenderService service;

    Random random = new Random(1000);

    // @GetMapping("/signUp")
    // public String signUp(@RequestParam Map<String, String> account,
    // HttpServletResponse response, Model model) {
    // System.out.println("sampe sini");
    // List<User> alreadyExist = userRepo.findByUsername(account.get("username"));
    // if (alreadyExist.isEmpty()) {
    // String username = account.get("username");
    // String password = account.get("password");
    // String email = account.get("email");
    // userRepo.save(new User(username, password, email));
    // response.setStatus(201);
    // return "user/signin";
    // }
    // return "user/usernameTaken";
    // }
    @GetMapping("/user/signUp")
    public String goSignUp() {
        return "user/signup";
    }

    @PostMapping("/user/signUp")
    public String signUp(@RequestParam Map<String, String> account, HttpServletResponse response, Model model) {
        System.out.println("sampe sini");
        List<User> alreadyExist = userRepo.findByUsername(account.get("username"));
        Boolean emptyUsername = account.get("username").equals("");
        Boolean emptyPassword = account.get("password").equals("");
        Boolean emptyRePassword = account.get("retypePassword").equals("");
        Boolean emptyEmail = account.get("email").equals("");
        Boolean badSignUp = false;
        if (emptyUsername) {
            model.addAttribute("usernameAlert", "Username is required");
            badSignUp = true;
        }
        if (emptyPassword) {
            model.addAttribute("passwordAlert", "Password is required");
            badSignUp = true;
        }
        if (emptyRePassword) {
            model.addAttribute("rePasswordAlert", "Please confirm your password");
            badSignUp = true;
        }
        if (emptyEmail) {
            model.addAttribute("emailAlert", "Email is required");
            badSignUp = true;
        }
        if (!emptyPassword && !emptyRePassword && !account.get("password").equals(account.get("retypePassword"))) {
            model.addAttribute("rePasswordAlert", "Not same as password");
            badSignUp = true;
        }
        if (!emptyUsername && alreadyExist.size() != 0) {
            model.addAttribute("usernameAlert", "Username already exsit!");
            badSignUp = true;
        }

        if (badSignUp) {
            return "user/signup";
        }
        String username = account.get("username");
        String password = account.get("password");
        String email = account.get("email");
        User newUser = new User(username, password, email);
        newUser.setRegisterDate(LocalDate.now());
        userRepo.save(newUser);
        response.setStatus(201);
        return "user/signin";

        // if (alreadyExist.isEmpty()) {
        // String username = account.get("username");
        // String password = account.get("password");
        // //userRepo.save(new User(username, password, email));
        // response.setStatus(201);
        // return "user/login";
        // }
        // return "user/usernameTaken";
    }

    @GetMapping("/user/login")
    public String goSignin(@RequestParam Map<String, String> info, Model model, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "user/signin";
        } else {
            user.setLastLoginDate(LocalDate.now());
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("uid", user.getUid());
            return "user/userCentre";
        }
    }

    @PostMapping("/user/login")
    public String login(@RequestParam Map<String, String> info, Model model,
            HttpServletRequest request, HttpSession session) {
        List<User> userList = userRepo.findByUsernameAndPassword(info.get("username"),
                info.get("password"));
        Boolean badLogin = false;
        Boolean blankName = info.get("username").equals("");
        Boolean blankPassword = info.get("password").equals("");
        Boolean wrongPassword = userRepo.findByUsername(info.get("username")).size() != 0 && userList.size() == 0;
        Boolean noUser = userRepo.findByUsername(info.get("username")).size() == 0;
        if (blankName) {
            model.addAttribute("usernameAlert", "Username required");
            badLogin = true;
        }

        if (blankPassword) {
            model.addAttribute("passwordAlert", "Password required");
            badLogin = true;
        }
        if (noUser && !blankPassword) {
            model.addAttribute("usernameAlert", "User not found");
            model.addAttribute("passwordAlert", "");
            badLogin = true;
        }
        if (!blankName && !blankPassword && wrongPassword) {
            model.addAttribute("passwordAlert", "Wrong password");
            badLogin = true;
        }
        if (badLogin) {
            return "user/signin";
        } else {
            User user = userList.get(0);

            userRepo.save(user);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("uid", user.getUid());
            handleActiveUser(user.getUid(), LocalDate.now());
            return "user/userCentre";
        }
    }

    private void handleActiveUser(int uid, LocalDate loginDate) {
        User user = userRepo.findByUid(uid).get(0);
        Boolean newLogin = false;

        if (user.getLastLoginDate() == null) {
            newLogin = true;
            user.setLastLoginDate(loginDate);
            userRepo.save(user);
        }

        int lastLoginMonth = user.getLastLoginDate().getMonthValue();
        user.setLastLoginDate(loginDate);
        userRepo.save(user);

        if (activeUsersRepo.findByYear(loginDate.getYear()).size() == 0) {
            activeUsersRepo.save(new activeUsers(LocalDate.now().getYear()));
        }

        if (newLogin || lastLoginMonth != loginDate.getMonthValue()) {
            activeUsers row = activeUsersRepo.findByYear(loginDate.getYear()).get(0);
            row.addMonthUserNum(loginDate.getMonthValue());
            activeUsersRepo.save(row);
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
        if (buttonValue.equals("ContactUs")) {
            return "user/ContactUs";
        }
        if (buttonValue.equals("Inbox")) {
            int currentPage = Integer.parseInt(info.get("currentPage"));
            int uid = Integer.parseInt(info.get("uid"));
            // User user = (User) session.getAttribute("session_user");
            // System.out.println("==================");
            // System.out.println("uid: "+user.getUid());
            // System.out.println("==================");
            List<adminMessage> allAdminMessages = adminMsgRepo.findAll();
            List<adminMessage> allInbox = new ArrayList<>();
            List<adminMessage> inbox = new ArrayList<>();
            Comparator<adminMessage> sentDateComparator = Comparator.comparing(adminMessage::getSentDate).reversed();
            for (adminMessage m : allAdminMessages) {
                if (m.getToUid() == uid || m.getToUid() == 0) {
                    allInbox.add(m);
                }
            }
            Collections.sort(allInbox, sentDateComparator);
            for (int i = 0; i < currentPage * 5; i++) {
                if (i < allInbox.size()) {
                    inbox.add(allInbox.get(i));
                } else {
                    break;
                }
            }
            System.out.println("Number of msg: " + allInbox.size());
            model.addAttribute("inbox", inbox);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("maxPage", (int) Math.ceil(allInbox.size() / 5.0));

            return "user/userInbox";
        }
        if (buttonValue.equals("Statistic")) {
            List<User> user = userRepo.findByUid(Integer.parseInt(info.get("uid")));
            ArrayList<Float> userRecords = user.get(0).getWPM();
            // Handle avg and best records
            Float sum = 0f;
            for (Float f : userRecords) {
                sum += f;
            }
            int recordSize = userRecords.size();
            user.get(0).setAverageRecord((sum * 1f) / recordSize);
            if (recordSize > 0) {
                model.addAttribute("avgRecord", user.get(0).getAverageWPM() + " WPM");
                model.addAttribute("bestRecord", user.get(0).getBestWPM() + " WPM");
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
                // model.addAttribute("recordSize", recordSize);
            } else if (recordSize > 10) {
                for (int k = 0; k < 10; k++) {
                    lastTen = userRecords.get(recordSize - 1 - k) + "," + lastTen;
                }
                // model.addAttribute("recordSize", 10);
            }

            if (recordSize > 0) {
                lastTen = lastTen.substring(0, lastTen.length() - 1);
            }

            System.out.println("----------------------");
            System.out.println(lastTen);
            System.out.println("----------------------");

            model.addAttribute("recordSize", recordSize);
            model.addAttribute("userRecords", lastTen);
            return "user/statistic";
        }
        if (buttonValue.equals("Ladder")) {
            List<ladderBoard> entireBoard = ladderRepo.findAll();
            if (entireBoard.size() > 10) {
                List<ladderBoard> listOf10 = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    listOf10.add(entireBoard.get(i));
                }
                entireBoard = listOf10;
            }
            ladderBoard userInfo = null;
            int uid = Integer.parseInt(info.get("uid"));
            if (ladderRepo.findByUid(uid).size() == 1) {
                userInfo = ladderRepo.findByUid(uid).get(0);
            }
            model.addAttribute("ladderBoard", entireBoard);
            if (userInfo != null) {
                model.addAttribute("userRank", userInfo.getRank());
                model.addAttribute("username", userInfo.getUsername());
                model.addAttribute("userAvgWpm", userInfo.getAverageWPM());

            } else {
                model.addAttribute("userRank", "N/A");
                model.addAttribute("username", userRepo.findByUid(uid).get(0).getUsername());
                model.addAttribute("userAvgWpm", "N/A");
                model.addAttribute("alert", "No ranking: You played less than 10 games!");
            }

            return "user/ladderBoard";
        }
        if (buttonValue.equals("Setting")) {
            return "user/setting";
        }

        if (buttonValue.equals("LogOut")) {
            request.getSession().invalidate();
            return "user/signin";
        }

        else {
            List<User> user = userRepo.findByUid(Integer.parseInt(info.get("uid")));
            model.addAttribute("username", user.get(0).getUsername());
            return "user/userCentre";
        }
    }

    @PostMapping("/user/setting")
    public String handleSetting(@RequestParam("buttonValue") String buttonValue, @RequestParam Map<String, String> info,
            Model model) {
        int uid = Integer.parseInt(info.get("uid"));
        model.addAttribute("uid", uid);
        if (buttonValue.equals("update")) {
            User user = userRepo.findByUid(uid).get(0);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmailAddress());
            return "user/userInfo";
        }
        return "user/delete";
    }

    @PostMapping("/user/update")
    public String handleUpdate(@RequestParam Map<String, String> info, Model model) {
        int uid = Integer.parseInt(info.get("uid"));
        User user = userRepo.findByUid(uid).get(0);
        model.addAttribute("uid", uid);
        String newUsername = info.get("username");
        String newEmail = info.get("email");
        String newPW = info.get("newPassword");
        String ReNewPw = info.get("reNewPassword");
        Boolean correctPW = info.get("password").equals(user.getPassword());
        Boolean updated = false;
        if (correctPW) {
            if (!newUsername.equals("")) {
                if (userRepo.findByUsername(newUsername).size() == 0) {
                    user.setUsername(newUsername);
                    updated = true;
                } else if (newUsername.equals(user.getUsername())) {
                    model.addAttribute("usernameAlert", "Same as your old username :D");
                } else {
                    model.addAttribute("usernameAlert", "Username is already exist!");
                }
            }
            if (!newEmail.equals("")) {
                if (isGoodEmailFormat(newEmail)) {
                    user.setEmailAddress(newEmail);
                    updated = true;
                } else {
                    model.addAttribute("emailAlert", "Invalid email format");
                }
            }
            if (!newPW.equals("")) {
                Boolean gate = true;
                if (!newPW.equals(ReNewPw)) {
                    model.addAttribute("rePasswordAlert", "New password not match");
                    gate = false;
                }
                if (newPW.equals(user.getPassword())) {
                    model.addAttribute("newPasswordAlert", "Same as your old password :D");
                    gate = false;
                }
                if (gate) {
                    user.setEmailAddress(newEmail);
                    updated = true;
                }

            }
        } else if (info.get("password").equals("")) {
            model.addAttribute("passwordAlert", "Please enter password");
        } else {
            model.addAttribute("passwordAlert", "Incorrect password");
        }
        if (updated) {
            model.addAttribute("result", "User info update success!");
            userRepo.save(user);
        }
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmailAddress());
        return "user/userInfo";

    }

    private Boolean isGoodEmailFormat(String email) {
        Boolean existSigns = email.indexOf('@') != -1 && email.indexOf('.') != -1;
        Boolean onlyOneSign = email.indexOf('@', email.indexOf('@')) == -1
                && email.indexOf('.', email.indexOf('.')) == -1;
        Boolean goodSignFormat = email.indexOf('@', email.indexOf('.')) != -1;

        return existSigns && onlyOneSign && goodSignFormat;
    }

    // by 4
    // read msg from admin by its md in table: admin_message
    @PostMapping("/user/readInbox")
    public String readUserInbox(@RequestParam("buttonValue") String buttonValue, @RequestParam Map<String, String> info,
            Model model) {
        model.addAttribute("uid", info.get("uid"));
        int currentPage = Integer.parseInt(info.get("currentPage"));
        adminMessage msg = adminMsgRepo.findByMid(Integer.parseInt(buttonValue)).get(0);
        msg.setRead("Y");
        adminMsgRepo.save(msg);
        model.addAttribute("msg", msg);
        model.addAttribute("currentPage", currentPage);
        // model.addAttribute("maxPage", (int)Math.ceil(allInbox.size()/5.0));
        return "user/userReadInbox";
    }

    @PostMapping("/user/inbox")
    public String switchPage(@RequestParam("buttonValue") String buttonValue, @RequestParam Map<String, String> info,
            Model model) {
        List<adminMessage> allAdminMessages = adminMsgRepo.findAll();
        List<adminMessage> allInbox = new ArrayList<>();
        List<adminMessage> inbox = new ArrayList<>();
        int uid = Integer.parseInt(info.get("uid"));
        Comparator<adminMessage> sentDateComparator = Comparator.comparing(adminMessage::getSentDate).reversed();
        for (adminMessage m : allAdminMessages) {
            if (m.getToUid() == uid || m.getToUid() == 0) {
                allInbox.add(m);
            }
        }
        Collections.sort(allInbox, sentDateComparator);
        int currentPage = Integer.parseInt(info.get("currentPage"));
        int maxPage = (int) Math.ceil(allInbox.size() / 5.0);
        if (buttonValue.equals("next") && currentPage < maxPage) {
            currentPage++;
        } else if (buttonValue.equals("previous") && currentPage > 1) {
            currentPage--;
        }
        for (int i = (currentPage - 1) * 5; i < (currentPage) * 5; i++) {
            if (i < allInbox.size()) {
                inbox.add(allInbox.get(i));
            } else {
                break;
            }
        }

        System.out.println("Number of msg: " + allInbox.size());
        model.addAttribute("uid", info.get("uid"));
        model.addAttribute("inbox", inbox);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("maxPage", (int) Math.ceil(allInbox.size() / 5.0));
        return "user/userInbox";
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
            model.addAttribute("btnValue", "ContactUs");
            model.addAttribute("btnText", "Back to Contact Us");
            System.out.println("bad msg");
            return "user/sendResult";
        }

        // None of the rows are null
        System.out.println("Good msg");
        userMsgRepo.save(new userMessage(Integer.parseInt(message.get("uid")), message.get("subject"),
                message.get("content"), "N", LocalDate.now()));
        model.addAttribute("feedback", "Message sent!!");
        model.addAttribute("btnValue", "back");
        model.addAttribute("btnText", "Home");
        return "user/sendResult";
    }

    private void handleLadderBoard(User user) {
        List<Float> last10 = new ArrayList<>();
        int WPMsize = user.getWPM().size();
        Float last10Avg = 0f;
        if (WPMsize >= 10f) {
            for (int i = WPMsize - 10; i < WPMsize; i++) {
                last10.add(user.getWPM().get(i));
                last10Avg += user.getWPM().get(i);
            }
            last10Avg /= 10f;
            List<ladderBoard> row = ladderRepo.findByUid(user.getUid());
            if (row.size() == 0) {
                ladderRepo.save(new ladderBoard(user.getUid(), user.getUsername(), last10Avg));
            } else {
                row.get(0).setAverageWPM(last10Avg);
            }

            List<ladderBoard> entireBoard = ladderRepo.findAll();
            Comparator<ladderBoard> avgWpmComparator = Comparator.comparing(ladderBoard::getAverageWPM).reversed();
            Collections.sort(entireBoard, avgWpmComparator);
            for (int i = 0; i < entireBoard.size(); i++) {
                entireBoard.get(i).setRank(i + 1);
                ladderRepo.save(entireBoard.get(i));
            }
        }

    }

    @PostMapping("/submitWPM")
    public String addRecord(@RequestParam Map<String, String> wpm, Model model, HttpSession session,
            HttpServletRequest request) {
        User user = (User) session.getAttribute("session_user");
        user.addRecords(Float.parseFloat(wpm.get("wpm")));
        userRepo.save(user);
        // by A, it wont affect your system, just adding info for to the ladder board :
        // D
        handleLadderBoard(userRepo.findByUid(Integer.parseInt(wpm.get("uid"))).get(0));
        // done
        // return "user/userCentre";
        return "user/game2";
    }

    @GetMapping("/submitWPM")
    public String refresh(HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "user/signin";
        } else {
            return "user/game";

        }
    }

    @PostMapping("/user/get10Msg")
    public String get10msg(@RequestParam Map<String, String> info, Model model, HttpSession session) {
        int uid = Integer.parseInt(info.get("uid"));
        for (int i = 1; i <= 10; i++) {
            adminMsgRepo.save(new adminMessage("Test", uid, "Test subject" + i, "Test content" + i,
                    ranDateB4(LocalDate.now()), "N"));
        }
        List<adminMessage> allAdminMessages = adminMsgRepo.findAll();
        List<adminMessage> inbox = new ArrayList<>();
        for (adminMessage m : allAdminMessages) {
            if (m.getToUid() == uid || m.getToUid() == 0) {
                inbox.add(m);
            }
        }
        System.out.println("Number of msg: " + inbox.size());
        model.addAttribute("inbox", inbox);
        return "user/userInbox";

    }

    private LocalDate ranDateB4(LocalDate now) {
        LocalDate firstDate = LocalDate.parse(now.getYear() + "-01-01");
        int diff = (int) ChronoUnit.DAYS.between(firstDate, now);
        diff = new Random().nextInt(diff);
        now = now.minusDays(diff);
        return now;
    }

    @GetMapping("/forgotPassword")
    public String goForgotPassword() {
        return "user/forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam Map<String, String> info, HttpServletResponse response, Model model) {
        String email = info.get("email");
        List<User> userList = userRepo.findByEmailAddress(email);
        User user;

        if (!userList.isEmpty()) {
            user = userRepo.findByEmailAddress(info.get("email")).get(0);
            model.addAttribute("uid", user.getUid());
            int otp = random.nextInt(9999);
            System.out.println(otp);
            user.setPassword(Integer.toString(otp));
            userRepo.save(user);
            System.out.println(user.getUid());
            service.sendEmail(email, "Your new password is " + otp, "Password Reset");
            return "user/verificationPage";
        } else {
            model.addAttribute("emailNotFoundAlert", "Account Not Found");
            return "user/forgotPassword";
        }

    }

    @PostMapping("/submitOTP")
    public String submitOTP(@RequestParam Map<String, String> info, HttpServletResponse response, Model model) {
        int uid = Integer.parseInt(info.get("uid"));
        model.addAttribute("uid", uid);
        User user = userRepo.findByUid(uid).get(0);
        String newPassword = info.get("password");
        System.out.println(newPassword);
        System.out.println(user.getPassword());

        if (newPassword.equals(user.getPassword())) {
            return "user/resetPassword";
        }

        model.addAttribute("passwordDontMatch", "Password doesn't match. Please try again.");
        return "user/verificationPage";

    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam Map<String, String> info,
            HttpServletResponse response, Model model) {
        int uid = Integer.parseInt(info.get("uid"));
        model.addAttribute("uid", uid);
        User user = userRepo.findByUid(uid).get(0);
        String firstPassword = info.get("password");
        String secondPassword = info.get("repassword");

        if (!firstPassword.equals(secondPassword)) {
            model.addAttribute("passwordDontMatch", "Passwords do not match");
            return "user/resetPassword";
        }

        user.setPassword(firstPassword);
        userRepo.save(user);
        return "user/resetPasswordSuccess";
    }

}
