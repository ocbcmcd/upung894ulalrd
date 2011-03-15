package com.ocbcmcd.monitoring.web;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ocbcmcd.monitoring.domain.User;
import com.ocbcmcd.monitoring.exception.UserNotFoundException;
import com.ocbcmcd.monitoring.query.IUserQuery;
import com.ocbcmcd.monitoring.service.IRegistrationService;

@Controller

public class UserController {
	protected Log log = LogFactory.getLog(getClass());
	
	private static final int DISABLE_MESSAGE_ID = 5;
	private static final int ENABLE_MESSAGE_ID = 4;
	
	@Autowired
	private IUserQuery userQuery;
	
	@Autowired
	private IRegistrationService registrationService;
	
	@Value("${paging.size}")
	private String _pageSize;
	
	@RequestMapping("/user")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ModelMap list(@RequestParam(required = false) String p, Model model) {
		int page = 0;
		List searchResults = userQuery.getUsers();
		PagedListHolder pagedListHolder = new PagedListHolder(searchResults);
		
		try {
			page = Integer.parseInt(p);
		} catch (NumberFormatException e) {

		}
		
		pagedListHolder.setPage(page);
		int pageSize = Integer.parseInt(_pageSize);
		pagedListHolder.setPageSize(pageSize);

		return new ModelMap("pagedListHolder", pagedListHolder);
	}
	
	@RequestMapping("/user/message/{id}")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ModelAndView message(@PathVariable("id") int id, @RequestParam(required = false) String p) {
		int page = 0;
		List searchResults = userQuery.getUsers();
		PagedListHolder pagedListHolder = new PagedListHolder(searchResults);
		
		try {
			page = Integer.parseInt(p);
		} catch (NumberFormatException e) {

		}
		
		pagedListHolder.setPage(page);
		int pageSize = Integer.parseInt(_pageSize);
		pagedListHolder.setPageSize(pageSize);
		
		ModelAndView model = new ModelAndView("user");
		generateMessageNotification(id, model);
		
		model.addObject("pagedListHolder", pagedListHolder);
		
		return model;
	}
	
	@RequestMapping("/user/{id}")
	public ModelAndView detail(@PathVariable("id") int id, Model model) {
		User user = registrationService.getUser(id);
		if (user != null) {
			return new ModelAndView("user_detail", "user", user);
		} else {
			return new ModelAndView("redirect:/user/message/0");
		}
	}
	
	@RequestMapping("/user/{id}/message/{messageId}")
	public ModelAndView detailMessage(@PathVariable("id") int id, @PathVariable("messageId") int messageId) {
		User user = registrationService.getUser(id);
		if (user != null) {
			ModelAndView model = new ModelAndView("user_detail");
			generateMessageNotification(messageId, model);
			model.addObject("user", user);
			return model;
		} else {
			return new ModelAndView("redirect:/user/message/0");
		}
	}
	
	@RequestMapping("/user/disable/{id}")
	public ModelAndView disable(@PathVariable("id") int id, Model model) {
		String url;
		try {
			registrationService.disable(id);
			url = "redirect:/user/" + id + "/message/" + DISABLE_MESSAGE_ID;
			
			return new ModelAndView(url);
		} catch (UserNotFoundException e) {
			return new ModelAndView("redirect:/user/");
		}
	}
	
	@RequestMapping("/user/enable/{id}")
	public ModelAndView enable(@PathVariable("id") int id, Model model) {
		String url;
		try {
			registrationService.enable(id);
			url = "redirect:/user/" + id + "/message/" + ENABLE_MESSAGE_ID;
			
			return new ModelAndView(url);
		} catch (UserNotFoundException e) {
			return new ModelAndView("redirect:/user/");
		}
	}
	
	private void generateMessageNotification(int id, ModelAndView model) {
		switch (id) {
		case 0:
			model.addObject("message", "message.user.notfound");
			break;
		case 1:
			model.addObject("message", "message.user.created");
			break;
		case 3:
			model.addObject("message", "message.user.updated");
			break;
		case ENABLE_MESSAGE_ID:
			model.addObject("message", "message.user.enabled");
			break;
		case DISABLE_MESSAGE_ID:
			model.addObject("message", "message.user.disabled");
			break;
		default:
			break;
		}
	}
}
