package meterNG.model;

import java.util.Objects;

public class Meter {
	private String name;
	private String unit;

	public Meter() {
	}

	public Meter(String name, String unit) {
		super();
		this.name = name;
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, unit);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Meter)) {
			return false;
		}
		Meter o = (Meter) obj;

		return Objects.equals(name, o.name) && Objects.equals(unit, o.unit);
	}

}
