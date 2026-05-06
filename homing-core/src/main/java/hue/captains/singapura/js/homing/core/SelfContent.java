package hue.captains.singapura.js.homing.core;

import java.util.List;

/**
 * Marker for an {@link EsModule} that emits its own JS module body programmatically,
 * instead of relying on a hand-written {@code .js} resource file or an external
 * {@code ContentProvider}.
 *
 * <p>The framework's module-serving action dispatches to {@link #selfContent(ModuleNameResolver)}
 * when it encounters a module implementing this interface. The standard
 * {@code ExportWriter} adds the trailing {@code export {…}} block based on
 * {@link EsModule#exports()}.</p>
 *
 * <p>This is the lightweight alternative to a full {@code ContentProvider} class —
 * appropriate when body generation is straightforward data-projection from the
 * type itself (e.g., emitting one {@code const X = …} per declared item). For
 * generation that needs runtime state (themes, caches, etc.), a separate
 * {@code ContentProvider} is still the right fit.</p>
 */
public interface SelfContent {
    /** The JS body lines this module serves, given a name resolver for any cross-module imports. */
    List<String> selfContent(ModuleNameResolver nameResolver);
}
