package hue.captains.singapura.js.homing.core;

/**
 * Resolves how a module's name is written in a JavaScript import statement.
 * <p>Returns a {@link PartialModulePath} that can be completed with
 * theme and locale context via {@code withTheme} / {@code withLocale}.</p>
 */
@FunctionalInterface
public interface ModuleNameResolver {
    PartialModulePath resolve(EsModule<?> module);
}
