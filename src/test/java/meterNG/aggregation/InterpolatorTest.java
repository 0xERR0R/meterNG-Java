package meterNG.aggregation;

import static meterNG.util.DateUtil.parseDate;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.Lists;

import meterNG.model.Reading;
import meterNG.model.ReadingBuilder;

public class InterpolatorTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfLessThan2PointsAvailable() {
		new Interpolator(Collections.singletonList(new Reading()));
	}

	@Test
	public void extrapolationLowerValue() {
		Reading r1 = ReadingBuilder.builder().date("2015-01-11").value(1000).build();
		Reading r2 = ReadingBuilder.builder().date("2015-01-13").value(1200).build();
		Reading r3 = ReadingBuilder.builder().date("2015-01-15").value(1400).build();
		Interpolator interpolator = new Interpolator(Lists.newArrayList(r1, r2, r3));

		assertThat(interpolator.getValue(parseDate("2015-01-10"))).isEqualByComparingTo("900");
	}

	@Test
	public void extrapolationHigherValue() {
		Reading r1 = ReadingBuilder.builder().date("2015-01-11").value(1000).build();
		Reading r2 = ReadingBuilder.builder().date("2015-01-13").value(1200).build();
		Reading r3 = ReadingBuilder.builder().date("2015-01-15").value(1400).build();
		Interpolator interpolator = new Interpolator(Lists.newArrayList(r1, r2, r3));

		assertThat(interpolator.getValue(parseDate("2015-01-16"))).isEqualByComparingTo("1500");
	}

	@Test
	public void interpolationValueBetweenTwoPoints() {
		Reading r1 = ReadingBuilder.builder().date("2015-01-01").value(1000).build();
		Reading r2 = ReadingBuilder.builder().date("2015-01-03").value(1200).build();
		Reading r3 = ReadingBuilder.builder().date("2015-01-05").value(1400).build();
		Interpolator interpolator = new Interpolator(Lists.newArrayList(r1, r2, r3));

		assertThat(interpolator.getValue(parseDate("2015-01-02"))).isEqualByComparingTo("1100");
		assertThat(interpolator.getValue(parseDate("2015-01-04"))).isEqualByComparingTo("1300");
	}

	@Test
	public void valueExactEqualsPointValue() {
		Reading r1 = ReadingBuilder.builder().date("2015-01-01").value(1000).build();
		Reading r2 = ReadingBuilder.builder().date("2015-01-03").value(1200).build();
		Interpolator interpolator = new Interpolator(Lists.newArrayList(r1, r2));

		assertThat(interpolator.getValue(parseDate("2015-01-01"))).isEqualByComparingTo("1000");
		assertThat(interpolator.getValue(parseDate("2015-01-03"))).isEqualByComparingTo("1200");
	}
}
