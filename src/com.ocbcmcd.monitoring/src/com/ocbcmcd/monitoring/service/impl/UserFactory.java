package com.ocbcmcd.monitoring.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ocbcmcd.monitoring.command.RegistrationCommand;
import com.ocbcmcd.monitoring.common.MD5;
import com.ocbcmcd.monitoring.dao.IRoleDao;
import com.ocbcmcd.monitoring.domain.Role;
import com.ocbcmcd.monitoring.domain.User;
 
@Component
public class UserFactory {
	@Autowired
	private IRoleDao roleDao;
	
	public User createUser(RegistrationCommand command) {
		User user = new User();
		Role role = null;
		
		user.setUserName(command.getUserName());
		try {
			user.setPassword(MD5.hash(command.getPassword()));
		} catch (Exception e) {
		}
		
		if (command.isAdminType()) {
			role = roleDao.getAdminRole();
		} else {
			role = roleDao.getRegularUserRole();
		}
		
		user.addRole(role);
		
		return user;
	} 
	
}
