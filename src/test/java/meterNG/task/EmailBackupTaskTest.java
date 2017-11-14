package meterNG.task;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.mail.MessagingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import meterNG.task.EmailBackupTask;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "backupMail.enabled=true", "notificationMail.recipient=me@example.com",
		"spring.mail.host=localhost", "spring.mail.port=3025" })
public class EmailBackupTaskTest {

	@Autowired(required = false)
	private EmailBackupTask task;

	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

	@Test
	public void backupFileMail() throws IOException, MessagingException {
		task.runTask();
		assertThat(greenMail.getReceivedMessages()).hasSize(1);
		// assertThat(greenMail.getReceivedMessages()[0].getFileName()).isEqualTo(LocalDate.now()+".csv");
	}
}
