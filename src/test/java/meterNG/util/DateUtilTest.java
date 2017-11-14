package meterNG.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;

import meterNG.util.DateUtil;

public class DateUtilTest {

	@Test
	public void testStartOfDay() throws ParseException {
		assertEquals(LocalDate.of(2013, 01, 01).atStartOfDay(),
				DateUtil.getStartOfMonth(LocalDate.parse("2013-01-12").atStartOfDay()));
		assertEquals(LocalDate.of(2013, 01, 01).atStartOfDay(),
				DateUtil.getStartOfMonth(LocalDate.parse("2013-01-01").atStartOfDay()));
	}

	@Test
	public void testEndOfDay() throws ParseException {

		assertEquals(LocalDateTime.parse("2013-01-31T23:59:59"),
				DateUtil.getEndOfMonth(LocalDate.parse("2013-01-12").atStartOfDay()));
		assertEquals(LocalDateTime.parse("2013-02-28T23:59:59"),
				DateUtil.getEndOfMonth(LocalDate.parse("2013-02-01").atStartOfDay()));
	}

	@Test
	public void testGetMonthYearString() throws ParseException {
		assertEquals("05/2013", DateUtil.getMonthYearString(LocalDate.parse("2013-05-12").atStartOfDay()));
	}

	@Test
	public void testDiffToNow() throws ParseException {
		LocalDate d1 = LocalDate.parse("2013-12-12");
		LocalDate d2 = LocalDate.parse("2013-12-14");
		LocalDate d3 = LocalDate.parse("2013-12-19");

		// gleiches Datum
		assertEquals(0, DateUtil.diffDays(d1, d1));

		assertEquals(2, DateUtil.diffDays(d1, d2));
		assertEquals(2, DateUtil.diffDays(d2, d1));

		assertEquals(5, DateUtil.diffDays(d2, d3));

	}
}
