package meterNG.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import meterNG.model.Reading;

@Component
public interface ReadingsRepository extends CrudRepository<Reading, Long> {

	List<Reading> findByMeterName(String meterName);

	Iterable<Reading> findAllByOrderByMeterNameAscDateAsc();

	@Query("select max(date) from Reading where meterName in (?1)")
	LocalDate findReadingsLastDateByMeterNames(Collection<String> meterNames);

	@Query("select max(date) from Reading")
	LocalDate findAllReadingsLastDate();
}
