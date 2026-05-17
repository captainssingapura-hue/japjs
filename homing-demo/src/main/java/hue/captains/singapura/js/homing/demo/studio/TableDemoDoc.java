package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.studio.base.table.TableData;
import hue.captains.singapura.js.homing.studio.base.table.TableData.Align;
import hue.captains.singapura.js.homing.studio.base.table.TableData.Badge;
import hue.captains.singapura.js.homing.studio.base.table.TableData.Cell;
import hue.captains.singapura.js.homing.studio.base.table.TableDoc;

import java.util.List;

/**
 * RFC 0020 demo — a {@link TableDoc} showing the Typed Content
 * Vocabulary phase rollout, using cell badges (success / warning /
 * error) and alignment to exercise the slim feature set end-to-end.
 *
 * <p>Wired into {@link DemoStudio} as a catalogue leaf; opens in the
 * framework's registered {@code TableViewer}.</p>
 */
public final class TableDemoDoc {

    private TableDemoDoc() {}

    public static final TableDoc INSTANCE = build();

    private static TableDoc build() {
        var headers = List.of(
                Cell.of("Phase"),
                Cell.of("Scope"),
                Cell.of("Status"),
                Cell.of("Notes")
        );

        var rows = List.<List<Cell>>of(
                List.of(
                        Cell.of("Phase 1"),
                        Cell.of("ComposedDoc PoC (markdown + SVG segments)"),
                        Cell.badged("DONE", Badge.SUCCESS),
                        Cell.of("Shipped 0.0.103-pre")
                ),
                List.of(
                        Cell.of("Phase 2"),
                        Cell.of("Visual Asset Docs (TableDoc + ImageDoc)"),
                        Cell.badged("IN PROGRESS", Badge.WARNING),
                        Cell.of("This very table is the proof.")
                ),
                List.of(
                        Cell.of("Phase 3"),
                        Cell.of("Extend Segment ADT (TableSegment + ImageSegment)"),
                        Cell.badged("PLANNED", Badge.WARNING),
                        Cell.of("Depends on Phase 2.")
                ),
                List.of(
                        Cell.of("Phase 4"),
                        Cell.of(".mdad conformance scanner"),
                        Cell.badged("PLANNED", Badge.WARNING),
                        Cell.of("Locks the no-HTML discipline in CI.")
                ),
                List.of(
                        Cell.of("Phase 5"),
                        Cell.of("Self-proof case study"),
                        Cell.badged("PLANNED", Badge.WARNING),
                        Cell.of("\"Why We Ditched HTML\" — written in the vocabulary.")
                ),
                List.of(
                        Cell.of("—"),
                        Cell.of("Aggregate"),
                        new Cell("40%", 1, 1, null, Align.RIGHT),
                        Cell.of("2 of 5 phases shipped.")
                )
        );

        return new TableDoc(
                TableDoc.deterministicUuid("demo:phase-status"),
                "Typed Content Vocabulary — Phase Status",
                "RFC 0020 demo. Slim table with colour-coded status badges and right-aligned percentage cell.",
                new TableData(headers, rows));
    }
}
