package meterNG.controller;

import static meterNG.model.ReadingBuilder.readingBuilder;
import static meterNG.model.ReadingType.OFFSET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.extractor.Extractors.byName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.util.stream.StreamSupport;

import javax.annotation.Resource;

import org.assertj.core.groups.FieldsOrPropertiesExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;

import meterNG.model.Reading;
import meterNG.repository.ReadingsRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminControllerTest {
	@Resource
	private ReadingsRepository readingsRepository;

	@Autowired
	private MockMvc mvc;

	private WebClient webClient;

	@Before
	public void setup() {
		webClient = MockMvcWebClientBuilder.mockMvcSetup(mvc).build();
		// Test data
		readingsRepository.save(readingBuilder().date("2015-01-01").meterName("Electricity").value("4711.24").build());
		readingsRepository.save(readingBuilder().date("2015-01-01").meterName("Gas").value("4712").build());
	}

	@Test
	public void performCsvExport() throws Exception {

		mvc.perform(post("/admin/export")).andExpect(status().isOk()).andExpect(content().contentType("text/plain"))
				.andExpect(content().string("Electricity;2015-01-01;4711.24;MEASURE\nGas;2015-01-01;4712;MEASURE\n"));
	}

	@Test
	public void checkImportPage() throws Exception {
		HtmlPage page = webClient.getPage("http://localhost/admin/import");
		HtmlRadioButtonInput fullOption = (HtmlRadioButtonInput) page.getElementById("full");
		assertThat(fullOption).isNotNull();
		assertThat(fullOption.isChecked()).isTrue();

		HtmlRadioButtonInput incrementalOption = (HtmlRadioButtonInput) page.getElementById("incremental");
		assertThat(incrementalOption).isNotNull();

		HtmlFileInput fileInput = (HtmlFileInput) page.getElementById("file");
		assertThat(fileInput).isNotNull();

		HtmlButton submitButton = (HtmlButton) page.getElementById("submitButton");
		assertThat(submitButton).isNotNull();
	}

	@Test
	public void performFullImport() throws Exception {
		FileInputStream fis = new FileInputStream("./src/test/resources/import-testdata/valid-import-file.csv");
		MockMultipartFile upload = new MockMultipartFile("file", fis);

		mvc.perform(MockMvcRequestBuilders.fileUpload("/admin/import").file(upload).param("importOption", "full"))
				.andExpect(status().is(200));

		assertThat(readingsRepository.findAll()).hasSize(4);
		assertThat(StreamSupport.stream(readingsRepository.findAll().spliterator(), false)
				.filter(r -> r.getType().equals(OFFSET)).count()).isEqualTo(1);
	}

	@Test
	public void performIncrementalImport() throws Exception {
		FileInputStream fis = new FileInputStream("./src/test/resources/import-testdata/valid-import-file.csv");
		MockMultipartFile upload = new MockMultipartFile("file", fis);

		mvc.perform(
				MockMvcRequestBuilders.fileUpload("/admin/import").file(upload).param("importOption", "incremental"))
				.andExpect(status().is(200));

		assertThat(readingsRepository.findAll()).hasSize(6);
	}

	@Test
	public void importAndExportWithSameFile() throws Exception {
		Iterable<Reading> beforeExport = readingsRepository.findAllByOrderByMeterNameAscDateAsc();
		byte[] exportFileAsByteArray = mvc.perform(post("/admin/export")).andReturn().getResponse()
				.getContentAsByteArray();

		readingsRepository.deleteAll();
		MockMultipartFile upload = new MockMultipartFile("file", exportFileAsByteArray);

		mvc.perform(MockMvcRequestBuilders.fileUpload("/admin/import").file(upload).param("importOption", "full"))
				.andExpect(status().is(200));

		assertThat(readingsRepository.findAll()).extracting("meterName", "value", "date").containsExactlyElementsOf(
				FieldsOrPropertiesExtractor.extract(beforeExport, byName("meterName", "value", "date")));
	}

}
