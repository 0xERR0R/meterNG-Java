package meterNG.task;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import meterNG.task.EmailNotificationTask;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class EmailNotificationTaskWithNoPropertyActiveTest {

	@Autowired(required = false)
	private EmailNotificationTask task;

	@Test
	public void notificationIsDisabled() {
		assertThat(task).isNull();
	}
}
