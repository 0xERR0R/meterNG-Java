package meterNG.export;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import meterNG.controller.AdminController.ImportOption;
import meterNG.model.Reading;
import meterNG.model.ReadingBuilder;
import meterNG.model.ReadingType;
import meterNG.repository.ReadingsRepository;

/**
 * handles csv import and export of meter readings
 *
 */
@Service
public class ReadingsCsvExporter {

	@Autowired
	private ReadingsRepository readingsRepository;

	public void writeAsCsv(Writer writer) throws IOException {
		for (Reading r : readingsRepository.findAllByOrderByMeterNameAscDateAsc()) {
			writer.write(Joiner.on(";").join(r.getMeterName(), r.getDate().toLocalDate(), r.getValue(), r.getType()));
			writer.append("\n");
		}
	}

	public int importReadings(ImportOption importType, InputStream inputStream) throws IOException {
		switch (importType) {
		case full:
			readingsRepository.deleteAll();
			break;
		default:
			break;
		}
		List<Reading> parsedItems = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] tokens = line.split(";");
					Reading reading = ReadingBuilder.readingBuilder().meterName(tokens[0]).date(parseDate(tokens[1]))
							.value(tokens[2]).type(ReadingType.valueOf(tokens[3])).build();
					parsedItems.add(reading);
				}
			}
		}
		readingsRepository.save(parsedItems);
		return parsedItems.size();
	}

	private LocalDateTime parseDate(String in) {
		if (in.matches("\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d")) {
			return LocalDate.parse(in, DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay();
		} else {
			return LocalDate.parse(in).atStartOfDay();
		}
	}
}
