package meterNG.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import meterNG.util.DateUtil;

public final class ReadingBuilder {
	private final Reading r = new Reading();

	public ReadingBuilder date(String date) {
		r.setDate(DateUtil.parseDate(date));
		return this;
	}

	public ReadingBuilder date(LocalDateTime date) {
		r.setDate(date);
		return this;
	}

	public ReadingBuilder meterName(String meterName) {
		r.setMeterName(meterName);
		return this;
	}
	
	public ReadingBuilder type(ReadingType type) {
		r.setType(type);
		return this;
	}

	public ReadingBuilder value(long value) {
		r.setValue(BigDecimal.valueOf(value));
		return this;
	}

	public ReadingBuilder value(String value) {
		r.setValue(new BigDecimal(value));
		return this;
	}

	public Reading build() {
		return r;
	}

	public static ReadingBuilder readingBuilder() {
		return new ReadingBuilder();
	}
}