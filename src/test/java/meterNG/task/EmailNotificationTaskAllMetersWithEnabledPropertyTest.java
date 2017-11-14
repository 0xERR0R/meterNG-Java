package meterNG.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.time.LocalDate;

import javax.mail.MessagingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import meterNG.repository.ReadingsRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "notificationMail.enabled=true", "notificationMail.daysAfterLastReading=7",
		"notificationMail.recipient=me@example.com", "notificationMail.url=http://myurl:8080/record",
		"spring.mail.host=localhost", "spring.mail.port=3025" })
public class EmailNotificationTaskAllMetersWithEnabledPropertyTest {

	@Autowired(required = false)
	private EmailNotificationTask task;

	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

	@MockBean
	private ReadingsRepository readingsRepository;

	@Test
	public void notificationIsEnabled() {
		assertThat(task).isNotNull();
	}

	@Test
	public void lessThan7DaysNoMailSend() {
		given(readingsRepository.findAllReadingsLastDate()).willReturn(LocalDate.now().minusDays(6));
		task.runTask();
		assertThat(greenMail.getReceivedMessages()).hasSize(0);
	}

	@Test
	public void moreThan7DaysMailShouldBeSent() throws IOException, MessagingException {

		given(readingsRepository.findAllReadingsLastDate()).willReturn(LocalDate.now().minusDays(7));
		task.runTask();
		assertThat(greenMail.getReceivedMessages()).hasSize(1);
		String content = (String) greenMail.getReceivedMessages()[0].getContent();
		assertThat(content).contains("<p>Please record new meter readings</p>")
				.contains("<a href=\"http://myurl:8080/record\">Click here!</a>");
	}
}
