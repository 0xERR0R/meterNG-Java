package meterNG.task;

import static java.time.LocalDateTime.now;
import static meterNG.model.ReadingBuilder.readingBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import javax.mail.MessagingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import meterNG.repository.ReadingsRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "config.allMeters = Electricity(kWh), Gas(m³), Water(m³)",
		"notificationMail.recipient=me@example.com", "statisticMail.enabled=true", "spring.mail.host=localhost",
		"spring.mail.port=3025" })
public class EmailMonthlyStatisticTaskTest {

	@Autowired(required = false)
	private EmailMonthlyStatisticTask task;

	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

	@MockBean
	private ReadingsRepository readingsRepository;

	@Test
	public void notificationIsEnabled() {
		assertThat(task).isNotNull();
	}

	@Test
	public void mailSend() throws MessagingException {
		given(readingsRepository.findByMeterName("Electricity")).willReturn(Lists.newArrayList( //
				readingBuilder().meterName("Electricity").date(now().minusDays(400)).value("20").build(), //
				readingBuilder().meterName("Electricity").date(now().minusDays(350)).value("45").build(), //
				readingBuilder().meterName("Electricity").date(now().minusDays(300)).value("90").build(), //
				readingBuilder().meterName("Electricity").date(now().minusDays(250)).value("110").build(), //
				readingBuilder().meterName("Electricity").date(now().minusDays(200)).value("138").build(), //
				readingBuilder().meterName("Electricity").date(now().minusDays(150)).value("170").build(), //
				readingBuilder().meterName("Electricity").date(now().minusDays(100)).value("222").build(), //
				readingBuilder().meterName("Electricity").date(now().minusDays(50)).value("280").build(), //
				readingBuilder().meterName("Electricity").date(now().minusDays(20)).value("180").build() //
		));
		given(readingsRepository.findByMeterName("Water")).willReturn(Lists.newArrayList( //
				readingBuilder().meterName("Water").date(now().minusDays(400)).value("2777").build(), //
				readingBuilder().meterName("Water").date(now().minusDays(350)).value("3000").build(), //
				readingBuilder().meterName("Water").date(now().minusDays(300)).value("3300").build(), //
				readingBuilder().meterName("Water").date(now().minusDays(250)).value("3800").build(), //
				readingBuilder().meterName("Water").date(now().minusDays(200)).value("4205").build(), //
				readingBuilder().meterName("Water").date(now().minusDays(150)).value("4466").build(), //
				readingBuilder().meterName("Water").date(now().minusDays(100)).value("5000").build(), //
				readingBuilder().meterName("Water").date(now().minusDays(50)).value("5333").build(), //
				readingBuilder().meterName("Water").date(now().minusDays(20)).value("5133").build() //
		));
		task.runTask();
		assertThat(greenMail.getReceivedMessages()).hasSize(1);
	}
}
