package meterNG.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

import meterNG.aggregation.Aggregator;
import meterNG.model.Aggregation;
import meterNG.model.ChartType;
import meterNG.model.Meter;
import meterNG.model.Reading;
import meterNG.repository.ReadingsRepository;
import meterNG.util.DateUtil;

@RequestMapping("/chart")
@Controller
/**
 * MVC controller for showing and calculation of charts
 *
 */
public class ChartController extends AbstractBaseController {
	public static final String CONTROLLER_NAME = "chart";
	public static final String CHART = "chart";
	public static final String DATA = "data";

	@Autowired
	private MessageSource messageSource;

	@Resource
	private ReadingsRepository readingsRepository;
	
	@Resource
	private Aggregator aggregator = new Aggregator();

	private MessageSourceAccessor accessor;

	@PostConstruct
	protected void init() {
		accessor = new MessageSourceAccessor(messageSource);
	}

	@GetMapping(value = "/{meterName}/" + CHART + "/{chartType}")
	public String total(@PathVariable("meterName") String meterName, @PathVariable("chartType") ChartType chartType,
			Model uiModel) {
		uiModel.addAttribute("dataUrl", "/" + CONTROLLER_NAME + "/" + meterName + "/" + DATA + "/" + chartType);
		return returnViewAndBaseParams(uiModel, meterName, chartType);
	}

	private String returnViewAndBaseParams(Model uiModel, String meterName, ChartType chartType) {
		uiModel.addAttribute("meterName", meterName);
		uiModel.addAttribute("chartTypes", ChartType.values());
		uiModel.addAttribute("activeChart", chartType);
		return "chart";
	}

	@GetMapping(value = "/{id}")
	public String start(@PathVariable("id") String meterName, Model uiModel) {
		return total(meterName, ChartType.total, uiModel);
	}

	@GetMapping(value = "/{id}/" + DATA + "/" + "{chartType}")
	public ModelAndView dataTotal(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") Meter meter, @PathVariable("chartType") ChartType chartType)
			throws IOException, DataSourceException {
		DataSourceHelper.setServletResponse(createDataTable(meter, chartType), new DataSourceRequest(request),
				response);
		return null;
	}

	private DataTable createDataTable(Meter meter, ChartType chartType) {
		DataTable dataTable = new DataTable();
		dataTable.setCustomProperty("chartTitle",
				accessor.getMessage("chartType." + chartType + ".label") + ": " + meter.getName());
		dataTable.setCustomProperty("meterUnit", meter.getUnit());

		List<Reading> readingsForMeter = readingsRepository.findByMeterName(meter.getName());
		switch (chartType) {
		case total:
			populateTotalTable(dataTable, readingsForMeter);
			break;
		case monthAll:
			populateMonthAllTable(dataTable, readingsForMeter);
			break;
		case month:
			populateMonthTable(dataTable, readingsForMeter);
			break;
		case year:
			pupulateYearTable(dataTable, readingsForMeter);
			break;
		default:
			break;
		}
		return dataTable;
	}

	private void populateTotalTable(DataTable dataTable, List<Reading> readingsForMeter) {
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("date", ValueType.DATE, accessor.getMessage("date.label")));
		cd.add(new ColumnDescription("value", ValueType.NUMBER, accessor.getMessage("meterreading.label")));
		dataTable.addColumns(cd);

		dataTable.setCustomProperty("chartType", "LineChart");
		dataTable.setCustomProperty("hAxis.title", accessor.getMessage("readingDate.label"));

		BigDecimal minValue = BigDecimal.valueOf(Long.MAX_VALUE);

		for (Reading m : readingsForMeter) {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			Date date = Date.from(m.getDate().atZone(ZoneId.of("GMT")).toInstant());
			c.setTime(date);
			try {
				dataTable.addRowFromValues(c, m.getValue());
				minValue = minValue.min(m.getValue());
			} catch (TypeMismatchException e) {
				throw new RuntimeException(e);
			}
		}

		dataTable.setCustomProperty("vAxis.minValue", minValue.toString());
	}

	private void populateMonthAllTable(DataTable dataTable, List<Reading> readingsForMeter) {
		Collection<Aggregation> aggregationMonth = aggregator.aggregateMonth(readingsForMeter);
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("month", ValueType.TEXT, accessor.getMessage("month.label")));
		cd.add(new ColumnDescription("value", ValueType.NUMBER, accessor.getMessage("consumption.label")));
		dataTable.addColumns(cd);

		dataTable.setCustomProperty("chartType", "ColumnChart");
		dataTable.setCustomProperty("hAxis.title", accessor.getMessage("monthYear.label"));
		dataTable.setCustomProperty("vAxis.minValue", "0");

		for (Aggregation a : aggregationMonth) {
			try {
				dataTable.addRowFromValues(a.getAggregationName(), a.getAggregatedValue());
			} catch (TypeMismatchException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void pupulateYearTable(DataTable dataTable, List<Reading> readingsForMeter) {
		Collection<Aggregation> aggregationMonth = aggregator.aggregateYear(readingsForMeter);
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("year", ValueType.TEXT, accessor.getMessage("year.label")));
		cd.add(new ColumnDescription("value", ValueType.NUMBER, accessor.getMessage("consumption.label")));
		dataTable.addColumns(cd);

		dataTable.setCustomProperty("chartType", "ColumnChart");
		dataTable.setCustomProperty("hAxis.title", accessor.getMessage("year.label"));
		dataTable.setCustomProperty("vAxis.minValue", "0");

		for (Aggregation a : aggregationMonth) {
			try {
				dataTable.addRowFromValues(a.getAggregationName(), a.getAggregatedValue());
			} catch (TypeMismatchException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void populateMonthTable(DataTable dataTable, List<Reading> readingsForMeter) {
		Collection<Aggregation> aggregationMonth = aggregator.aggregateMonth(readingsForMeter);

		// Ermittlung min Jahr / max Jahr
		int minYear = 9999, maxYear = 0;
		for (Aggregation a : aggregationMonth) {
			int year = Integer.parseInt(a.getAggregationName().substring(3, 7));
			minYear = Math.min(year, minYear);
			maxYear = Math.max(year, maxYear);
		}

		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("month", ValueType.TEXT, accessor.getMessage("month.label")));
		for (int i = minYear; i <= maxYear; i++) {
			cd.add(new ColumnDescription("value" + i, ValueType.NUMBER,
					accessor.getMessage("consumption.label") + " " + i));
		}
		dataTable.addColumns(cd);

		dataTable.setCustomProperty("chartType", "ColumnChart");
		dataTable.setCustomProperty("hAxis.title", accessor.getMessage("month.label"));
		dataTable.setCustomProperty("vAxis.minValue", "0");

		int yearsCount = maxYear - minYear + 1;

		// Fuer 12 monate und alle Jahren 0 eintragen
		Map<String, List<BigDecimal>> groupByMonth = new HashMap<String, List<BigDecimal>>();
		for (int i = 1; i <= 12; i++) {
			String month = DateUtil.getMonthString(i);
			groupByMonth.put(month, new LinkedList<BigDecimal>());
			for (int j = 0; j < yearsCount; j++) {
				groupByMonth.get(month).add(BigDecimal.ZERO);
			}
		}

		// Aggregationswerte im richtigen Jahr setzen
		for (Aggregation a : aggregationMonth) {
			String month = a.getAggregationName().substring(0, 2);
			int year = Integer.parseInt(a.getAggregationName().substring(3, 7));
			groupByMonth.get(month).set(year - minYear, a.getAggregatedValue());
		}

		// Monate sortieren
		List<String> keys = new LinkedList<String>(groupByMonth.keySet());
		Collections.sort(keys);

		for (String key : keys) {
			try {
				List<Object> value = new LinkedList<Object>();
				value.add(key);
				value.addAll(groupByMonth.get(key));
				dataTable.addRowFromValues(value.toArray());
			} catch (TypeMismatchException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
