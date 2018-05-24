package meterNG.task;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import meterNG.repository.MeterRepository;
import meterNG.repository.ReadingsRepository;
import meterNG.util.DateUtil;

/**
 * 
 * Periodical task: sends reminder mail if X days elapsed since last reading
 * date
 *
 */
@Component
@ConditionalOnProperty(value = "notificationMail.enabled", havingValue = "true")
public class EmailNotificationTask {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${notificationMail.daysAfterLastReading:7}")
	private int daysAfterLastReading;

	@Value("${notificationMail.recipient}")
	private String recipient;

	@Value("${notificationMail.url}")
	private URL url;

	@Value("${notificationMail.meterNamesForNotification:}")
	private List<String> meterNamesForNotification;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private ReadingsRepository readingsRepository;

	@Autowired
	private MeterRepository meterRepository;

	@Autowired
	private JavaMailSender mailSender;

	public EmailNotificationTask() {
		logger.info("Mail notification task is enabled");
	}

	@Scheduled(cron = "${notificationMail.cron:0 0 7 * * *}")
	public void runTask() {

		List<String> meters = checkValidMeterNames();
		LocalDate lastDate = meters.isEmpty() ? readingsRepository.findAllReadingsLastDate()
				: readingsRepository.findReadingsLastDateByMeterNames(meters);

		if (shouldSendMail(lastDate)) {
			try {
				sendNotification();
			} catch (MessagingException e) {
				logger.error("enable to send mail", e);
			}
		}
	}

	private List<String> checkValidMeterNames() {
		List<String> validMeterNames = new ArrayList<>();
		if (meterNamesForNotification != null) {
			for (String m : meterNamesForNotification) {
				if (meterRepository.findMeterByName(m).isPresent()) {
					validMeterNames.add(m);
				}
			}
		}
		return validMeterNames;
	}

	private boolean shouldSendMail(LocalDate lastDate) {
		return lastDate != null && DateUtil.diffDays(LocalDate.now(), lastDate) >= daysAfterLastReading;
	}

	private void sendNotification() throws MessagingException {
		final Context ctx = new Context();
		ctx.setVariable("url", url);

		final MimeMessage mimeMessage = mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
		message.setSubject("[meterNG] Reminder: Please record new meter readings");
		message.setFrom("noreply@example.com");
		message.setTo(recipient);

		final String textContent = templateEngine.process("mails/notification", ctx);
		message.setText(textContent, true);

		logger.info("sending notification mail");
		mailSender.send(mimeMessage);
	}
}
