package meterNG.controller;

import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import meterNG.model.Meter;
import meterNG.repository.MeterRepository;

@RequestMapping("/")
@Controller
public class MainController {

	@Resource
	private MeterRepository repository;

	@GetMapping
	public String list(Model uiModel) {
		Optional<String> meterName = repository.getAllMeters().stream().map(Meter::getName).findFirst();
		if (meterName.isPresent()) {
			return "redirect:/" + ChartController.CONTROLLER_NAME + "/" + repository.getAllMeters().get(0).getName();
		} else {
			return "main";
		}

	}

}
