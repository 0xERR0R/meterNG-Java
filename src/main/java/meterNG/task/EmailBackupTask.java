package meterNG.task;

import java.io.StringWriter;
import java.time.LocalDate;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.google.common.base.Charsets;

import meterNG.export.ReadingsCsvExporter;

/**
 * 
 * Periodical task: creates new csv export file and sends this file via mail.
 *
 */
@Component
@ConditionalOnProperty(value = "backupMail.enabled", havingValue = "true")
public class EmailBackupTask {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private ReadingsCsvExporter exporter;

	@Autowired
	private TemplateEngine templateEngine;

	@Value("${notificationMail.recipient}")
	private String recipient;

	@Autowired
	private JavaMailSender mailSender;

	public EmailBackupTask() {
		logger.info("Mail backup task is enabled");
	}

	@Scheduled(cron = "${backupMail.cron:0 0 0 1 * *}")
	public void runTask() {

		try {
			StringWriter w = new StringWriter();
			exporter.writeAsCsv(w);
			sendBackupFile(w.toString());
		} catch (Exception e) {
			logger.error("unable to create or send backup file", e);
		}
	}

	private void sendBackupFile(String data) throws MessagingException {
		final Context ctx = new Context();

		final MimeMessage mimeMessage = mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setSubject("[meterNG] New backup");
		message.setFrom("noreply@example.com");
		message.setTo(recipient);

		message.addAttachment(LocalDate.now() + ".csv", new ByteArrayResource(data.getBytes(Charsets.UTF_8)));

		final String textContent = templateEngine.process("mails/backup", ctx);
		message.setText(textContent, true);

		logger.info("sending backup mail");
		mailSender.send(mimeMessage);
	}

}
