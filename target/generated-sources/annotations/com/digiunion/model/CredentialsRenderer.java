package com.digiunion.model;

/**
 * Generated Renderer.
 */
// @javax.annotation.Generated("io.jstach.apt.GenerateRendererProcessor")
public class CredentialsRenderer implements io.jstach.jstachio.Template.EncodedTemplate<com.digiunion.model.Credentials>,
    io.jstach.jstachio.context.ContextTemplate<com.digiunion.model.Credentials>,
    io.jstach.jstachio.spi.TemplateProvider,
    io.jstach.jstachio.spi.JStachioFilter.FilterChain {
    /**
     * Template path.
     * @hidden
     */
    public static final String TEMPLATE_PATH = "template/token.mustache";

    /**
     * Inline template string copied.
     * @hidden
     */

    public static final String TEMPLATE_STRING = "";

    /**
     * Template name. Do not rely on this.
     * @hidden
     */
    public static final String TEMPLATE_NAME = "com.digiunion.model.CredentialsRenderer";

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
    public static final Class<?> MODEL_CLASS = com.digiunion.model.Credentials.class;

    /**
     * The instance. Use {@link {@link #of()} instead.
     * @hidden
     */
    private static final CredentialsRenderer INSTANCE = new CredentialsRenderer();

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
    public CredentialsRenderer(
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
    public CredentialsRenderer() {
        this(null, null);
    }

    @Override
    public StringBuilder execute(com.digiunion.model.Credentials model, StringBuilder sb) {
        render(model, io.jstach.jstachio.Output.of(sb), this.formatter, this.escaper, templateAppender());
        return sb;
    }

    @Override
    public <A extends io.jstach.jstachio.Output<E>, E extends Exception> A execute(
        com.digiunion.model.Credentials model, 
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
        com.digiunion.model.Credentials model, 
        A a, 
        io.jstach.jstachio.Formatter formatter,
        io.jstach.jstachio.Escaper escaper) throws E {
        render(model, a, formatter, escaper, templateAppender());
    }

    @Override
    public <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write(
        com.digiunion.model.Credentials model, 
        A outputStream) throws E {
        encode(model, outputStream, this.formatter, this.escaper, templateAppender());
        return outputStream;
    }

    @Override
    public <A extends io.jstach.jstachio.Output<E>, E extends Exception> A execute(
        com.digiunion.model.Credentials model, 
        io.jstach.jstachio.context.ContextNode context, 
        A a) throws E {
        render(this, model, context, a, this.formatter, this.escaper, templateAppender());
        return a;
    }

    @Override
    public <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write(
        com.digiunion.model.Credentials model, 
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
        return java.util.List.of(io.jstach.jstachio.TemplateConfig.empty() == templateConfig ? INSTANCE :  new CredentialsRenderer(templateConfig));
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
        execute( (com.digiunion.model.Credentials) model, appendable);
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
    public CredentialsRenderer(io.jstach.jstachio.TemplateConfig templateConfig) {
        this(templateConfig.formatter(), templateConfig.escaper());
    }

    /**
     * Convience static factory that will reuse the same singleton instance.
     * @return renderer same as calling no-arg constructor but is cached with singleton instance
     */
    public static CredentialsRenderer of() {
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
        com.digiunion.model.Credentials data, 
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
        com.digiunion.model.CredentialsRenderer template, 
        com.digiunion.model.Credentials data, 
        io.jstach.jstachio.context.ContextNode context, 
        A unescapedWriter,
        io.jstach.jstachio.Formatter formatter,
        io.jstach.jstachio.Appender escaper,
        io.jstach.jstachio.Appender appender) throws E {

        unescapedWriter.append(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <header></header>\n" +
            "  <body>\n" +
            "    <div class=\"box\"></div>\n" +
            "  </body>\n" +
            "  <style>\n" +
            "\n" +
            "    :root {\n" +
            "      --text: #00e701;\n" +
            "      --background: #0b0e0f; \n" +
            "      --border: #474f54\n" +
            "    }\n" +
            "    body {\n" +
            "      background-color: var(--background);\n" +
            "    }\n" +
            "\n" +
            "    .box {\n" +
            "      height: 25vw;\n" +
            "      width: 25vw;\n" +
            "      position: absolute;\n" +
            "      top: 50%;\n" +
            "      left: 50%;\n" +
            "      transform: translate(-50%, -50%);\n" +
            "      border: solid var(--border);\n" +
            "      border-radius: 50%;\n" +
            "      animation: 1s ease-in 0.5s infinite alternate loading;\n" +
            "    }\n" +
            "\n" +
            "    @Keyframes loading {\n" +
            "      from {\n" +
            "        height: 25vw;\n" +
            "        width: 25vw;\n" +
            "      }\n" +
            "\n" +
            "      to {\n" +
            "        height: 50vw;\n" +
            "        width: 50vw; \n" +
            "      }\n" +
            "    }\n" +
            "  </style>\n" +
            "</html>\n");


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
        com.digiunion.model.Credentials data, 
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
        com.digiunion.model.CredentialsRenderer template, 
        com.digiunion.model.Credentials data, 
        io.jstach.jstachio.context.ContextNode context, 
        A unescapedWriter,
        io.jstach.jstachio.Formatter formatter,
        io.jstach.jstachio.Escaper escaper,
        io.jstach.jstachio.Appender appender) throws E {

        unescapedWriter.write(TEXT_0);


    }
    private static final byte[] TEXT_0 = (
    "<!DOCTYPE html>\n" +
    "<html>\n" +
    "  <header></header>\n" +
    "  <body>\n" +
    "    <div class=\"box\"></div>\n" +
    "  </body>\n" +
    "  <style>\n" +
    "\n" +
    "    :root {\n" +
    "      --text: #00e701;\n" +
    "      --background: #0b0e0f; \n" +
    "      --border: #474f54\n" +
    "    }\n" +
    "    body {\n" +
    "      background-color: var(--background);\n" +
    "    }\n" +
    "\n" +
    "    .box {\n" +
    "      height: 25vw;\n" +
    "      width: 25vw;\n" +
    "      position: absolute;\n" +
    "      top: 50%;\n" +
    "      left: 50%;\n" +
    "      transform: translate(-50%, -50%);\n" +
    "      border: solid var(--border);\n" +
    "      border-radius: 50%;\n" +
    "      animation: 1s ease-in 0.5s infinite alternate loading;\n" +
    "    }\n" +
    "\n" +
    "    @Keyframes loading {\n" +
    "      from {\n" +
    "        height: 25vw;\n" +
    "        width: 25vw;\n" +
    "      }\n" +
    "\n" +
    "      to {\n" +
    "        height: 50vw;\n" +
    "        width: 50vw; \n" +
    "      }\n" +
    "    }\n" +
    "  </style>\n" +
    "</html>\n").getBytes(TEMPLATE_CHARSET);
}
