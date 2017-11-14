package meterNG.aggregation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import meterNG.model.Reading;
import meterNG.util.DateUtil;

/**
 * Calculates a reading value on a arbitrary date using linear interpolation.
 *
 */
public class Interpolator {

	private final double[] x, y;
	private final double minX, maxX;

	public Interpolator(List<Reading> readings) {
		if (readings.size() < 2) {
			throw new IllegalArgumentException("interpolation calculation needs at least two values");
		}
		List<Reading> copy = new LinkedList<>(readings);
		Collections.sort(copy);
		x = new double[copy.size()];
		y = new double[copy.size()];

		for (int i = 0; i < copy.size(); i++) {
			Reading m = copy.get(i);
			x[i] = dateToValue(m.getDate());
			y[i] = m.getValue().doubleValue();
		}

		minX = x[0];
		maxX = x[copy.size() - 1];
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
