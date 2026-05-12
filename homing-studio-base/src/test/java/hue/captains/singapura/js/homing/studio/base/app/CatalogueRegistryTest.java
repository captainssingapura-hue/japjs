package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CatalogueRegistry}'s boot-time validations after
 * RFC 0005-ext2 (typed catalogue levels + typed sub-catalogue / leaf split).
 *
 * <p>Cycle and multi-parent rejection tests from the original RFC 0005 suite
 * have been removed: the type system now makes them unrepresentable. The
 * depth-mismatch check is also gone — {@code subCatalogues()} is typed by
 * level, so a wrong-depth child is a compile error. See RFC 0005-ext2.</p>
 */
class CatalogueRegistryTest {

    // ----- Test fixtures: minimal Doc + typed-level Catalogue records -----

    private static final UUID DOC_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");

    private static final Doc TEST_DOC = new Doc() {
        @Override public UUID   uuid()    { return DOC_ID; }
        @Override public String title()   { return "Test Doc"; }
        @Override public String contents(){ return "# Test"; }
    };

    record RootCatalogue() implements L0_Catalogue {
        public static final RootCatalogue INSTANCE = new RootCatalogue();
        @Override public String name() { return "Root"; }
        @Override public List<L1_Catalogue<RootCatalogue>> subCatalogues() {
            return List.of(LeafCatalogue.INSTANCE);
        }
    }

    record LeafCatalogue() implements L1_Catalogue<RootCatalogue> {
        public static final LeafCatalogue INSTANCE = new LeafCatalogue();
        @Override public RootCatalogue parent() { return RootCatalogue.INSTANCE; }
        @Override public String name()         { return "Leaf"; }
        @Override public List<Entry> leaves()  { return List.of(Entry.of(TEST_DOC)); }
    }

    record BlankNameCatalogue() implements L0_Catalogue {
        @Override public String name() { return "  "; }
    }

    /** References LeafCatalogue but Leaf will be omitted from the registered list. */
    record OrphanReferencingCatalogue() implements L0_Catalogue {
        @Override public String name() { return "Orphan-Referencer"; }
        @Override public List<L1_Catalogue<OrphanReferencingCatalogue>> subCatalogues() {
            // Compile note: LeafCatalogue's parent type is RootCatalogue, not
            // OrphanReferencingCatalogue. We need a flexible bound to express
            // "any L1" for this test.
            //
            // Workaround: use the wildcard-default from Catalogue.subCatalogues()
            // by *not* narrowing here.
            return List.of();
        }
        @Override public List<Entry> leaves() { return List.of(); }
        // For the test we need an L1 that's typed-mismatched. Build it via a
        // helper below — see "orphanReferencingRoot" usage.
    }

    /** A non-L0 catalogue (deliberately) wired as the brand home-app to test
     *  the "home-app must be L0" check. */
    record NonL0AsHomeApp() implements L1_Catalogue<RootCatalogue> {
        public static final NonL0AsHomeApp INSTANCE = new NonL0AsHomeApp();
        @Override public RootCatalogue parent() { return RootCatalogue.INSTANCE; }
        @Override public String name() { return "Pretend-Home"; }
    }

    /** Declares parent() as RootCatalogue, but is inserted as a child of OtherRoot. */
    record StaleParentChild() implements L1_Catalogue<RootCatalogue> {
        public static final StaleParentChild INSTANCE = new StaleParentChild();
        @Override public RootCatalogue parent() { return RootCatalogue.INSTANCE; }
        @Override public String name() { return "Stale-Child"; }
    }

    record OtherRoot() implements L0_Catalogue {
        public static final OtherRoot INSTANCE = new OtherRoot();
        @Override public String name() { return "Other-Root"; }
        // We deliberately put a child whose parent() points elsewhere — to
        // exercise the parent-match runtime backstop. Java's typed wildcard
        // means the list element only needs to be `? extends L1_Catalogue<?>`.
        @Override public List<? extends L1_Catalogue<?>> subCatalogues() {
            return List.of(StaleParentChild.INSTANCE);
        }
    }

    /** L0 that references an unregistered L1 child via the wildcard escape hatch. */
    record OrphanRoot() implements L0_Catalogue {
        public static final OrphanRoot INSTANCE = new OrphanRoot();
        @Override public String name() { return "Orphan-Root"; }
        @Override public List<? extends L1_Catalogue<?>> subCatalogues() {
            return List.of(LeafCatalogue.INSTANCE);   // Leaf will be omitted from the registered list
        }
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
    void breadcrumbsForDoc_walksFromContainingCatalogueToRoot() {
        var brand = new StudioBrand("Test", RootCatalogue.class);
        var registry = new CatalogueRegistry(brand, docs,
                List.of(RootCatalogue.INSTANCE, LeafCatalogue.INSTANCE));

        var crumbs = registry.breadcrumbsForDoc(DOC_ID);
        assertEquals(2, crumbs.size());
        assertSame(RootCatalogue.INSTANCE, crumbs.get(0));
        assertSame(LeafCatalogue.INSTANCE, crumbs.get(1));
    }

    @Test
    void levelOf_returnsTypedLevel() {
        assertEquals(0, CatalogueRegistry.levelOf(RootCatalogue.INSTANCE));
        assertEquals(1, CatalogueRegistry.levelOf(LeafCatalogue.INSTANCE));
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
        var brand = new StudioBrand("Test", OrphanRoot.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        // OrphanRoot references LeafCatalogue (via wildcard) but Leaf isn't in the list.
                        List.of(OrphanRoot.INSTANCE)));
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
        record StrangerCatalogue(Doc d) implements L0_Catalogue {
            @Override public String name()        { return "Stranger-Cat"; }
            @Override public List<Entry> leaves() { return List.of(Entry.of(d)); }
        }
        var brand = new StudioBrand("Test", StrangerCatalogue.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        List.of(new StrangerCatalogue(strangerDoc))));
        assertTrue(ex.getMessage().contains("not in the DocRegistry"));
    }

    @Test
    void rejects_brandHomeApp_notRegistered() {
        var brand = new StudioBrand("Test", RootCatalogue.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        // RootCatalogue not in the list — brand references it but it's missing.
                        List.of()));
        assertTrue(ex.getMessage().contains("not in the registered catalogue list"));
    }

    @Test
    void rejects_brandHomeApp_notL0() {
        // The brand declares an L1 catalogue as home-app. Must be L0.
        var brand = new StudioBrand("Test", NonL0AsHomeApp.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        List.of(RootCatalogue.INSTANCE, NonL0AsHomeApp.INSTANCE)));
        assertTrue(ex.getMessage().contains("must be an L0_Catalogue"));
    }

    @Test
    void rejects_staleParentReference() {
        // StaleParentChild.parent() returns RootCatalogue.INSTANCE, but it's
        // nested under OtherRoot via the wildcard subCatalogues() escape hatch.
        // The parent-match runtime backstop (RFC 0005-ext2) must reject this.
        var brand = new StudioBrand("Test", OtherRoot.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        List.of(OtherRoot.INSTANCE,
                                RootCatalogue.INSTANCE,   // present so closure check passes
                                StaleParentChild.INSTANCE,
                                LeafCatalogue.INSTANCE)));
        assertTrue(ex.getMessage().contains("parent()")
                || ex.getMessage().contains("typed-level invariant"));
    }
}
