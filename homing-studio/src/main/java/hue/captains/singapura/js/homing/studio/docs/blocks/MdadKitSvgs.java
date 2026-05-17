package hue.captains.singapura.js.homing.studio.docs.blocks;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * SvgGroup for the {@code .mdad} kit doc — three diagrams that
 * together explain the typed-text vocabulary end to end:
 *
 * <ul>
 *   <li>{@link astShape}        — the {@code Block} / {@code Inline}
 *                                 sealed ADTs, drawn as a tree of sums
 *                                 with named variants.</li>
 *   <li>{@link parsePipeline}   — the parsing path: body text → block
 *                                 split → block classify → inline tokenize
 *                                 → typed AST.</li>
 *   <li>{@link renderFlow}      — the server-to-client round-trip: the
 *                                 server parses + serialises AST as JSON;
 *                                 the client walks JSON to DOM.</li>
 * </ul>
 *
 * <p>Per RFC 0017 every fill/stroke uses {@code var(--color-*)} tokens
 * with literal fallbacks, so all three diagrams inherit the active theme.</p>
 */
public record MdadKitSvgs() implements SvgGroup<MdadKitSvgs> {

    public record astShape()      implements SvgBeing<MdadKitSvgs> {}
    public record parsePipeline() implements SvgBeing<MdadKitSvgs> {}
    public record renderFlow()    implements SvgBeing<MdadKitSvgs> {}

    public static final MdadKitSvgs INSTANCE = new MdadKitSvgs();

    @Override
    public List<SvgBeing<MdadKitSvgs>> svgBeings() {
        return List.of(new astShape(), new parsePipeline(), new renderFlow());
    }

    @Override
    public ExportsOf<MdadKitSvgs> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
