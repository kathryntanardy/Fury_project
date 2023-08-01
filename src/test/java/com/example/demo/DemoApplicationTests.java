package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.User;
import com.example.demo.model.UserRepository;
import com.example.demo.model.activeUsers;
import com.example.demo.model.activeUsersRepository;
import com.example.demo.model.adminMessage;
import com.example.demo.model.adminMessageRepository;
import com.example.demo.model.ladderBoard;
import com.example.demo.model.ladderRepository;
import com.example.demo.model.ladderRepository;
import com.example.demo.model.userMessage;
import com.example.demo.model.userMessageRepository;

@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private UserRepository userRepoTest;

	@Autowired
	private userMessageRepository userMsgRepo;

	@Autowired
	private adminMessageRepository adminMsgRepo;

	@Autowired
	private ladderRepository ladderRepo;

	@Autowired
	private activeUsersRepository activeRepo;

	@Test
	void createAccount() {
		User newUser = new User("testUname", "testPassword", "ilovemygroup123@gmail.com");
		List<User> testFind = userRepoTest.findByUsernameAndPassword("testUname", "testPassword");
		if (!testFind.isEmpty()) {
			fail();
		} else {
			assertTrue(true);
		}
	}

	@Test
	void testAddRecords() {
		User user = new User("test", "test", "test@gmail.com");
		user.addRecords(250);
		ArrayList<Float> wpm = user.getWPM();
		if (wpm.isEmpty()) {
			fail();
		} else {
			assertTrue(true);
		}
	}

	@Test
	void testLogIn() {
		User user = new User("test", "test", "test@gmail.com");
		if (user.getLastLoginDate() == null) {
			assertTrue(true);
		} else {
			fail();
		}
	}

	@Test
	void testUserAvgWpm() {
		Boolean testPass = true;
		List<User> allUsers = userRepoTest.findAll();
		for (User u : allUsers) {
			List<Float> wpmList = u.getWPM();
			Float sum = 0f;
			Float avg = 0f;
			if (wpmList.size() != 0) {
				for (Float wpm : wpmList) {
					sum += wpm;
				}
				avg = sum / wpmList.size();
				if (u.getAverageWPM() != avg) {
					testPass = false;
					break;
				}
			}
		}
		assertEquals(true, testPass);
	}

	@Test
	void testUserBestWpm() {
		Boolean testPass = true;
		List<User> allUsers = userRepoTest.findAll();
		for (User u : allUsers) {
			List<Float> wpmList = u.getWPM();
			Float best = 0f;
			if (wpmList.size() != 0) {
				for (Float wpm : wpmList) {
					if (wpm > best) {
						best = wpm;
					}
				}
				if (u.getBestWPM() != best) {
					testPass = false;
					break;
				}
			}
		}
		assertEquals(true, testPass);
	}

	@Test
	void testUserContactUs() {
		Boolean testPass = false;
		LocalDate testDate = LocalDate.now();
		userMessage testMessage = new userMessage(-1, "testSubject", "testContent", "testSolved", testDate);
		userMsgRepo.save(testMessage);
		List<userMessage> userMsgList = userMsgRepo.findAll();
		for (userMessage m : userMsgList) {
			if (m.getFromUid() == -1) {
				if (m.getSubject().equals("testSubject") && m.getContent().equals("testContent")
						&& m.getSolved().equals("testSolved") && m.getSentDate().equals(testDate)) {
					testPass = true;
				}
			}
		}
		userMsgRepo.delete(testMessage);
		assertEquals(true, testPass);
	}

	@Test
	void testUserInbox() {
		Boolean testPass = false;
		LocalDate testDate = LocalDate.now();
		adminMessage testMessage = new adminMessage("testType", -1, "testSubject", "testContent", testDate, "testRead");
		adminMsgRepo.save(testMessage);
		List<adminMessage> allAdminMessages = adminMsgRepo.findAll();
		for (adminMessage m : allAdminMessages) {
			if (m.getToUid() == -1) {
				if (m.getSubject().equals("testSubject") && m.getContent().equals("testContent")
						&& m.getSentDate().equals(testDate) && m.getRead().equals("testRead")) {
					testPass = true;
				}
			}
		}
		adminMsgRepo.delete(testMessage);
		assertEquals(true, testPass);
	}

	@Test
	void testUserLeaderBoard() {
		Boolean testPass = true;
		List<ladderBoard> ranking = ladderRepo.findAll();
		int rank = ranking.get(0).getRank();
		for (int i = 1; i < ranking.size(); i++) {
			int nextRank = ranking.get(i).getRank();
			if (nextRank > rank) {
				rank = nextRank;
			} else {
				testPass = false;
				break;
			}
		}
		assertEquals(true, testPass);
	}

	@Test
	void testAdminInboxAndReply() {
		Boolean testPass = false;
		LocalDate testDate = LocalDate.now();
		userMessage testMessage = new userMessage(-1, "testSubject", "testContent", "testSolved", testDate);
		userMsgRepo.save(testMessage);
		List<userMessage> userMsgList = userMsgRepo.findAll();
		for (userMessage m : userMsgList) {
			if (m.getFromUid() == -1) {
				if (m.getSubject().equals("testSubject") && m.getContent().equals("testContent")
						&& m.getSolved().equals("testSolved") && m.getSentDate().equals(testDate)) {
					testPass = true;
				}
			}
		}
		testPass = false;
		adminMessage testReply = new adminMessage("testReply", -1, "testSubject", "testContent", testDate, "testRead");
		adminMsgRepo.save(testReply);
		List<adminMessage> adminMsgList = adminMsgRepo.findAll();
		for (adminMessage m : adminMsgList) {
			if (m.getType().equals("testReply")) {
				if (m.getToUid() == 0 && m.getSubject().equals("testSubject") && m.getContent().equals("testContent")
						&& m.getSentDate().equals(testDate) && m.getRead().equals("testRead")) {
					testPass = true;
				}
			}
		}
		adminMsgRepo.delete(testReply);
		userMsgRepo.delete(testMessage);
		assertEquals(true, testPass);
	}

	@Test
	void testAdminAnouncement() {
		Boolean testPass = false;
		LocalDate testDate = LocalDate.now();
		adminMessage testMessage = new adminMessage("testType", 0, "testSubject", "testContent", testDate, "testRead");
		adminMsgRepo.save(testMessage);
		List<adminMessage> adminMsgList = adminMsgRepo.findAll();
		for (adminMessage m : adminMsgList) {
			if (m.getType().equals("testType")) {
				if (m.getToUid() == 0 && m.getSubject().equals("testSubject") && m.getContent().equals("testContent")
						&& m.getSentDate().equals(testDate) && m.getRead().equals("testRead")) {
					testPass = true;
				}
			}
		}
		adminMsgRepo.delete(testMessage);
		assertEquals(true, testPass);
	}

	@Test
	void testAdminActiveUser() {
		int testMonth = LocalDate.now().getMonthValue();
		int testYear = LocalDate.now().getYear();
		List<User> allUser = userRepoTest.findAll();
		int activeThisMonth = 0;
		for (User u : allUser) {
			if (u.getLastLoginDate().getYear() == testYear && u.getLastLoginDate().getMonthValue() == testMonth) {
				activeThisMonth++;
			}
		}
		System.out.println(activeThisMonth);
		activeUsers row = activeRepo.findByYear(testYear).get(0);

		assertEquals(true, row.getMonthUserNum().get(testMonth - 1) == activeThisMonth);

	}

}
