package com.ocbcmcd.monitoring;

import junit.framework.Assert;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ocbcmcd.monitoring.command.AdminUpdateUserCommand;
import com.ocbcmcd.monitoring.command.UserType;
import com.ocbcmcd.monitoring.domain.User;
import com.ocbcmcd.monitoring.exception.UserNotFoundException;
import com.ocbcmcd.monitoring.query.IUserQuery;
import com.ocbcmcd.monitoring.service.IRegistrationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class When_admin_update_user_to_admin_type {
	@Autowired
	IRegistrationService registrationService;
	
	@Autowired
	IUserQuery userQuery;
	
	@Autowired 
	HibernateTemplate hibernateTemplate;
	
	AdminUpdateUserCommand command = null;
	
	@Before
	public void setUp() {
		Logger.getRootLogger().setLevel(Level.INFO);
		Logger.getLogger("org.springframework").setLevel(Level.WARN);
		BasicConfigurator.configure();
		
		int userId = 26;
		String password = "password123";
		
		command = new AdminUpdateUserCommand();
		command.setId(userId);
		command.setPassword(password);
		command.setConfirmPassword(password);
		command.setUserType(UserType.ADMIN_TYPE);
	}
	
	@Test
	public void should_change_user() throws UserNotFoundException {
		registrationService.update(command);
		
		User tempUser = hibernateTemplate.get(User.class, command.getId());
		Assert.assertEquals(command.getHashedPassword(), tempUser.getPassword());
		
		
		Assert.assertTrue(tempUser.isAdminUser());
	}

	
}
