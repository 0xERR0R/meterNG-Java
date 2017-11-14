package meterNG.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import meterNG.model.Meter;
import meterNG.model.Reading;
import meterNG.model.ReadingBuilder;
import meterNG.repository.MeterRepository;
import meterNG.repository.ReadingsRepository;

@RequestMapping("/record")
@Controller
/**
 * MVC controller for new meter reading records
 *
 */
public class NewReadingController {

	@Resource
	private MeterRepository meterRepository;

	@Resource
	private ReadingsRepository readingRepository;

	@GetMapping
	public String showNewReadingPage(Model uiModel, Locale locale) {
		uiModel.addAttribute("meters", meterRepository.getAllMeters());
		Optional<LocalDate> lastReadingsDate = Optional.ofNullable(readingRepository.findAllReadingsLastDate());
		uiModel.addAttribute("lastReadingDate",
				lastReadingsDate
						.map(d -> d.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)))
						.orElse("-"));
		uiModel.addAttribute("currentDate", LocalDate.now());
		return "record";

	}

	@PostMapping
	public String storeNewReadings(@RequestParam Map<String, String> formData) {
		LocalDateTime readingTime = LocalDate.parse((String) formData.get("date")).atStartOfDay();

		for (String meterName : formData.keySet()) {
			if (meterName.equals("date")) {
				continue;
			}

			Optional<Meter> meter = meterRepository.findMeterByName(meterName);

			if (meter.isPresent()) {
				String valueAsString = (String) formData.get(meterName);
				if (valueAsString != null && valueAsString.length() > 0) {
					Reading reading = ReadingBuilder.builder().date(readingTime).meterName(meterName)
							.value(valueAsString).build();
					readingRepository.save(reading);
				}
			}
		}
		return "recordOk";
	}

}
