package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CatalogueRegistry}'s four boot-time validations
 * (RFC 0005 §6.1).
 */
class CatalogueRegistryTest {

    // ----- Test fixtures: minimal Doc + Catalogue records -----

    private static final UUID DOC_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");

    private static final Doc TEST_DOC = new Doc() {
        @Override public UUID   uuid()    { return DOC_ID; }
        @Override public String title()   { return "Test Doc"; }
        @Override public String contents(){ return "# Test"; }
    };

    record LeafCatalogue() implements Catalogue {
        public static final LeafCatalogue INSTANCE = new LeafCatalogue();
        @Override public String name()         { return "Leaf"; }
        @Override public List<Entry> entries() { return List.of(Entry.of(TEST_DOC)); }
    }

    record RootCatalogue() implements Catalogue {
        public static final RootCatalogue INSTANCE = new RootCatalogue();
        @Override public String name()         { return "Root"; }
        @Override public List<Entry> entries() { return List.of(Entry.of(LeafCatalogue.INSTANCE)); }
    }

    record OrphanReferencingCatalogue() implements Catalogue {
        @Override public String name()         { return "Orphan-Referencer"; }
        @Override public List<Entry> entries() { return List.of(Entry.of(LeafCatalogue.INSTANCE)); }
        // References LeafCatalogue but LeafCatalogue won't be in the registered list.
    }

    record BlankNameCatalogue() implements Catalogue {
        @Override public String name()         { return "  "; }
        @Override public List<Entry> entries() { return List.of(); }
    }

    record CycleACatalogue() implements Catalogue {
        public static final CycleACatalogue INSTANCE = new CycleACatalogue();
        @Override public String name()         { return "Cycle-A"; }
        @Override public List<Entry> entries() { return List.of(Entry.of(CycleBCatalogue.INSTANCE)); }
    }

    record CycleBCatalogue() implements Catalogue {
        public static final CycleBCatalogue INSTANCE = new CycleBCatalogue();
        @Override public String name()         { return "Cycle-B"; }
        @Override public List<Entry> entries() { return List.of(Entry.of(CycleACatalogue.INSTANCE)); }
    }

    record DualParentACatalogue() implements Catalogue {
        public static final DualParentACatalogue INSTANCE = new DualParentACatalogue();
        @Override public String name()         { return "Dual-A"; }
        @Override public List<Entry> entries() { return List.of(Entry.of(LeafCatalogue.INSTANCE)); }
    }

    record DualParentBCatalogue() implements Catalogue {
        public static final DualParentBCatalogue INSTANCE = new DualParentBCatalogue();
        @Override public String name()         { return "Dual-B"; }
        @Override public List<Entry> entries() { return List.of(Entry.of(LeafCatalogue.INSTANCE)); }
    }

    // ----- Tests -----

    private final DocRegistry docs = new DocRegistry(List.of(TEST_DOC));

    @Test
    void valid_simpleTree_constructsCleanly() {
        var brand = new StudioBrand("Test Studio", RootCatalogue.class);
        var registry = new CatalogueRegistry(brand, docs,
                List.of(RootCatalogue.INSTANCE, LeafCatalogue.INSTANCE));

        assertEquals(2, registry.size());
        assertSame(RootCatalogue.INSTANCE, registry.resolve(RootCatalogue.class));
        assertNull(registry.parentOf(RootCatalogue.class));
        assertSame(RootCatalogue.INSTANCE, registry.parentOf(LeafCatalogue.class));
    }

    @Test
    void breadcrumbs_walkUpFromLeafToRoot() {
        var brand = new StudioBrand("Test", RootCatalogue.class);
        var registry = new CatalogueRegistry(brand, docs,
                List.of(RootCatalogue.INSTANCE, LeafCatalogue.INSTANCE));

        var crumbs = registry.breadcrumbs(LeafCatalogue.class);
        assertEquals(2, crumbs.size());
        assertSame(RootCatalogue.INSTANCE, crumbs.get(0));
        assertSame(LeafCatalogue.INSTANCE, crumbs.get(1));
    }

    @Test
    void rejects_blankName() {
        var brand = new StudioBrand("Test", RootCatalogue.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        List.of(RootCatalogue.INSTANCE, LeafCatalogue.INSTANCE, new BlankNameCatalogue())));
        assertTrue(ex.getMessage().contains("blank name"));
    }

    @Test
    void rejects_unregisteredSubCatalogue() {
        var brand = new StudioBrand("Test", OrphanReferencingCatalogue.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        // OrphanReferencingCatalogue references LeafCatalogue but Leaf isn't in the list.
                        List.of(new OrphanReferencingCatalogue())));
        assertTrue(ex.getMessage().contains("not in the registered catalogue list"));
    }

    @Test
    void rejects_unregisteredDoc() {
        UUID strangerId = UUID.randomUUID();
        Doc strangerDoc = new Doc() {
            @Override public UUID   uuid()    { return strangerId; }
            @Override public String title()   { return "Stranger"; }
            @Override public String contents(){ return "x"; }
        };
        record StrangerCatalogue(Doc d) implements Catalogue {
            @Override public String name()         { return "Stranger-Cat"; }
            @Override public List<Entry> entries() { return List.of(Entry.of(d)); }
        }
        var brand = new StudioBrand("Test", StrangerCatalogue.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        List.of(new StrangerCatalogue(strangerDoc))));
        assertTrue(ex.getMessage().contains("not in the DocRegistry"));
    }

    @Test
    void rejects_cycle() {
        var brand = new StudioBrand("Test", CycleACatalogue.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        List.of(CycleACatalogue.INSTANCE, CycleBCatalogue.INSTANCE)));
        assertTrue(ex.getMessage().toLowerCase().contains("cycle"));
    }

    @Test
    void rejects_multiParent() {
        var brand = new StudioBrand("Test", DualParentACatalogue.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        List.of(DualParentACatalogue.INSTANCE,
                                DualParentBCatalogue.INSTANCE,
                                LeafCatalogue.INSTANCE)));
        assertTrue(ex.getMessage().contains("multiple parents"));
    }

    @Test
    void rejects_brandHomeApp_notRegistered() {
        var brand = new StudioBrand("Test", RootCatalogue.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        // RootCatalogue not in the list — brand references it but it's missing.
                        List.of(LeafCatalogue.INSTANCE)));
        assertTrue(ex.getMessage().contains("not in the registered catalogue list"));
    }
}
