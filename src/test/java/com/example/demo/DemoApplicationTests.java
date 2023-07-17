package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.User;
import com.example.demo.model.UserRepository;
import com.example.demo.model.adminMessage;
import com.example.demo.model.adminMessageRepository;
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
	void testSendingByUser(){
		Boolean testPass=false;
		LocalDate testDate=LocalDate.now();
		userMessage testMessage=new userMessage(0, "testSubject", "testContent", "testSolved", testDate);
		userMsgRepo.save(testMessage);
		List<userMessage> userMsgList=userMsgRepo.findAll();
		for(userMessage m:userMsgList){
			if(m.getFromUid()==0){
				if(m.getSubject().equals("testSubject") && m.getContent().equals("testContent") && m.getSolved().equals("testSolved") && m.getSentDate().equals(testDate)){
					testPass=true;
				}
			}
		}
		userMsgRepo.delete(testMessage);
		assertEquals(true, testPass);
	}

	@Test
	void testSendingByAdmin(){
		Boolean testPass=false;
		LocalDate testDate=LocalDate.now();
		adminMessage testMessage=new adminMessage("testType",0,"testSubject","testContent",testDate,"testRead");
		adminMsgRepo.save(testMessage);
		List<adminMessage> adminMsgList=adminMsgRepo.findAll();
		for(adminMessage m:adminMsgList){
			if(m.getType().equals("testType")){
				if(m.getToUid()==0 && m.getSubject().equals("testSubject") && m.getContent().equals("testContent") && m.getSentDate().equals(testDate) && m.getRead().equals("testRead")){
					testPass=true;
				}
			}
		}
		adminMsgRepo.delete(testMessage);
		assertEquals(true, testPass);
	}

}
