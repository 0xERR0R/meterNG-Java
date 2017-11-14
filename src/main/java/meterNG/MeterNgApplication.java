package meterNG;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import meterNG.controller.StringToMeterConverter;
import meterNG.repository.MeterRepository;

@EntityScan(basePackageClasses = { MeterNgApplication.class, Jsr310JpaConverters.class })
@SpringBootApplication
@EnableScheduling
/**
 * Main class and spring boot configuration
 *
 */
public class MeterNgApplication extends WebMvcConfigurerAdapter {
	public static void main(String[] args) {
		SpringApplication.run(MeterNgApplication.class, args);
	}

	@Bean
	public StringToMeterConverter getStringToMeterConverter(MeterRepository r) {
		return new StringToMeterConverter(r);
	}

	@Bean
	public Java8TimeDialect java8TimeDialect() {
		return new Java8TimeDialect();
	}

	@Bean
	public ConversionService conversionService() {
		// enables conversion of String in application.properties to list
		return new DefaultConversionService();
	}
}
