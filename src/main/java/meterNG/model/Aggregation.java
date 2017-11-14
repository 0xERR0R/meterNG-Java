package meterNG.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * represents an aggregated value for a particular time period
 *
 */
public class Aggregation {
	private final BigDecimal aggregatedValue;
	private final String aggregationName;

	public Aggregation(BigDecimal aggregatedValue, String aggregationName) {
		super();
		this.aggregatedValue = aggregatedValue;
		this.aggregationName = aggregationName;
	}

	public BigDecimal getAggregatedValue() {
		return aggregatedValue;
	}

	public String getAggregationName() {
		return aggregationName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAggregationName(), getAggregatedValue());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		Aggregation other = (Aggregation) o;
		return Objects.equals(getAggregationName(), other.getAggregationName())
				&& Objects.equals(getAggregatedValue(), other.getAggregatedValue());
	}

	@Override
	public String toString() {
		return "Aggregation [aggregatedValue=" + aggregatedValue + ", aggregationName=" + aggregationName + "]";
	}

}
