package test;

import java.util.Locale;

import tech.lapsa.java.commons.localization.Localized;

public class TestEntity implements Localized {

    private static final long serialVersionUID = 1L;

    @Override
    public String localized(final LocalizationVariant variant, final Locale locale) {
	return variant + " " + locale;
    }

}
