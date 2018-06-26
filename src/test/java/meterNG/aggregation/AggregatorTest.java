package meterNG.aggregation;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import meterNG.model.Aggregation;
import meterNG.model.Reading;
import meterNG.model.ReadingBuilder;

public class AggregatorTest {
	
	private Aggregator aggregator = new Aggregator();

	@Test
	public void oneReadingsShouldReturnEmptyAggregationResult() {
		Collection<Aggregation> result = aggregator
				.aggregateMonth(Lists.newArrayList(ReadingBuilder.readingBuilder().meterName("test").build()));
		assertThat(result).isEmpty();
	}

	@Test
	public void singleMonthResult() {
		List<Reading> input = Lists.newArrayList(
				ReadingBuilder.readingBuilder().date("2016-01-01").value(1200).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-01-10").value(1250).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-01-20").value(1300).meterName("Electricity").build());

		Collection<Aggregation> result = aggregator.aggregateMonth(input);

		assertThat(result).hasSize(1).containsExactly(new Aggregation(new BigDecimal("160.00"), "01/2016"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void aggregationShouldBePossibleOnlyForOneMeter() {
		List<Reading> input = Lists.newArrayList(
				ReadingBuilder.readingBuilder().date("2016-01-01").value(1200).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-01-10").value(1250).meterName("Water").build());

		aggregator.aggregateMonth(input);
	}

	@Test
	public void singleMonthWithDateFromOtherMonthsResult() {
		List<Reading> input = Lists.newArrayList(
				ReadingBuilder.readingBuilder().date("2015-12-25").value(1100).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-01-01").value(1200).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-01-10").value(1250).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-01-20").value(1300).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-02-02").value(1400).meterName("Electricity").build());

		Collection<Aggregation> result = aggregator.aggregateMonth(input);

		assertThat(result).hasSize(3).contains(new Aggregation(new BigDecimal("192.31"), "01/2016"));
	}

	@Test
	public void aggregateYearResult() {
		List<Reading> input = Lists.newArrayList(
				ReadingBuilder.readingBuilder().date("2016-01-01").value(1200).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-03-10").value(1450).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-06-20").value(1700).meterName("Electricity").build(),
				ReadingBuilder.readingBuilder().date("2016-12-02").value(2400).meterName("Electricity").build());

		Collection<Aggregation> result = aggregator.aggregateYear(input);

		assertThat(result).hasSize(1).contains(new Aggregation(new BigDecimal("1327.27"), "2016"));
	}
}
