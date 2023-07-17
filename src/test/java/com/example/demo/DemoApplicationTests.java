package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.User;
import com.example.demo.model.UserRepository;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private UserRepository userRepoTest;

	@Test
	void createAccount() {
		User newUser = new User("testUname", "testPassword", "ilovemygroup123@gmail.com");
		List<User> testFind = userRepoTest.findByUsernameAndPassword("testUname", "testPassword");
		if (testFind.isEmpty()) {
			fail();
		} else {
			assertTrue(true);
		}
	}
}
