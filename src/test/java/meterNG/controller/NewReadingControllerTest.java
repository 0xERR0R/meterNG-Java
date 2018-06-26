package meterNG.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDateInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlNumberInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;

import meterNG.model.Meter;
import meterNG.model.Reading;
import meterNG.model.ReadingBuilder;
import meterNG.repository.MeterRepository;
import meterNG.repository.ReadingsRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(NewReadingController.class)
public class NewReadingControllerTest {

	private static final Meter METER_GAS = new Meter("Gas", "m³");

	private static final Meter METER_ELECTRICITY = new Meter("Electricity", "kwH");

	@Autowired
	private MockMvc mvc;

	private WebClient webClient;

	@MockBean
	private ReadingsRepository readingsRepository;

	@MockBean
	private MeterRepository meterRepository;

	@Before
	public void setup() {
		webClient = MockMvcWebClientBuilder.mockMvcSetup(mvc).build();
		given(readingsRepository.findAllReadingsLastDate()).willReturn(LocalDate.parse("2015-01-01"));
		given(meterRepository.getAllMeters()).willReturn(Lists.newArrayList(METER_ELECTRICITY, METER_GAS));
		given(meterRepository.findMeterByName("Gas")).willReturn(Optional.of(METER_GAS));
		given(meterRepository.findMeterByName("Electricity")).willReturn(Optional.of(METER_ELECTRICITY));
	}

	@Test
	public void recordPage() throws Exception {
		HtmlPage page = webClient.getPage("http://localhost/record");
		HtmlDateInput dateInput = (HtmlDateInput) page.getElementById("date");
		assertThat(dateInput).isNotNull().hasFieldOrPropertyWithValue("defaultValue", LocalDate.now().toString());

		assertThat(page.getElementById("lastReadingDateText").getTextContent()).contains("Last record date:  1/1/15");
		assertThat(page.getElementById("Gas")).isNotNull().isExactlyInstanceOf(HtmlNumberInput.class);
		assertThat(page.getElementById("Electricity")).isNotNull().isExactlyInstanceOf(HtmlNumberInput.class);
	}

	@Test
	public void recordPageWithoutLastReadingDate() throws Exception {
		given(readingsRepository.findAllReadingsLastDate()).willReturn(null);

		HtmlPage page = webClient.getPage("http://localhost/record");

		assertThat(page.getElementById("lastReadingDateText").getTextContent()).contains("Last record date:  -");
	}

	@Test
	public void createRecord() throws Exception {
		HtmlPage page = webClient.getPage("http://localhost/record");
		HtmlForm form = page.getHtmlElementById("recordForm");
		HtmlDateInput dateInput = (HtmlDateInput) page.getElementById("date");
		dateInput.setValueAttribute("2016-01-01");
		HtmlNumberInput gasValue = (HtmlNumberInput) page.getElementById("Gas");
		gasValue.setValueAttribute("1234.56");
		HtmlNumberInput ElectricityValue = (HtmlNumberInput) page.getElementById("Electricity");
		ElectricityValue.setValueAttribute("4711");

		HtmlButton submit = form.getOneHtmlElementByAttribute("button", "type", "submit");
		submit.click();

		Reading expected1 = ReadingBuilder.readingBuilder().date("2016-01-01").meterName("Gas").value("1234.56").build();
		Reading expected2 = ReadingBuilder.readingBuilder().date("2016-01-01").meterName("Electricity").value("4711").build();

		Mockito.verify(readingsRepository).save(expected1);
		Mockito.verify(readingsRepository).save(expected2);
	}
}
