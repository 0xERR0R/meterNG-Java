package meterNG.controller;

import org.springframework.core.convert.converter.Converter;

import meterNG.model.Meter;
import meterNG.repository.MeterRepository;

/**
 * Converts string to a meter, uses this string as Id
 *
 */
public class StringToMeterConverter implements Converter<String, Meter> {

	private final MeterRepository meterRepository;

	public StringToMeterConverter(MeterRepository meterRepository) {
		this.meterRepository = meterRepository;
	}

	@Override
	public Meter convert(String name) {
		return meterRepository.findMeterByName(name)
				.orElseThrow(() -> new IllegalStateException("Unknown meter '" + name + "'"));
	}

}
