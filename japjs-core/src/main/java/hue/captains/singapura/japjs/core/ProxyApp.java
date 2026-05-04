package hue.captains.singapura.japjs.core;

/**
 * A typed declaration for an external destination — a URL the kernel does
 * not serve, but which other modules can construct typed links to.
 *
 * <p>Examples: a GitHub repo, a docs site, a vendor API endpoint, a {@code mailto:},
 * {@code tel:}, or {@code sms:} URL.</p>
 *
 * <p>A ProxyApp:</p>
 * <ul>
 *   <li>has no JS module, no CSS, no DOM rendering;</li>
 *   <li>is not served by the kernel — {@code /app?app=&lt;proxy-name&gt;} returns 404;</li>
 *   <li><i>is</i> registered alongside AppModules in the same {@code SimpleAppResolver}
 *       and discovered through the same transitive {@code AppLink<?>} import walk;</li>
 *   <li>is imported via the same {@link AppLink} mechanism, and appears in the
 *       generated {@code nav} object indistinguishably from internal apps.</li>
 * </ul>
 *
 * <p>Each ProxyApp declares an inner {@code public record link()} that other
 * modules import to gain a typed {@code nav.X(params)} entry pointing at this
 * destination. The outgoing URL is constructed from {@link #urlTemplate()}.</p>
 *
 * <p>Introduced in RFC 0001 Step 03.</p>
 *
 * @param <P> self-type
 */
public non-sealed interface ProxyApp<P extends ProxyApp<P>> extends Linkable {

    /**
     * URL template with {@code {name}} interpolation slots. Parsed at first
     * use against {@link #paramsType()} — invalid templates throw
     * {@link IllegalStateException} early.
     *
     * <p>Template DSL:</p>
     * <ul>
     *   <li>{@code {name}}            — required interpolation; the param must
     *       exist on the Params record and must NOT be {@code Optional}.</li>
     *   <li>{@code {name?}}           — optional interpolation; the param must
     *       exist on the Params record and MUST be {@code Optional}. Substitutes
     *       empty string when absent.</li>
     *   <li>{@code {name?:default}}   — same as {@code {name?}} but uses the
     *       literal default value when the optional is empty.</li>
     *   <li>any other text            — literal, copied verbatim.</li>
     * </ul>
     *
     * <p>All interpolated values are URL-encoded via {@code URLEncoder.encode(v, UTF-8)}
     * (the {@code application/x-www-form-urlencoded} flavor). For path segments
     * containing slashes that should remain unencoded, declare separate params
     * for each segment.</p>
     */
    String urlTemplate();

    /**
     * Default kebab-case derivation from the simple class name.
     * Override to lock the URL contract independently of the Java class name.
     */
    @Override
    default String simpleName() {
        return Linkable.defaultSimpleName(this.getClass());
    }

    /**
     * Java record describing this proxy's typed parameters, or {@code Void.class}
     * for a static URL with no interpolation. Default is {@code Void.class}.
     */
    @Override
    default Class<?> paramsType() {
        return Void.class;
    }
}
