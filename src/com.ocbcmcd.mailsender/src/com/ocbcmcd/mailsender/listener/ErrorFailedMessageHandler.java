package com.ocbcmcd.mailsender.listener;

import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import com.ocbcmcd.mailsender.mail.MailService;
import com.ocbcmcd.message.OcbcFileSendingFailed;

@Component
public class ErrorFailedMessageHandler {
	protected Log log = LogFactory.getLog(getClass());
	
	@Autowired
	@Qualifier("ftpErrorEmailService")
	private MailService mailService;
	
	@ServiceActivator
	public void handleMessage(Message<javax.jms.Message> message) {
		ObjectMessage objectMessage = (ObjectMessage) message.getPayload();
		
		try {
			OcbcFileSendingFailed event = (OcbcFileSendingFailed) objectMessage.getObject();
			log.info("Received event file error processed : " + event.getFileName());
			
			mailService.sendErrorMessage(event.getFileName(), event.getErrorMessage(), event.getStackTrace());
			
			log.info("Sending email : " + event.getFileName());
		} catch (Exception e) {
			log.error("Error sending mail", e);
		}
	}
}
