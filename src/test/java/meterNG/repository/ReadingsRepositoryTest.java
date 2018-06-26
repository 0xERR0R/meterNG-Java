package meterNG.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import meterNG.model.Reading;
import meterNG.model.ReadingBuilder;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class ReadingsRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ReadingsRepository repository;

	@Test
	public void findByMeterNameShouldReturnOneReading() {
		Reading r = ReadingBuilder.readingBuilder().meterName("Electricity").date("2015-06-06").value(4711).build();
		this.entityManager.persist(r);
		List<Reading> result = this.repository.findByMeterName("Electricity");

		assertThat(result).hasSize(1).first().isEqualToIgnoringNullFields(r);
	}

	@Test
	public void findAllReadingsLastDate() {
		entityManager.persistFlushFind(ReadingBuilder.readingBuilder().meterName("a").date("2015-03-03").value(4711).build());
		entityManager.persistFlushFind(ReadingBuilder.readingBuilder().meterName("a").date("2015-04-04").value(4711).build());
		entityManager.persistFlushFind(ReadingBuilder.readingBuilder().meterName("a").date("2015-01-01").value(4711).build());
		assertThat(repository.findAllReadingsLastDate()).isEqualTo(LocalDate.parse("2015-04-04"));
	}

	@Test
	public void findReadingsByListLastDate() {
		entityManager.persistFlushFind(ReadingBuilder.readingBuilder().meterName("a").date("2015-03-03").value(4711).build());
		entityManager.persistFlushFind(ReadingBuilder.readingBuilder().meterName("a").date("2015-04-04").value(4711).build());
		entityManager.persistFlushFind(ReadingBuilder.readingBuilder().meterName("a").date("2015-01-01").value(4711).build());
		entityManager.persistFlushFind(ReadingBuilder.readingBuilder().meterName("b").date("2015-01-01").value(4711).build());
		assertThat(repository.findReadingsLastDateByMeterNames(Collections.singletonList("a")))
				.isEqualTo(LocalDate.parse("2015-04-04"));
		assertThat(repository.findReadingsLastDateByMeterNames(Collections.singletonList("b")))
				.isEqualTo(LocalDate.parse("2015-01-01"));
	}

}
