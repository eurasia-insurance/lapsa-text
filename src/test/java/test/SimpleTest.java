package test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import tech.lapsa.java.commons.localization.Localized;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder;
import tech.lapsa.lapsa.text.TextFactory.TextTemplateBuilder.TextTemplate;

public class SimpleTest {
    public static void main(String[] args) {

	TextModelBuilder textModelBuilder = TextFactory.newModelBuilder()
		.bind("instant", Instant.now())
		.bind("localTime", LocalTime.now())
		.bind("localDate", LocalDate.now())
		.bind("localDateTime", LocalDateTime.now())
		.bind("name", new TestEntity());

	TextTemplate template = TextFactory.newTextTemplateBuilder().buildFromPattern(""
		+ "----------\n"
		+ "$lang \n"
		+ "----------\n"
		+ "NORMAL \n"
		+ "$date.normal($instant) \n"
		+ "$date.normal($localTime) \n"
		+ "$date.normal($localDateTime) \n"
		+ "$date.normal($localDate) \n"
		+ "$display.normal($name) \n"
		+ "FEW \n"
		+ "$date.few($instant) \n"
		+ "$date.few($localTime) \n"
		+ "$date.few($localDateTime) \n"
		+ "$date.few($localDate) \n"
		+ "$display.few($name) \n"
		+ "FULL \n"
		+ "$date.full($instant) \n"
		+ "$date.full($localTime) \n"
		+ "$date.full($localDateTime) \n"
		+ "$date.full($localDate) \n"
		+ "$display.full($name) \n"
		+ "");

	System.out.println(template.merge(textModelBuilder.withLocale(Locale.forLanguageTag("kk")).build()));
	System.out.println(template.merge(textModelBuilder.withLocale(Locale.forLanguageTag("ru")).build()));
	System.out.println(template.merge(textModelBuilder.withLocale(Locale.forLanguageTag("en")).build()));
    }

    static class TestEntity implements Localized {
	@Override
	public String localized(LocalizationVariant variant, Locale locale) {
	    return variant + " " + locale;
	}
    }
}
