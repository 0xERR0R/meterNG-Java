package meterNG.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import meterNG.export.ReadingsCsvExporter;
import meterNG.task.EmailMonthlyStatisticTask;

@RequestMapping("/admin")
@Controller
/**
 * MVC controller for admin tasks like import and export
 *
 */
public class AdminController extends AbstractBaseController {

	public enum ImportOption {
		full, incremental
	}

	@Resource
	private ReadingsCsvExporter exporter;

	@Autowired(required = false)
	private EmailMonthlyStatisticTask statisticTask;

	@GetMapping("/export")
	public String showExportReadingsView() {
		return "export";
	}

	@PostMapping("/export")
	public void createExportFile(@RequestParam Map<String, String> formData, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename=" + LocalDate.now() + ".csv");
		PrintWriter writer = response.getWriter();
		exporter.writeAsCsv(writer);
		writer.flush();
		writer.close();
	}

	@GetMapping("/import")
	public String showImportReadingsView() {
		return "import";
	}

	@PostMapping(path = "/import", consumes = { "multipart/*" })
	@Transactional
	public String performImport(@RequestParam("file") MultipartFile file,
			@RequestParam("importOption") ImportOption importType, Model uiModel) throws IOException {
		int count = exporter.importReadings(importType, file.getInputStream());
		uiModel.addAttribute("importCount", count);
		return "import";
	}

	@GetMapping("/tasks/runMonthlyStatisticTask")
	public @ResponseBody String runMonthlyStatisticTask() throws MessagingException {
		statisticTask.runTask();
		return "OK";
	}

}
