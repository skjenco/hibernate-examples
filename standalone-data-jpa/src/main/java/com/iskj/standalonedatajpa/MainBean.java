package com.iskj.standalonedatajpa;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.iskj.standalonedatajpa.model.User;
import com.iskj.standalonedatajpa.repository.UserRepository;

public class MainBean {
	
	@Autowired
	private UserRepository userRepository;
	
	private static Logger log = LoggerFactory.getLogger(MainBean.class);

	public void start() {
		
		// Spring Data JPA CRUD operations are transactionnal by default !
		// http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/#transactions
		User newUser = new User();
		newUser.setName("inserted");
		userRepository.save(newUser);
		
		List<User> all = userRepository.findAll();
		log.info("users=" + all);
	}

}
