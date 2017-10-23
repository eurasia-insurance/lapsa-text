package tech.lapsa.lapsa.text;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

import tech.lapsa.java.commons.function.MyOptionals;

public final class DateTool {

    private static final FormatStyle NORMAL_STYLE = FormatStyle.MEDIUM;
    private static final FormatStyle FULL_STYLE = FormatStyle.MEDIUM;
    private static final FormatStyle FEW_STYLE = FormatStyle.SHORT;

    private final DateTimeFormatter normalTime;
    private final DateTimeFormatter normalTimestamp;
    private final DateTimeFormatter normalDate;

    private final DateTimeFormatter fullTime;
    private final DateTimeFormatter fullTimestamp;
    private final DateTimeFormatter fullDate;

    private final DateTimeFormatter fewTime;
    private final DateTimeFormatter fewTimestamp;
    private final DateTimeFormatter fewDate;

    DateTool(Locale locale) {
	normalTime = df(null, NORMAL_STYLE, locale);
	normalTimestamp = df(NORMAL_STYLE, NORMAL_STYLE, locale);
	normalDate = df(NORMAL_STYLE, null, locale);

	fullTime = df(null, FULL_STYLE, locale);
	fullTimestamp = df(FULL_STYLE, FULL_STYLE, locale);
	fullDate = df(FULL_STYLE, null, locale);

	fewTime = df(null, FEW_STYLE, locale);
	fewTimestamp = df(FEW_STYLE, FEW_STYLE, locale);
	fewDate = df(FEW_STYLE, null, locale);
    }

    private static DateTimeFormatter df(FormatStyle dateStyle, FormatStyle timeStyle, Locale locale) {
	return new DateTimeFormatterBuilder().appendLocalized(dateStyle, timeStyle).toFormatter(locale);

    }

    // full

    public String full(Instant instant) {
	return MyOptionals.of(instant) //
		.map(x -> fullTimestamp.format(LocalDateTime.ofInstant(x, ZoneId.systemDefault()))) //
		.orElse(null);
    }

    public String full(LocalDate localDate) {
	return MyOptionals.of(localDate) //
		.map(x -> fullDate.format(x)) //
		.orElse(null);
    }

    public String full(LocalDateTime localDateTime) {
	return MyOptionals.of(localDateTime) //
		.map(x -> fullTimestamp.format(x)) //
		.orElse(null);
    }

    public String full(LocalTime localTime) {
	return MyOptionals.of(localTime) //
		.map(x -> fullTime.format(x)) //
		.orElse(null);
    }

    // few

    public String few(Instant instant) {
	return MyOptionals.of(instant) //
		.map(x -> fewTimestamp.format(LocalDateTime.ofInstant(x, ZoneId.systemDefault()))) //
		.orElse(null);
    }

    public String few(LocalDate localDate) {
	return MyOptionals.of(localDate) //
		.map(x -> fewDate.format(x)) //
		.orElse(null);
    }

    public String few(LocalDateTime localDateTime) {
	return MyOptionals.of(localDateTime) //
		.map(x -> fewTimestamp.format(x)) //
		.orElse(null);
    }

    public String few(LocalTime localTime) {
	return MyOptionals.of(localTime) //
		.map(x -> fewTime.format(x)) //
		.orElse(null);
    }

    // regular

    public String regular(Instant instant) {
	return MyOptionals.of(instant) //
		.map(x -> normalTimestamp.format(LocalDateTime.ofInstant(x, ZoneId.systemDefault()))) //
		.orElse(null);
    }

    public String regular(LocalDate localDate) {
	return MyOptionals.of(localDate) //
		.map(x -> normalDate.format(x)) //
		.orElse(null);
    }

    public String regular(LocalDateTime localDateTime) {
	return MyOptionals.of(localDateTime) //
		.map(x -> normalTimestamp.format(x)) //
		.orElse(null);
    }

    public String regular(LocalTime localTime) {
	return MyOptionals.of(localTime) //
		.map(x -> normalTime.format(x)) //
		.orElse(null);
    }
}
