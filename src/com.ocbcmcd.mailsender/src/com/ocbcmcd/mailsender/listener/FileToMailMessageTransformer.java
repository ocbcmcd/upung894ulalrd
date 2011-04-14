package com.ocbcmcd.mailsender.listener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.annotation.Transformer;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Component
public class FileToMailMessageTransformer {
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${endofday.mail.to}")
	private String mailTo; 
	
	@Value("${mail.from}")
	private String mailFrom;
	
	@Value("${daily.report.dir}")
	private String dailyReportDir;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@Transformer
	public MailMessage transformFileToString(File file) throws Exception {
		
		File destFile = new File(dailyReportDir, file.getName());
		FileUtils.moveFile(file, destFile);
		
		MimeMessage message = mailSender.createMimeMessage();
		
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		helper.setSubject("OCBC File Sending - End Of Day Reporting");
		helper.setFrom(mailFrom);
		helper.setTo(mailTo);
		helper.setText(getEmailContent(FileUtils.readFileToString(destFile)));
		
		helper.addAttachment(file.getName(), new FileSystemResource(destFile));
		
		return new MimeMailMessage(message);
	}

	private String getEmailContent(String fileList) {
		Map model = new HashMap();
        model.put("fileList", fileList);
		return VelocityEngineUtils.mergeTemplateIntoString(
		           velocityEngine, "endofday-notification.vm", model);
	}
}
