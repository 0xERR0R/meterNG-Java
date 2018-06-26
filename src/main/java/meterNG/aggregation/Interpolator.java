package meterNG.aggregation;

import static meterNG.model.ReadingType.MEASURE;
import static meterNG.model.ReadingType.OFFSET;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import meterNG.model.Reading;
import meterNG.util.DateUtil;

/**
 * Calculates a reading value on a arbitrary date using linear interpolation.
 *
 */
public class Interpolator {

	private final double[] x, y;
	private final double minX, maxX;

	Interpolator(List<Reading> readings) {
		if (readings.size() < 2) {
			throw new IllegalArgumentException("interpolation calculation needs at least two values");
		}
		List<Reading> measureReadings = readings.stream() //
				.filter(r -> r.getType().equals(MEASURE)) //
				.sorted() //
				.collect(Collectors.toList());

		List<Reading> offsets = readings.stream() //
				.filter(r -> r.getType().equals(OFFSET)) //
				.collect(Collectors.toList());

		x = new double[measureReadings.size()];
		y = new double[measureReadings.size()];

		for (int i = 0; i < measureReadings.size(); i++) {
			Reading m = measureReadings.get(i);
			LocalDateTime readingDate = m.getDate();
			BigDecimal value = m.getValue();
			Optional<Reading> prev = (i - 1 >= 0) ? Optional.of(measureReadings.get(i - 1)) : Optional.empty();
			boolean valueSmallerThanPreviousReadingValue = prev.map(Reading::getValue).orElse(BigDecimal.ZERO)
					.compareTo(value) > 0;

			Optional<BigDecimal> totalOffset = offsets.stream() //
					.filter(offset -> offset.getDate().isBefore(readingDate) // offset is before
							|| (offset.getDate().equals(readingDate) && valueSmallerThanPreviousReadingValue) //
					) //
					.map(Reading::getValue) //
					.reduce((a, b) -> a.add(b));
			x[i] = dateToValue(readingDate);
			y[i] = m.getValue().add(totalOffset.orElse(BigDecimal.ZERO)).doubleValue();
		}

		minX = x[0];
		maxX = x[measureReadings.size() - 1];
	}

	private long dateToValue(LocalDateTime date) {
		return DateUtil.asMiliseconds(LocalDateTime.from(date));
	}

	public BigDecimal getValue(LocalDateTime date) {

		long xVal = dateToValue(date);

		if (xVal < minX) {
			// extrapolation
			return interpolateBetween(xVal, 0, 1);
		}
		if (xVal > maxX) {
			// extrapolation
			return interpolateBetween(xVal, x.length - 2, x.length - 1);
		}

		for (int i = 0; i < x.length; i++) {
			if (xVal == x[i]) {
				return BigDecimal.valueOf(y[i]);
			} else {
				if (xVal > x[i] && (i + 1 <= x.length) && xVal < x[i + 1]) {
					return interpolateBetween(xVal, i, i + 1);
				}
			}
		}

		return null;
	}

	private BigDecimal interpolateBetween(long xVal, int i, int j) {
		double m = (y[j] - y[i]) / (x[j] - x[i]);
		double result = y[i] + (m * (xVal - x[i]));
		return BigDecimal.valueOf(result);
	}

}
