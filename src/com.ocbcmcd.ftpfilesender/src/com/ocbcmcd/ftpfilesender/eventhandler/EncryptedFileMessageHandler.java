package com.ocbcmcd.ftpfilesender.eventhandler;

import java.io.File;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHandlingException;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsUtils;

import com.ocbcmcd.message.EncryptedFileSending;
import com.ocbcmcd.message.OcbcFileSent;
import com.ocbcmcd.message.SapFileEncrypted;

public class EncryptedFileMessageHandler {
	protected Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	@Qualifier("processingDestination")
	private Destination processingDestination;
	
	@Value("${encrypted.ext}")
	private String encryptedExt;
	
	@Value("${encrypted.dir}")
	private String encryptedDirectory;
	
	@Autowired
	@Qualifier("outChannel")
	private DirectChannel ftpChannel;
	
	@ServiceActivator
	public void handleMessage(Message<javax.jms.Message> message) {
		ObjectMessage objectMessage = (ObjectMessage) message.getPayload();
		
		try {
			SapFileEncrypted event = (SapFileEncrypted) objectMessage.getObject();
			log.info("Received event encrypteed : " + event.getFileName());
			
			Message<File> fileMessage =  MessageBuilder.withPayload(new File(encryptedDirectory, event.getFileName() + encryptedExt)).build();
			
			jmsTemplate.convertAndSend(processingDestination, new EncryptedFileSending(event.getFileName()));
			
			ftpChannel.send(fileMessage);
			log.info("Ftp file successfully : sent");
			
			jmsTemplate.convertAndSend(new OcbcFileSent(event.getFileName()));
		} catch (JMSException e) {
			throw JmsUtils.convertJmsAccessException(e);
		} catch (MessageHandlingException e) {
			log.error("Error send file", e);
		}
	}
}
