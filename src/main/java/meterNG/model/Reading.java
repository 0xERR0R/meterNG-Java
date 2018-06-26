package meterNG.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Reading implements Comparable<Reading> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String meterName;

	@Column(nullable = false)
	private BigDecimal value;

	@Column(nullable = false)
	private LocalDateTime date;

	@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
	private ReadingType type = ReadingType.MEASURE;

	public Long getId() {
		return id;
	}

	public String getMeterName() {
		return meterName;
	}

	public BigDecimal getValue() {
		return value;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMeterName(String meterName) {
		this.meterName = meterName;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public ReadingType getType() {
		return type;
	}

	public void setType(ReadingType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, meterName, value, date, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Reading)) {
			return false;
		}
		Reading o = (Reading) obj;

		return Objects.equals(id, o.id) && Objects.equals(meterName, o.meterName) && Objects.equals(value, o.value)
				&& Objects.equals(date, o.date) && Objects.equals(type, o.type);
	}

	@Override
	public int compareTo(Reading o) {
		return getDate().compareTo(o.getDate());
	}

	@Override
	public String toString() {
		return String.format("Reading [meterName=%s, value=%s, date=%s, type=%s]", meterName, value, date, type);
	}

}
