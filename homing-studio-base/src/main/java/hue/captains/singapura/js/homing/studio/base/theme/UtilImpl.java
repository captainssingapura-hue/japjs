package hue.captains.singapura.js.homing.studio.base.theme;

import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.studio.base.css.Util;

/**
 * RFC 0002-ext1 Phase 06 — trivial impl for the {@link Util} CssGroup.
 *
 * <p>Util records carry their CSS bodies inline (RFC 0002-ext1 Phase 05),
 * so this impl has no per-class methods. It exists purely to satisfy the
 * {@code (group, theme.slug)} resolution contract in the registry: the
 * framework looks up an impl per `(Util, theme)` pair to know "yes, this
 * group is served." Empty {@code cssVariables()} / {@code semanticTokens()}
 * / {@code globalRules()} — the theme's `:root` cascade comes from the
 * other groups (notably `StudioStyles`) loaded alongside Util.</p>
 *
 * <p>Registered under {@link HomingDefault} as a placeholder. After Phase 09
 * (when `CssGroupImpl<CG, TH>` retires), this file goes away — Util
 * becomes a plain marker with no impl needed.</p>
 */
public record UtilImpl() implements CssGroupImpl<Util, HomingDefault> {

    public static final UtilImpl INSTANCE = new UtilImpl();

    @Override public Util          group() { return Util.INSTANCE; }
    @Override public HomingDefault theme() { return HomingDefault.INSTANCE; }
}
