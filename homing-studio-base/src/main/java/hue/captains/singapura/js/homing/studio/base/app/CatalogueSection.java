package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * One labelled section in a catalogue, rendering {@code tiles} either as
 * pills or as cards per {@link #tileStyle()}.
 */
public record CatalogueSection(String title, TileStyle tileStyle, List<CatalogueTile> tiles) {

    public enum TileStyle {
        /** Icon + label + desc launcher tile (StudioElements.Pill). */
        PILL,
        /** Title + summary + badge + open-link card (StudioElements.Card). */
        CARD
    }
}
