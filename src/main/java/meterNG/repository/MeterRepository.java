package meterNG.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.google.common.collect.ImmutableList;

import meterNG.model.Meter;

@Component
public class MeterRepository {
	private final List<Meter> allMeters = new ArrayList<>();

	public MeterRepository(@Value("${config.allMeters:}") String allMetersString) {

		for (String s : allMetersString.split(",")) {
			if (s.length() > 0) {
				String[] tokens = s.split("\\(|\\)");
				Assert.state(tokens.length == 2, "wrong format");
				Meter m = new Meter(tokens[0].trim(), tokens[1].trim());
				allMeters.add(m);
			}
		}
	}

	public List<Meter> getAllMeters() {
		return ImmutableList.copyOf(allMeters);
	}

	public Optional<Meter> findMeterByName(String name) {
		return getAllMeters().stream().filter(m -> m.getName().equals(name)).findFirst();
	}

	public List<Meter> getMeterByNames(List<String> meterNamesForNotification) {

		return getAllMeters().stream().filter(m -> meterNamesForNotification.contains(m.getName()))
				.collect(Collectors.toList());
	}
}
