package tech.lapsa.lapsa.text;

import java.util.Locale;

import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyOptionals;
import tech.lapsa.java.commons.localization.Localized;

public final class LocalizedTool {

    private final Locale locale;

    LocalizedTool(final Locale locale) {
	this.locale = MyObjects.requireNonNull(locale, "locale");
    }

    public String regular(final Localized entity) {
	return MyOptionals.of(entity) //
		.map(x -> x.regular(locale)) //
		.orElse(null);
    }

    public String few(final Localized entity) {
	return MyOptionals.of(entity) //
		.map(x -> x.few(locale)) //
		.orElse(null);
    }

    public String full(final Localized entity) {
	return MyOptionals.of(entity) //
		.map(x -> x.full(locale)) //
		.orElse(null);
    }
}
