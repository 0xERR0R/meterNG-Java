package meterNG.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.common.collect.Lists;

import meterNG.controller.MainController;
import meterNG.model.Meter;
import meterNG.repository.MeterRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {
	private static final Meter METER = new Meter("Electricity", "kWh");

	@Autowired
	private MockMvc mvc;

	@MockBean
	private MeterRepository meterRepository;

	@Before
	public void setUp() {
		given(meterRepository.getAllMeters()).willReturn(Lists.newArrayList(METER));
	}

	@Test
	public void shouldRedirectToMeterchart() throws Exception {
		mvc.perform(get("/")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/meterReadings/Electricity"));
	}
}
