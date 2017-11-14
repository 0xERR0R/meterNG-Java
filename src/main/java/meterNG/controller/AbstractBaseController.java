package meterNG.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ModelAttribute;

import meterNG.model.Meter;
import meterNG.repository.MeterRepository;

public abstract class AbstractBaseController {

	@Resource
	private MeterRepository meterRepository;

	@ModelAttribute("meters")
	public List<Meter> getAllMeters() {
		return meterRepository.getAllMeters();
	}
}
