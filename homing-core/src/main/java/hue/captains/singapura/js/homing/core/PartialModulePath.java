package hue.captains.singapura.js.homing.core;

/**
 * A partially-resolved module path that may accept theme and locale context.
 * <p>When {@code domAware} is true, {@link #withTheme} and {@link #withLocale}
 * append query parameters; otherwise they are no-ops. Each {@code with*} call
 * returns a new instance (or {@code this} when nothing changes), making the
 * record safe to chain.</p>
 */
public record PartialModulePath(String basePath, boolean domAware) {

    public PartialModulePath withTheme(String theme) {
        if (!domAware || theme == null) return this;
        return new PartialModulePath(basePath + "&theme=" + theme, domAware);
    }

    public PartialModulePath withLocale(String locale) {
        if (!domAware || locale == null) return this;
        return new PartialModulePath(basePath + "&locale=" + locale, domAware);
    }

    @Override
    public String toString() {
        return basePath;
    }
}
