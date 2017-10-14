package test;

import java.util.Locale;

import tech.lapsa.java.commons.localization.Localized;

public class TestEntity implements Localized {

    @Override
    public String displayName(DisplayNameVariant variant, Locale locale) {
	return variant + " " + locale;
    }

}
