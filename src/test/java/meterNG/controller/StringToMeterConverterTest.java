package meterNG.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import meterNG.controller.StringToMeterConverter;
import meterNG.model.Meter;
import meterNG.repository.MeterRepository;

public class StringToMeterConverterTest {
	private static final Meter METER = new Meter("Electricity", "kWh");
	private StringToMeterConverter converter;

	@Before
	public void setUp() {
		MeterRepository r = mock(MeterRepository.class);
		when(r.findMeterByName("Electricity")).thenReturn(Optional.of(METER));
		when(r.findMeterByName("unknown")).thenReturn(Optional.empty());
		converter = new StringToMeterConverter(r);
	}

	@Test
	public void meterExists() {
		assertThat(converter.convert("Electricity")).isEqualTo(METER);
	}

	@Test(expected = IllegalStateException.class)
	public void meterDoesNotExists() {
		converter.convert("unknown");
	}
}
