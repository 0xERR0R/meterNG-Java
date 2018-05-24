package meterNG.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

import meterNG.aggregation.Aggregator;
import meterNG.model.Aggregation;
import meterNG.model.Meter;
import meterNG.repository.MeterRepository;
import meterNG.repository.ReadingsRepository;
import meterNG.util.DateUtil;

/**
 * 
 * Periodical task: sends information about difference in the consumption in
 * current month compared with last month and same month in last year
 *
 */
@Component
@ConditionalOnProperty(value = "statisticMail.enabled", havingValue = "true")
public class EmailMonthlyStatisticTask {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ReadingsRepository readingsRepository;

	@Autowired
	private MeterRepository meterRepository;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${notificationMail.recipient}")
	private String recipient;

	@Scheduled(cron = "${statisticMail.cron:0 0 0 1 * *}")
	public void runTask() throws MessagingException {
		List<StatisticEntry> entries = new ArrayList<>();
		for (Meter m : meterRepository.getAllMeters()) {
			LocalDateTime lastMonthDate = LocalDate.now().withDayOfMonth(1).minusDays(1).atStartOfDay();
			String lastMonth = DateUtil.getMonthYearString(lastMonthDate);
			String secontLastMonth = DateUtil.getMonthYearString(lastMonthDate.minusMonths(1));
			String sameMonthInLastYear = DateUtil.getMonthYearString(lastMonthDate.minusYears(1));

			Collection<Aggregation> agg = Aggregator.aggregateMonth(readingsRepository.findByMeterName(m.getName()));
			Optional<Aggregation> lastMonthAggregation = agg.stream()
					.filter(a -> a.getAggregationName().equals(lastMonth)).findFirst();
			Optional<Aggregation> secondLastMonthAggregation = agg.stream()
					.filter(a -> a.getAggregationName().equals(secontLastMonth)).findFirst();
			Optional<Aggregation> sameMonthInLastYearAggregation = agg.stream()
					.filter(a -> a.getAggregationName().equals(sameMonthInLastYear)).findFirst();

			if (lastMonthAggregation.isPresent() && secondLastMonthAggregation.isPresent()) {
				StatisticEntry entry = new StatisticEntry();
				entry.setMeter(m);
				entry.setConsumption2(lastMonthAggregation.get().getAggregatedValue());
				entry.setPeriod2(lastMonthAggregation.get().getAggregationName());
				entry.setConsumption1(secondLastMonthAggregation.get().getAggregatedValue());
				entry.setPeriod1(secondLastMonthAggregation.get().getAggregationName());
				entries.add(entry);
			}

			if (lastMonthAggregation.isPresent() && sameMonthInLastYearAggregation.isPresent()) {
				StatisticEntry entry = new StatisticEntry();
				entry.setMeter(m);
				entry.setConsumption2(lastMonthAggregation.get().getAggregatedValue());
				entry.setPeriod2(lastMonthAggregation.get().getAggregationName());
				entry.setConsumption1(sameMonthInLastYearAggregation.get().getAggregatedValue());
				entry.setPeriod1(sameMonthInLastYearAggregation.get().getAggregationName());
				entries.add(entry);
			}
		}
		if (!entries.isEmpty()) {
			sendMail(entries);
		}

	}

	private void sendMail(List<StatisticEntry> entries) throws MessagingException {
		final Context ctx = new Context();

		ctx.setVariable("entries", entries);

		final MimeMessage mimeMessage = mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setSubject("[meterNG] Statistics");
		message.setFrom("noreply@example.com");
		message.setTo(recipient);

		final String textContent = templateEngine.process("mails/monthlyStatistics", ctx);
		message.setText(textContent, true);

		logger.info("sending statistics mail");
		mailSender.send(mimeMessage);
	}
}
