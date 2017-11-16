package meterNG.aggregation;

import static meterNG.util.DateUtil.addMonth;
import static meterNG.util.DateUtil.addYear;
import static meterNG.util.DateUtil.getEndOfMonth;
import static meterNG.util.DateUtil.getEndOfYear;
import static meterNG.util.DateUtil.getMonthYearString;
import static meterNG.util.DateUtil.getStartOfMonth;
import static meterNG.util.DateUtil.getStartOfYear;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import meterNG.model.Aggregation;
import meterNG.model.Reading;

public final class Aggregator {
	private Aggregator() {
	}

	public static Collection<Aggregation> aggregateMonth(List<Reading> readings) {
		List<Aggregation> result = new LinkedList<>();

		if (readings.size() < 2) {
			return result;
		}
		Interpolator interpolator = new Interpolator(readings);

		LocalDateTime firstDate = readings.get(0).getDate();
		LocalDateTime lastDate = getEndOfMonth(readings.get(readings.size() - 1).getDate());

		LocalDateTime date = firstDate;
		do {
			LocalDateTime startOfMonth = getStartOfMonth(date);
			LocalDateTime endOfMonth = getEndOfMonth(date);
			BigDecimal v1 = interpolator.getValue(startOfMonth);
			BigDecimal v2 = interpolator.getValue(endOfMonth);

			String monthYearString = getMonthYearString(startOfMonth);
			Aggregation agg = new Aggregation(v2.subtract(v1).setScale(2, RoundingMode.HALF_UP), monthYearString);
			result.add(agg);

			date = addMonth(startOfMonth);
		} while (date.isBefore(lastDate));

		return result;
	}

	public static Collection<Aggregation> aggregateYear(List<Reading> readings) {
		List<Aggregation> result = new LinkedList<>();

		if (readings.size() < 2) {
			return result;
		}
		Interpolator interpolator = new Interpolator(readings);
		LocalDateTime firstDate = readings.get(0).getDate();
		LocalDateTime lastDate = getEndOfYear(readings.get(readings.size() - 1).getDate());

		LocalDateTime date = firstDate;
		do {
			LocalDateTime startOfYear = getStartOfYear(date);
			LocalDateTime endOfYear = getEndOfYear(date);
			BigDecimal v1 = interpolator.getValue(startOfYear);
			BigDecimal v2 = interpolator.getValue(endOfYear);

			String yearString = Integer.toString(startOfYear.get(ChronoField.YEAR));
			Aggregation agg = new Aggregation(v2.subtract(v1).setScale(2, RoundingMode.HALF_UP), yearString);
			result.add(agg);

			date = addYear(startOfYear);
		} while (date.isBefore(lastDate));

		return result;
	}
}
