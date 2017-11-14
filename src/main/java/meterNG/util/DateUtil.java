package meterNG.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class DateUtil {
	public static final LocalDateTime getStartOfMonth(LocalDateTime date) {
		return LocalDateTime.from(date).withDayOfMonth(1);
	}

	public static final LocalDateTime getStartOfYear(LocalDateTime date) {
		return LocalDateTime.from(date).withDayOfMonth(1).withMonth(1);
	}

	public static long diffDays(LocalDate date1, LocalDate date2) {
		return Math.abs(ChronoUnit.DAYS.between(date1, date2));
	}

	public static final LocalDateTime getEndOfMonth(LocalDateTime date) {
		LocalDate d = LocalDate.from(date);
		return date.withDayOfMonth(d.lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
	}

	public static final LocalDateTime getEndOfYear(LocalDateTime date) {
		return date.withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59).withSecond(59);
	}

	public static String getMonthYearString(LocalDateTime date) {
		return getMonthString(date.getMonthValue()) + "/" + date.getYear();
	}

	public static String getMonthString(int month) {
		return month < 10 ? ("0" + month) : Integer.toString(month);
	}

	public static long asMiliseconds(LocalDateTime date) {
		LocalDateTime time = LocalDateTime.from(date);
		return time.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
	}

	public static LocalDateTime addMonth(LocalDateTime date) {
		return date.plus(1, ChronoUnit.MONTHS);
	}

	public static LocalDateTime addYear(LocalDateTime date) {
		return date.plus(1, ChronoUnit.YEARS);
	}

	public static LocalDateTime parseDate(String date) {
		return LocalDate.parse(date).atStartOfDay();
	}

}
