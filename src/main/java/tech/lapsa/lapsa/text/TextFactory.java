package tech.lapsa.lapsa.text;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;

public final class TextFactory {
    private TextFactory() {
    }

    public static TextModelBuilder newModelBuilder() {
	return new TextModelBuilder();
    }

    public final static class TextModelBuilder {
	private final Map<String, Object> bindings = new HashMap<>();

	private TextModelBuilder() {
	    withLocale(Locale.getDefault());
	}

	public TextModelBuilder withLocale(final Locale locale) {
	    MyObjects.requireNonNull(locale, "locale");
	    bindings.put("locale", locale);
	    bindings.put("lang", locale.getLanguage());
	    bindings.put("date", new DateTool(locale));
	    bindings.put("localized", new LocalizedTool(locale));
	    return this;
	}

	public TextModelBuilder withLanguageTag(final String lang) {
	    MyStrings.requireNonEmpty(lang, "lang");
	    final Locale locale = Locale.forLanguageTag(lang);
	    return withLocale(locale);
	}

	public TextModelBuilder bind(final String name, final Object object) {
	    if (bindings.putIfAbsent(MyStrings.requireNonEmpty(name, "name"),
		    MyObjects.requireNonNull(object, "object")) != null)
		throw new IllegalStateException("Already has bindings with name ''");
	    return this;
	}

	public TextModelBuilder bindOptional(final String name, final Optional<?> optional) {
	    MyObjects.requireNonNull(optional, "optional") //
		    .ifPresent(x -> bind(name, x));
	    return this;
	}

	public TextModelBuilder bindProperties(final Properties properties) {
	    MyObjects.requireNonNull(properties, "properties") //
		    .entrySet() //
		    .stream() //
		    .forEach(x -> {
			try {
			    bind(x.getKey().toString(), x.getValue());
			} catch (final IllegalStateException ignored) {
			}
		    });
	    return this;
	}

	public TextModel build() {
	    return new TextModel();
	}

	public final class TextModel {
	    private final VelocityContext context;

	    private TextModel() {
		context = new VelocityContext();
		bindings.entrySet().stream() //
			.forEach(x -> context.put(x.getKey(), x.getValue()));
	    }
	}
    }

    public static TextTemplateBuilder newTextTemplateBuilder() {
	return new TextTemplateBuilder();
    }

    public final static class TextTemplateBuilder {
	private Charset inputEncoding;

	private TextTemplateBuilder() {
	    withInputEncoding(Charset.forName("UTF-8"));
	}

	public TextTemplateBuilder withInputEncoding(final Charset inputEncoding) {
	    this.inputEncoding = MyObjects.requireNonNull(inputEncoding, "inputEncoding");
	    return this;
	}

	public TextTemplate buildFromReader(final Reader reader) {
	    MyObjects.requireNonNull(reader, "reader");
	    final RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
	    SimpleNode node;
	    try {
		node = runtimeServices.parse(reader, "template");
	    } catch (final ParseException e) {
		throw new IllegalArgumentException("Invalid pattern", e);
	    }
	    final Template t = new Template();
	    t.setRuntimeServices(runtimeServices);
	    t.setData(node);
	    t.initDocument();
	    return new TextTemplate(t);
	}

	public TextTemplate buildFromPattern(final String pattern) {
	    return buildFromReader(new StringReader(MyStrings.requireNonEmpty(pattern, "pattern")));
	}

	public TextTemplate buildFromInputStream(final InputStream stream) {
	    return buildFromInputStream(stream, inputEncoding);
	}

	public TextTemplate buildFromInputStream(final InputStream stream, final Charset charset) {
	    return buildFromReader(new InputStreamReader(MyObjects.requireNonNull(stream, "stream"),
		    MyObjects.requireNonNull(charset, "charset")));
	}

	public final class TextTemplate {

	    private final Template t;

	    private TextTemplate(final Template t) {
		this.t = t;
	    }

	    public Text merge(final TextModel textModel) {
		final StringWriter w = new StringWriter();
		t.merge(textModel.context, w);
		return new Text(w.toString());
	    }

	    public final class Text {
		private final String value;

		private Text(final String value) {
		    this.value = MyObjects.requireNonNull(value, "value");
		}

		public Reader asReader() {
		    return new StringReader(value);
		}

		public InputStream asBytesStream() {
		    return new ByteArrayInputStream(value.getBytes());
		}

		public InputStream asBytesStream(final Charset charset) {
		    return new ByteArrayInputStream(value.getBytes(MyObjects.requireNonNull(charset, "charset")));
		}

		public StringBuilder asBuilder() {
		    return new StringBuilder(value);
		}

		public String asString() {
		    return value;
		}

		@Override
		public String toString() {
		    return asString();
		}
	    }

	}
    }
}
