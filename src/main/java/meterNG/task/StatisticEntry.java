package meterNG.task;

import java.math.BigDecimal;
import java.math.RoundingMode;

import meterNG.model.Meter;
/**
 * representation of a statistic recort with consumption diffF
 * @author herzogd
 *
 */
public class StatisticEntry {
	private Meter meter;

	private String period1, period2;

	private BigDecimal consumption1, consumption2;

	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	public String getPeriod1() {
		return period1;
	}

	public void setPeriod1(String period1) {
		this.period1 = period1;
	}

	public String getPeriod2() {
		return period2;
	}

	public void setPeriod2(String period2) {
		this.period2 = period2;
	}

	public BigDecimal getConsumption1() {
		return consumption1;
	}

	public void setConsumption1(BigDecimal consumption1) {
		this.consumption1 = consumption1;
	}

	public BigDecimal getConsumption2() {
		return consumption2;
	}

	public void setConsumption2(BigDecimal consumption2) {
		this.consumption2 = consumption2;
	}

	public BigDecimal getDiff() {
		return consumption2.subtract(consumption1);
	}

	public String getDiffPercent() {
		double diff = consumption2.doubleValue() - consumption1.doubleValue();
		BigDecimal val = BigDecimal.valueOf(diff * 100.0 / consumption1.doubleValue()).setScale(2	, RoundingMode.UP);
		if(val.doubleValue()>0) return "+" + val;
		return val.toString();
	}

}
