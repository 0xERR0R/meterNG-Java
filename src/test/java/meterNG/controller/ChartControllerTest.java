package meterNG.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.common.collect.Lists;

import meterNG.aggregation.Aggregator;
import meterNG.model.ChartType;
import meterNG.model.Meter;
import meterNG.model.ReadingBuilder;
import meterNG.repository.MeterRepository;
import meterNG.repository.ReadingsRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(ChartController.class)
public class ChartControllerTest {
	@MockBean
	private ReadingsRepository readingsRepository;

	@MockBean
	private MeterRepository meterRepository;
	
	@MockBean
	private Aggregator aggregator;

	private static final Meter METER_GAS = new Meter("Gas", "m³");

	private static final Meter METER_ELECTRICITY = new Meter("Electricity", "kwH");

	@Autowired
	private MockMvc mvc;

	@Before
	public void setup() {
		given(readingsRepository.findByMeterName("Electricity")) //
				.willReturn(Lists.newArrayList( //
						ReadingBuilder.readingBuilder().date("2015-01-01").value(4711).meterName("Electricity").build()));
		given(meterRepository.getAllMeters()).willReturn(Lists.newArrayList(METER_ELECTRICITY, METER_GAS));
		given(meterRepository.findMeterByName("Gas")).willReturn(Optional.of(METER_GAS));
		given(meterRepository.findMeterByName("Electricity")).willReturn(Optional.of(METER_ELECTRICITY));
	}

	@Test
	public void totalElectricityReturnsCorrectModelAndView() throws Exception {
		mvc.perform(get("/chart/Electricity/chart/total")) //
				.andExpect(status().isOk()) //
				.andExpect(model().attribute("meterName", "Electricity")) //
				.andExpect(model().attribute("chartTypes", ChartType.values())) //
				.andExpect(model().attribute("activeChart", ChartType.total)) //
				.andExpect(model().attribute("meters", Lists.newArrayList(METER_ELECTRICITY, METER_GAS)))
				.andExpect(view().name("chart"));
	}

	@Test
	public void totalElectricityReturnsCorrectTableData() throws Exception {
		mvc.perform(get("/chart/Electricity/data/total")) //
				.andExpect(status().isOk()).andExpect(content().contentType("text/javascript; charset=UTF-8"));
	}
}
