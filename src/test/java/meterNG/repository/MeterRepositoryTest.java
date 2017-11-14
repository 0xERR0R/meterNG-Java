package meterNG.repository;

import org.junit.Test;

import meterNG.model.Meter;
import meterNG.repository.MeterRepository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

public class MeterRepositoryTest {

	@Test
	public void noMeters() {
		MeterRepository r = new MeterRepository("");
		assertThat(r.getAllMeters()).isEmpty();
		assertThat(r.findMeterByName("unknown")).isEqualTo(Optional.empty());
	}

	@Test
	public void withTwoMeters() {
		MeterRepository r = new MeterRepository("Electricity(kWh), Gas(m³)");
		assertThat(r.getAllMeters()).hasSize(2).containsExactly(new Meter("Electricity", "kWh"),
				new Meter("Gas", "m³"));
		assertThat(r.findMeterByName("Electricity")).isEqualTo(Optional.of(new Meter("Electricity", "kWh")));
		assertThat(r.findMeterByName("unknown")).isEqualTo(Optional.empty());
	}

	@Test(expected = IllegalStateException.class)
	public void withWrongConfiguration() {
		new MeterRepository("blabla");
	}
}
