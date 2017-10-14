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

    public static class TextModelBuilder {
	private Map<String, Object> bindings = new HashMap<>();

	private TextModelBuilder() {
	    withLocale(Locale.getDefault());
	}

	public TextModelBuilder withLocale(Locale locale) {
	    MyObjects.requireNonNull(locale, "locale");
	    bindings.put("locale", locale);
	    bindings.put("lang", locale.getLanguage());
	    bindings.put("date", new DateTool(locale));
	    bindings.put("display", new DisplayTool(locale));
	    return this;
	}

	public TextModelBuilder withLanguageTag(String lang) {
	    MyStrings.requireNonEmpty(lang, "lang");
	    Locale locale = Locale.forLanguageTag(lang);
	    return withLocale(locale);
	}

	public TextModelBuilder bind(String name, Object object) {
	    if (bindings.putIfAbsent(MyStrings.requireNonEmpty(name, "name"),
		    MyObjects.requireNonNull(object, "object")) != null)
		throw new IllegalStateException("Already has bindings with name ''");
	    return this;
	}

	public TextModelBuilder bindOptional(String name, Optional<?> optional) {
	    MyObjects.requireNull(optional, "optional") //
		    .ifPresent(x -> bind(name, x));
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

    public static class TextTemplateBuilder {
	private Charset inputEncoding;

	private TextTemplateBuilder() {
	    withInputEncoding(Charset.forName("UTF-8"));
	}

	public TextTemplateBuilder withInputEncoding(Charset inputEncoding) {
	    this.inputEncoding = MyObjects.requireNonNull(inputEncoding, "inputEncoding");
	    return this;
	}

	public TextTemplate buildFromReader(Reader reader) {
	    MyObjects.requireNonNull(reader, "reader");
	    RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
	    SimpleNode node;
	    try {
		node = runtimeServices.parse(reader, "template");
	    } catch (ParseException e) {
		throw new IllegalArgumentException("Invalid pattern", e);
	    }
	    Template t = new Template();
	    t.setRuntimeServices(runtimeServices);
	    t.setData(node);
	    t.initDocument();
	    return new TextTemplate(t);
	}

	public TextTemplate buildFromPattern(String pattern) {
	    return buildFromReader(new StringReader(MyStrings.requireNonEmpty(pattern, "pattern")));
	}

	public TextTemplate buildFromInputStream(InputStream stream) {
	    return buildFromInputStream(stream, inputEncoding);
	}

	public TextTemplate buildFromInputStream(InputStream stream, Charset charset) {
	    return buildFromReader(new InputStreamReader(MyObjects.requireNonNull(stream, "stream"),
		    MyObjects.requireNonNull(charset, "charset")));
	}

	public final class TextTemplate {

	    private final Template t;

	    private TextTemplate(Template t) {
		this.t = t;
	    }

	    public Text merge(TextModel textModel) {
		StringWriter w = new StringWriter();
		t.merge(textModel.context, w);
		return new Text(w.toString());
	    }

	    public final class Text {
		private final String value;

		private Text(String value) {
		    this.value = MyObjects.requireNonNull(value, "value");
		}

		public Reader asReader() {
		    return new StringReader(value);
		}

		public InputStream asBytesStream() {
		    return new ByteArrayInputStream(value.getBytes());
		}

		public InputStream asBytesStream(Charset charset) {
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
