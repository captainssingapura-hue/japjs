package hue.captains.singapura.japjs.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Generates the typed {@code params} const for an {@link AppModule}'s compiled JS.
 *
 * <p>For any AppModule whose {@link AppModule#paramsType()} is non-Void, the
 * writer emits a frozen {@code params} const at the top of the module's JS,
 * populated from {@code window.location.search} with type-appropriate coercion
 * per record component.</p>
 *
 * <p>Type coercion table:</p>
 *
 * <table>
 *   <caption>Java component type → JS coercion</caption>
 *   <tr><th>Java</th><th>JS coercion</th></tr>
 *   <tr><td>String</td><td>{@code sp.get(name) || ""}</td></tr>
 *   <tr><td>int / long / short / byte</td><td>{@code parseInt(...) | 0}</td></tr>
 *   <tr><td>Integer / Long / Short / Byte</td><td>{@code parseInt(...)} or null</td></tr>
 *   <tr><td>double / float</td><td>{@code parseFloat(...) | 0}</td></tr>
 *   <tr><td>Double / Float</td><td>{@code parseFloat(...)} or null</td></tr>
 *   <tr><td>boolean / Boolean</td><td>{@code sp.get(name) === "true"}</td></tr>
 *   <tr><td>Optional&lt;T&gt;</td><td>undefined when absent; coerced T when present</td></tr>
 *   <tr><td>List&lt;T&gt;</td><td>{@code sp.getAll(name).map(coerce)}</td></tr>
 *   <tr><td>Enum</td><td>validated against allowed values; null on mismatch</td></tr>
 * </table>
 *
 * <p>Reserved component names (collide with kernel URL params):
 * {@code app}, {@code theme}, {@code locale}. Rejected at write time.</p>
 *
 * <p>Nested record components are explicitly forbidden in v1 (see RFC §9.2).</p>
 *
 * <p>Introduced in RFC 0001 Step 06.</p>
 */
public final class ParamsWriter {

    private static final Set<String> RESERVED = Set.of("app", "theme", "locale");

    private final Class<?> paramsType;

    public ParamsWriter(Class<?> paramsType) {
        this.paramsType = paramsType;
    }

    public List<String> write() {
        if (paramsType == null || paramsType == Void.class || paramsType == void.class) {
            return List.of();
        }
        if (!paramsType.isRecord()) {
            throw new IllegalStateException(
                    "paramsType must be a record class or Void.class; got: " + paramsType.getName());
        }

        var components = paramsType.getRecordComponents();

        // Reserved-key collision check
        for (var rc : components) {
            if (RESERVED.contains(rc.getName())) {
                throw new IllegalStateException(
                        "Params component name '" + rc.getName() + "' collides with reserved URL key. "
                      + "Reserved keys: " + RESERVED);
            }
        }

        boolean needsEnumHelper = false;
        for (var rc : components) {
            if (containsEnum(rc)) { needsEnumHelper = true; break; }
        }

        List<String> out = new ArrayList<>();
        out.add("// === japjs generated params (RFC 0001) ===");
        out.add("const params = (function() {");
        out.add("    var sp = new URLSearchParams(window.location.search);");
        if (needsEnumHelper) {
            out.add("    function _enum(v, allowed) { return allowed.indexOf(v) >= 0 ? v : null; }");
        }
        out.add("    return Object.freeze({");
        for (int i = 0; i < components.length; i++) {
            var rc = components[i];
            String comma = (i < components.length - 1) ? "," : "";
            String coerce = coerceFor(rc);
            out.add("        " + rc.getName() + ": " + coerce + comma);
        }
        out.add("    });");
        out.add("})();");
        out.add("// === end japjs generated params ===");
        return out;
    }

    // ---- Coercion expressions per component type --------------------------

    private String coerceFor(RecordComponent rc) {
        Class<?> raw = rc.getType();
        String name = rc.getName();
        String key = jstr(name);

        if (raw == String.class) {
            return "(sp.get(" + key + ") || \"\")";
        }
        if (raw == int.class || raw == long.class || raw == short.class || raw == byte.class) {
            return "(function(){ var v = sp.get(" + key + "); return v == null ? 0 : parseInt(v, 10); })()";
        }
        if (raw == Integer.class || raw == Long.class || raw == Short.class || raw == Byte.class) {
            return "(function(){ var v = sp.get(" + key + "); return v == null ? null : parseInt(v, 10); })()";
        }
        if (raw == double.class || raw == float.class) {
            return "(function(){ var v = sp.get(" + key + "); return v == null ? 0 : parseFloat(v); })()";
        }
        if (raw == Double.class || raw == Float.class) {
            return "(function(){ var v = sp.get(" + key + "); return v == null ? null : parseFloat(v); })()";
        }
        if (raw == boolean.class || raw == Boolean.class) {
            return "(sp.get(" + key + ") === \"true\")";
        }
        if (raw.isEnum()) {
            String allowed = enumAllowedJs(raw);
            return "(function(){ var v = sp.get(" + key + "); return v == null ? null : _enum(v, " + allowed + "); })()";
        }
        if (raw == Optional.class) {
            Class<?> inner = innerOf(rc.getGenericType(), "Optional");
            String innerExpr = scalarCoerce(inner, "v");
            return "(function(){ var v = sp.get(" + key + "); return v == null ? undefined : " + innerExpr + "; })()";
        }
        if (raw == List.class) {
            Class<?> inner = innerOf(rc.getGenericType(), "List");
            String innerExpr = scalarCoerce(inner, "x");
            return "(sp.getAll(" + key + ") || []).map(function(x) { return " + innerExpr + "; })";
        }
        if (raw.isRecord()) {
            throw new IllegalStateException(
                    "Nested record params are not supported in v1 (component '" + name
                  + "' is type " + raw.getSimpleName() + "). See RFC §9.2.");
        }
        throw new IllegalStateException(
                "Unsupported param component type: " + raw.getName() + " (component '" + name + "')");
    }

    /** Coerce a single string value (already extracted from URLSearchParams) to the target type. */
    private String scalarCoerce(Class<?> raw, String var) {
        if (raw == String.class) return var;
        if (raw == int.class || raw == Integer.class
         || raw == long.class || raw == Long.class
         || raw == short.class || raw == Short.class
         || raw == byte.class || raw == Byte.class) {
            return "parseInt(" + var + ", 10)";
        }
        if (raw == double.class || raw == Double.class
         || raw == float.class || raw == Float.class) {
            return "parseFloat(" + var + ")";
        }
        if (raw == boolean.class || raw == Boolean.class) {
            return "(" + var + " === \"true\")";
        }
        if (raw.isEnum()) {
            return "_enum(" + var + ", " + enumAllowedJs(raw) + ")";
        }
        if (raw.isRecord()) {
            throw new IllegalStateException(
                    "Nested records inside Optional/List are not supported in v1 (got " + raw.getSimpleName() + ")");
        }
        // Fallback — pass through as string. Unsigned future types will fail loudly.
        throw new IllegalStateException("Unsupported scalar inner type: " + raw.getName());
    }

    private static Class<?> innerOf(Type genericType, String containerName) {
        if (!(genericType instanceof ParameterizedType pt)) {
            throw new IllegalStateException(
                    containerName + " component must be parameterized (e.g. " + containerName + "<String>); "
                  + "got raw " + containerName);
        }
        Type[] args = pt.getActualTypeArguments();
        if (args.length != 1) {
            throw new IllegalStateException(
                    containerName + " must have exactly one type argument; got " + args.length);
        }
        Type a = args[0];
        if (a instanceof Class<?> cls) return cls;
        if (a instanceof ParameterizedType pt2 && pt2.getRawType() instanceof Class<?> cls) return cls;
        throw new IllegalStateException(
                "Unsupported type argument in " + containerName + ": " + a);
    }

    private static boolean containsEnum(RecordComponent rc) {
        if (rc.getType().isEnum()) return true;
        if (rc.getType() == Optional.class || rc.getType() == List.class) {
            try {
                return innerOf(rc.getGenericType(), rc.getType().getSimpleName()).isEnum();
            } catch (RuntimeException ignored) {
                return false;
            }
        }
        return false;
    }

    private static String enumAllowedJs(Class<?> enumType) {
        Object[] values = enumType.getEnumConstants();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(jstr(((Enum<?>) values[i]).name()));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String jstr(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
