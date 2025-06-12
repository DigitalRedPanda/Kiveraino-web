package com.digiunion.model;

/**
 * Generated Renderer.
 */
// @javax.annotation.Generated("io.jstach.apt.GenerateRendererProcessor")
public class URLRenderer implements io.jstach.jstachio.Template.EncodedTemplate<com.digiunion.model.URL>,
    io.jstach.jstachio.context.ContextTemplate<com.digiunion.model.URL>,
    io.jstach.jstachio.spi.TemplateProvider,
    io.jstach.jstachio.spi.JStachioFilter.FilterChain {
    /**
     * Template path.
     * @hidden
     */
    public static final String TEMPLATE_PATH = "template/main.mustache";

    /**
     * Inline template string copied.
     * @hidden
     */

    public static final String TEMPLATE_STRING = "";

    /**
     * Template name. Do not rely on this.
     * @hidden
     */
    public static final String TEMPLATE_NAME = "com.digiunion.model.URLRenderer";

    /**
     * Template charset.
     * @hidden
     */
    public static final java.nio.charset.Charset TEMPLATE_CHARSET = java.nio.charset.StandardCharsets.UTF_8;

    /**
     * Template mediaType.
     * @hidden
     */
    public static final String TEMPLATE_MEDIA_TYPE = "text/html";

    /**
     * The models class. Use {@link #modelClass()} instead.
     * @hidden
     */
    public static final Class<?> MODEL_CLASS = com.digiunion.model.URL.class;

    /**
     * The instance. Use {@link {@link #of()} instead.
     * @hidden
     */
    private static final URLRenderer INSTANCE = new URLRenderer();

    /**
     * Formatter. 
     * @hidden
     */
    private final io.jstach.jstachio.Formatter formatter;

    /**
     * Escaper. 
     * @hidden
     */
    private final io.jstach.jstachio.Escaper escaper;

    /**
     * Renderer constructor for manual wiring.
     * @param formatter formatter if null the static formatter will be used.
     * @param escaper escaper if null the static escaper will be used
     */
    public URLRenderer(
        java.util.function./* @Nullable */ Function</* @Nullable */ Object, String> formatter,
        java.util.function./* @Nullable */ Function<String, String> escaper) {
        super();
        this.formatter = __formatter(formatter);
        this.escaper = __escaper(escaper);
    }

    private static io.jstach.jstachio.Formatter __formatter(java.util.function./* @Nullable */ Function</* @Nullable */ Object, String> formatter) {
        return io.jstach.jstachio.Formatter.of(formatter != null ? formatter : io.jstach.jstachio.formatters.DefaultFormatter.provider());
    }

    private static io.jstach.jstachio.Escaper __escaper(java.util.function./* @Nullable */ Function<String, String> escaper) {
        return io.jstach.jstachio.Escaper.of(escaper != null ? escaper : io.jstach.jstachio.escapers.Html.provider());
    }

    /**
     * Renderer constructor for reflection (use of() instead).
     * For programmatic consider using {@link #of()} for a shared singleton.
     */
    public URLRenderer() {
        this(null, null);
    }

    @Override
    public StringBuilder execute(com.digiunion.model.URL model, StringBuilder sb) {
        render(model, io.jstach.jstachio.Output.of(sb), this.formatter, this.escaper, templateAppender());
        return sb;
    }

    @Override
    public <A extends io.jstach.jstachio.Output<E>, E extends Exception> A execute(
        com.digiunion.model.URL model, 
        A a) throws E {
        render(model, a, this.formatter, this.escaper, templateAppender());
        return a;
    }

    /**
     * Renders the passed in model.
     * @param <A> appendable type.
     * @param <E> error type.
     * @param model a model assumed never to be <code>null</code>.
     * @param a appendable to write to.
     * @param formatter formats variables before they are passed to the escaper
     * @param escaper used to write escaped variables
     * @throws E if an error occurs while writing to the appendable
     */
    protected <A extends io.jstach.jstachio.Output<E>, E extends Exception> void execute(
        com.digiunion.model.URL model, 
        A a, 
        io.jstach.jstachio.Formatter formatter,
        io.jstach.jstachio.Escaper escaper) throws E {
        render(model, a, formatter, escaper, templateAppender());
    }

    @Override
    public <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write(
        com.digiunion.model.URL model, 
        A outputStream) throws E {
        encode(model, outputStream, this.formatter, this.escaper, templateAppender());
        return outputStream;
    }

    @Override
    public <A extends io.jstach.jstachio.Output<E>, E extends Exception> A execute(
        com.digiunion.model.URL model, 
        io.jstach.jstachio.context.ContextNode context, 
        A a) throws E {
        render(this, model, context, a, this.formatter, this.escaper, templateAppender());
        return a;
    }

    @Override
    public <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write(
        com.digiunion.model.URL model, 
        io.jstach.jstachio.context.ContextNode context, 
        A outputStream) throws E {
        encode(this, model, context, outputStream, this.formatter, this.escaper, templateAppender());
        return outputStream;
    }

    @Override
    public boolean supportsType(Class<?> type) {
        return MODEL_CLASS.isAssignableFrom(type);
    }

    /**
     * Needed for jstachio runtime.
     * @hidden
     */
    @Override
    public java.util.List<io.jstach.jstachio.Template<?>> provideTemplates(io.jstach.jstachio.TemplateConfig templateConfig ) {
        return java.util.List.of(io.jstach.jstachio.TemplateConfig.empty() == templateConfig ? INSTANCE :  new URLRenderer(templateConfig));
    }

    @Override
    public String templatePath() {
        return TEMPLATE_PATH;
    }
    @Override
    public String templateName() {
        return TEMPLATE_NAME;
    }
    @Override
    public java.nio.charset.Charset templateCharset() {
        return TEMPLATE_CHARSET;
    }
    @Override
    public String templateMediaType() {
        return TEMPLATE_MEDIA_TYPE;
    }
    @Override
    public String templateString() {
        return TEMPLATE_STRING;
    }
    @Override
    public Class<?> templateContentType() {
        return io.jstach.jstachio.escapers.Html.class;
    }
    @Override
    public  io.jstach.jstachio.Escaper templateEscaper() {
        return this.escaper;
    }

    @Override
    public io.jstach.jstachio.Formatter templateFormatter() {
        return this.formatter;
    }

    /**
     * Appender.
     * @return appender for writing unescaped variables.
     */
    public io.jstach.jstachio.Appender templateAppender() {
        return io.jstach.jstachio.Appender.defaultAppender();
    }

    /**
     * Model class.
     * @return class used as model (annotated with JStache).
     */
    @Override
    public Class<?> modelClass() {
        return MODEL_CLASS;
    }

    /**
     * Needed for jstachio runtime.
     * @hidden
     */
    @Override
    public void process(Object model, io.jstach.jstachio.Output<?> appendable) throws java.lang.Exception {
        execute( (com.digiunion.model.URL) model, appendable);
    }

    /**
     * Needed for jstachio runtime.
     * @hidden
     */
    @Override
    public boolean isBroken(Object model) {
        return !supportsType(model.getClass());
    }

    /**
     * Renderer constructor using config.
     * @param templateConfig config that has collaborators
     */
    public URLRenderer(io.jstach.jstachio.TemplateConfig templateConfig) {
        this(templateConfig.formatter(), templateConfig.escaper());
    }

    /**
     * Convience static factory that will reuse the same singleton instance.
     * @return renderer same as calling no-arg constructor but is cached with singleton instance
     */
    public static URLRenderer of() {
        return INSTANCE;
    }

    /**
     * Renders the passed in model.
     * @param <A> appendable type.
     * @param <E> error type.
     * @param data model
     * @param unescapedWriter appendable to write to.
     * @param formatter formats variables before they are passed to the escaper.
     * @param escaper used to write escaped variables.
     * @param appender used to write unescaped variables.
     * @throws E if an error occurs while writing to the appendable
     */
    public static <A extends io.jstach.jstachio.Output<E>, E extends Exception> void render(
        com.digiunion.model.URL data, 
        A unescapedWriter,
        io.jstach.jstachio.Formatter formatter,
        io.jstach.jstachio.Appender escaper,
        io.jstach.jstachio.Appender appender) throws E {
        render(of(), data, io.jstach.jstachio.context.ContextNode.resolve(data,unescapedWriter), unescapedWriter, formatter, escaper, appender);
    }

    /**
     * Renders the passed in model.
     * @param <A> appendable type.
     * @param <E> error type.
     * @param template instance of template.
     * @param data model
     * @param context context
     * @param unescapedWriter appendable to write to.
     * @param formatter formats variables before they are passed to the escaper.
     * @param escaper used to write escaped variables.
     * @param appender used to write unescaped variables.
     * @throws E if an error occurs while writing to the appendable
     */
    protected static <A extends io.jstach.jstachio.Output<E>, E extends Exception> void render(
        com.digiunion.model.URLRenderer template, 
        com.digiunion.model.URL data, 
        io.jstach.jstachio.context.ContextNode context, 
        A unescapedWriter,
        io.jstach.jstachio.Formatter formatter,
        io.jstach.jstachio.Appender escaper,
        io.jstach.jstachio.Appender appender) throws E {

        unescapedWriter.append(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<header>\n" +
            "<title>Kivarino</title>\n" +
            "</header>\n" +
            "<body>\n" +
            "<button onclick=\"location.href='");

        // variable: url
        /* RenderingContext: class io.jstach.apt.internal.context.DeclaredTypeRenderingContext */
        /* TypeMirror: java.lang.String */
        formatter.format(escaper, unescapedWriter, "url", data.url());

        unescapedWriter.append(
            "'\" class=\"auth\">Authorize</button>\n" +
            "</body>\n" +
            "<style>\n" +
            "	:root {\n" +
            "		--text: #00e701;\n" +
            "		--background: #0b0e0f; \n" +
            "		--border: #474f54\n" +
            "	}\n" +
            "	body {\n" +
            "		background-color: var(--background);\n" +
            "	}\n" +
            "	.auth {\n" +
            "		font-size: 4.0rem;\n" +
            "		position: absolute;\n" +
            "		top: 50%;\n" +
            "		left: 50%;\n" +
            "		transform: translate(-50%, -50%);\n" +
            "    	font-family: 'Inter', sans-serif;		\n" +
            "		border-radius: unset;\n" +
            "		background-color: var(--background);\n" +
            "		border-color: var(--border);\n" +
            "		border-width: 0.15rem 0.15rem 0.15rem 0.15rem;\n" +
            "		color: var(--text);\n" +
            "		transition: color 100ms ease-out, border-width 100ms ease-out, -webkit-text-stroke-color 100ms ease-out;\n" +
            "		animation: strokeBackwards 100ms linear forwards\n" +
            "		-webkit-text-stroke-color: var(--text);\n" +
            "}\n" +
            "	.auth:hover {\n" +
            "		color: white;\n" +
            "		border-width: 0.25rem 0.5rem 0.25rem 0.5rem;\n" +
            "		animation: border-animation 100ms linear forwards;\n" +
            "	}\n" +
            "	.auth:active {\n" +
            "		border-width: 0.20rem 0.35rem 0.20rem 0.35rem;\n" +
            "	}\n" +
            "\n" +
            "@keyframes strokeBackwards {\n" +
            "  0%   { -webkit-text-stroke-width: 0.25rem; }\n" +
            "  5%   { -webkit-text-stroke-width: 0.245rem; }\n" +
            "  10%  { -webkit-text-stroke-width: 0.24rem; }\n" +
            "  15%  { -webkit-text-stroke-width: 0.235rem; }\n" +
            "  20%  { -webkit-text-stroke-width: 0.23rem; }\n" +
            "  25%  { -webkit-text-stroke-width: 0.225rem; }\n" +
            "  30%  { -webkit-text-stroke-width: 0.22rem; }\n" +
            "  35%  { -webkit-text-stroke-width: 0.215rem; }\n" +
            "  40%  { -webkit-text-stroke-width: 0.21rem; }\n" +
            "  45%  { -webkit-text-stroke-width: 0.205rem; }\n" +
            "  50%  { -webkit-text-stroke-width: 0.2rem; }\n" +
            "  55%  { -webkit-text-stroke-width: 0.195rem; }\n" +
            "  60%  { -webkit-text-stroke-width: 0.19rem; }\n" +
            "  65%  { -webkit-text-stroke-width: 0.185rem; }\n" +
            "  70%  { -webkit-text-stroke-width: 0.18rem; }\n" +
            "  75%  { -webkit-text-stroke-width: 0.175rem; }\n" +
            "  80%  { -webkit-text-stroke-width: 0.17rem; }\n" +
            "  85%  { -webkit-text-stroke-width: 0.165rem; }\n" +
            "  90%  { -webkit-text-stroke-width: 0.16rem; }\n" +
            "  95%  { -webkit-text-stroke-width: 0.155rem; }\n" +
            "  100% { -webkit-text-stroke-width: 0.15rem; }\n" +
            "	}\n" +
            "	@keyframes border-animation {\n" +
            "  0%   { -webkit-text-stroke-width: 0.15rem; }\n" +
            "  5%   { -webkit-text-stroke-width: 0.155rem; }\n" +
            "  10%  { -webkit-text-stroke-width: 0.16rem; }\n" +
            "  15%  { -webkit-text-stroke-width: 0.165rem; }\n" +
            "  20%  { -webkit-text-stroke-width: 0.17rem; }\n" +
            "  25%  { -webkit-text-stroke-width: 0.175rem; }\n" +
            "  30%  { -webkit-text-stroke-width: 0.18rem; }\n" +
            "  35%  { -webkit-text-stroke-width: 0.185rem; }\n" +
            "  40%  { -webkit-text-stroke-width: 0.19rem; }\n" +
            "  45%  { -webkit-text-stroke-width: 0.195rem; }\n" +
            "  50%  { -webkit-text-stroke-width: 0.2rem; }\n" +
            "  55%  { -webkit-text-stroke-width: 0.205rem; }\n" +
            "  60%  { -webkit-text-stroke-width: 0.21rem; }\n" +
            "  65%  { -webkit-text-stroke-width: 0.215rem; }\n" +
            "  70%  { -webkit-text-stroke-width: 0.22rem; }\n" +
            "  75%  { -webkit-text-stroke-width: 0.225rem; }\n" +
            "  80%  { -webkit-text-stroke-width: 0.23rem; }\n" +
            "  85%  { -webkit-text-stroke-width: 0.235rem; }\n" +
            "  90%  { -webkit-text-stroke-width: 0.24rem; }\n" +
            "  95%  { -webkit-text-stroke-width: 0.245rem; }\n" +
            "  100% { -webkit-text-stroke-width: 0.25rem; }</style>\n" +
            "\n" +
            "</html>\n" +
            "\n");


    }

    /**
     * Renders to an OutputStream use pre-encoded parts of the template.
     * @param <A> output type.
     * @param <E> error type.
     * @param data model
     * @param unescapedWriter stream to write to.
     * @param formatter formats variables before they are passed to the escaper.
     * @param escaper used to write escaped variables.
     * @param appender used to write unescaped variables.
     * @throws E if an error occurs while writing to the appendable
     */
    protected static <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> void encode(
        com.digiunion.model.URL data, 
        A unescapedWriter,
        io.jstach.jstachio.Formatter formatter,
        io.jstach.jstachio.Escaper escaper,
        io.jstach.jstachio.Appender appender) throws E {
        encode(of(), data, io.jstach.jstachio.context.ContextNode.resolve(data,unescapedWriter), unescapedWriter, formatter, escaper, appender);
    }

    /**
     * Renders to an OutputStream use pre-encoded parts of the template.
     * @param <A> output type.
     * @param <E> error type.
     * @param template instance of template.
     * @param data model
     * @param context context
     * @param unescapedWriter stream to write to.
     * @param formatter formats variables before they are passed to the escaper.
     * @param escaper used to write escaped variables.
     * @param appender used to write unescaped variables.
     * @throws E if an error occurs while writing to the appendable
     */
    protected static <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> void encode(
        com.digiunion.model.URLRenderer template, 
        com.digiunion.model.URL data, 
        io.jstach.jstachio.context.ContextNode context, 
        A unescapedWriter,
        io.jstach.jstachio.Formatter formatter,
        io.jstach.jstachio.Escaper escaper,
        io.jstach.jstachio.Appender appender) throws E {

        unescapedWriter.write(TEXT_0);

        // variable: url
        /* RenderingContext: class io.jstach.apt.internal.context.DeclaredTypeRenderingContext */
        /* TypeMirror: java.lang.String */
        formatter.format(escaper, unescapedWriter, "url", data.url());

        unescapedWriter.write(TEXT_1);


    }
    private static final byte[] TEXT_0 = (
    "<!DOCTYPE html>\n" +
    "<html>\n" +
    "<header>\n" +
    "<title>Kivarino</title>\n" +
    "</header>\n" +
    "<body>\n" +
    "<button onclick=\"location.href='").getBytes(TEMPLATE_CHARSET);
    private static final byte[] TEXT_1 = (
    "'\" class=\"auth\">Authorize</button>\n" +
    "</body>\n" +
    "<style>\n" +
    "	:root {\n" +
    "		--text: #00e701;\n" +
    "		--background: #0b0e0f; \n" +
    "		--border: #474f54\n" +
    "	}\n" +
    "	body {\n" +
    "		background-color: var(--background);\n" +
    "	}\n" +
    "	.auth {\n" +
    "		font-size: 4.0rem;\n" +
    "		position: absolute;\n" +
    "		top: 50%;\n" +
    "		left: 50%;\n" +
    "		transform: translate(-50%, -50%);\n" +
    "    	font-family: 'Inter', sans-serif;		\n" +
    "		border-radius: unset;\n" +
    "		background-color: var(--background);\n" +
    "		border-color: var(--border);\n" +
    "		border-width: 0.15rem 0.15rem 0.15rem 0.15rem;\n" +
    "		color: var(--text);\n" +
    "		transition: color 100ms ease-out, border-width 100ms ease-out, -webkit-text-stroke-color 100ms ease-out;\n" +
    "		animation: strokeBackwards 100ms linear forwards\n" +
    "		-webkit-text-stroke-color: var(--text);\n" +
    "}\n" +
    "	.auth:hover {\n" +
    "		color: white;\n" +
    "		border-width: 0.25rem 0.5rem 0.25rem 0.5rem;\n" +
    "		animation: border-animation 100ms linear forwards;\n" +
    "	}\n" +
    "	.auth:active {\n" +
    "		border-width: 0.20rem 0.35rem 0.20rem 0.35rem;\n" +
    "	}\n" +
    "\n" +
    "@keyframes strokeBackwards {\n" +
    "  0%   { -webkit-text-stroke-width: 0.25rem; }\n" +
    "  5%   { -webkit-text-stroke-width: 0.245rem; }\n" +
    "  10%  { -webkit-text-stroke-width: 0.24rem; }\n" +
    "  15%  { -webkit-text-stroke-width: 0.235rem; }\n" +
    "  20%  { -webkit-text-stroke-width: 0.23rem; }\n" +
    "  25%  { -webkit-text-stroke-width: 0.225rem; }\n" +
    "  30%  { -webkit-text-stroke-width: 0.22rem; }\n" +
    "  35%  { -webkit-text-stroke-width: 0.215rem; }\n" +
    "  40%  { -webkit-text-stroke-width: 0.21rem; }\n" +
    "  45%  { -webkit-text-stroke-width: 0.205rem; }\n" +
    "  50%  { -webkit-text-stroke-width: 0.2rem; }\n" +
    "  55%  { -webkit-text-stroke-width: 0.195rem; }\n" +
    "  60%  { -webkit-text-stroke-width: 0.19rem; }\n" +
    "  65%  { -webkit-text-stroke-width: 0.185rem; }\n" +
    "  70%  { -webkit-text-stroke-width: 0.18rem; }\n" +
    "  75%  { -webkit-text-stroke-width: 0.175rem; }\n" +
    "  80%  { -webkit-text-stroke-width: 0.17rem; }\n" +
    "  85%  { -webkit-text-stroke-width: 0.165rem; }\n" +
    "  90%  { -webkit-text-stroke-width: 0.16rem; }\n" +
    "  95%  { -webkit-text-stroke-width: 0.155rem; }\n" +
    "  100% { -webkit-text-stroke-width: 0.15rem; }\n" +
    "	}\n" +
    "	@keyframes border-animation {\n" +
    "  0%   { -webkit-text-stroke-width: 0.15rem; }\n" +
    "  5%   { -webkit-text-stroke-width: 0.155rem; }\n" +
    "  10%  { -webkit-text-stroke-width: 0.16rem; }\n" +
    "  15%  { -webkit-text-stroke-width: 0.165rem; }\n" +
    "  20%  { -webkit-text-stroke-width: 0.17rem; }\n" +
    "  25%  { -webkit-text-stroke-width: 0.175rem; }\n" +
    "  30%  { -webkit-text-stroke-width: 0.18rem; }\n" +
    "  35%  { -webkit-text-stroke-width: 0.185rem; }\n" +
    "  40%  { -webkit-text-stroke-width: 0.19rem; }\n" +
    "  45%  { -webkit-text-stroke-width: 0.195rem; }\n" +
    "  50%  { -webkit-text-stroke-width: 0.2rem; }\n" +
    "  55%  { -webkit-text-stroke-width: 0.205rem; }\n" +
    "  60%  { -webkit-text-stroke-width: 0.21rem; }\n" +
    "  65%  { -webkit-text-stroke-width: 0.215rem; }\n" +
    "  70%  { -webkit-text-stroke-width: 0.22rem; }\n" +
    "  75%  { -webkit-text-stroke-width: 0.225rem; }\n" +
    "  80%  { -webkit-text-stroke-width: 0.23rem; }\n" +
    "  85%  { -webkit-text-stroke-width: 0.235rem; }\n" +
    "  90%  { -webkit-text-stroke-width: 0.24rem; }\n" +
    "  95%  { -webkit-text-stroke-width: 0.245rem; }\n" +
    "  100% { -webkit-text-stroke-width: 0.25rem; }</style>\n" +
    "\n" +
    "</html>\n" +
    "\n").getBytes(TEMPLATE_CHARSET);
}
