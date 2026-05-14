package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CatalogueRegistry}'s boot-time validations after
 * RFC 0005-ext2 + RFC 0011 (CRTP catalogue self-type, typed entries).
 */
class CatalogueRegistryTest {

    private static final UUID DOC_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");

    private static final Doc TEST_DOC = new Doc() {
        @Override public UUID   uuid()    { return DOC_ID; }
        @Override public String title()   { return "Test Doc"; }
        @Override public String contents(){ return "# Test"; }
    };

    record RootCatalogue() implements L0_Catalogue<RootCatalogue> {
        public static final RootCatalogue INSTANCE = new RootCatalogue();
        @Override public String name() { return "Root"; }
        @Override public List<? extends L1_Catalogue<RootCatalogue, ?>> subCatalogues() {
            return List.of(LeafCatalogue.INSTANCE);
        }
    }

    record LeafCatalogue() implements L1_Catalogue<RootCatalogue, LeafCatalogue> {
        public static final LeafCatalogue INSTANCE = new LeafCatalogue();
        @Override public RootCatalogue parent() { return RootCatalogue.INSTANCE; }
        @Override public String name()         { return "Leaf"; }
        @Override public List<Entry<LeafCatalogue>> leaves() {
            return List.of(Entry.of(this, TEST_DOC));
        }
    }

    record BlankNameCatalogue() implements L0_Catalogue<BlankNameCatalogue> {
        @Override public String name() { return "  "; }
    }

    /** A non-L0 catalogue wired as the brand home-app to test the
     *  "home-app must be L0" check. */
    record NonL0AsHomeApp() implements L1_Catalogue<RootCatalogue, NonL0AsHomeApp> {
        public static final NonL0AsHomeApp INSTANCE = new NonL0AsHomeApp();
        @Override public RootCatalogue parent() { return RootCatalogue.INSTANCE; }
        @Override public String name() { return "Pretend-Home"; }
    }

    /** An L1 typed under OrphanRoot but never registered — used to drive the
     *  unregistered-sub-catalogue runtime check. */
    record UnregisteredChild() implements L1_Catalogue<OrphanRoot, UnregisteredChild> {
        public static final UnregisteredChild INSTANCE = new UnregisteredChild();
        @Override public OrphanRoot parent() { return OrphanRoot.INSTANCE; }
        @Override public String name() { return "Unregistered-Child"; }
    }

    /** L0 referencing an L1 child that's typed correctly but isn't in the
     *  registered list — the closure check should reject this at boot. */
    record OrphanRoot() implements L0_Catalogue<OrphanRoot> {
        public static final OrphanRoot INSTANCE = new OrphanRoot();
        @Override public String name() { return "Orphan-Root"; }
        @Override public List<? extends L1_Catalogue<OrphanRoot, ?>> subCatalogues() {
            return List.of(UnregisteredChild.INSTANCE);
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
        record StrangerCatalogue(Doc d) implements L0_Catalogue<StrangerCatalogue> {
            @Override public String name() { return "Stranger-Cat"; }
            @Override public List<Entry<StrangerCatalogue>> leaves() {
                return List.of(Entry.of(this, d));
            }
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
                        List.<Catalogue<?>>of()));
        assertTrue(ex.getMessage().contains("not in the registered catalogue list"));
    }

    @Test
    void rejects_brandHomeApp_notL0() {
        var brand = new StudioBrand("Test", NonL0AsHomeApp.class);
        var ex = assertThrows(IllegalStateException.class,
                () -> new CatalogueRegistry(brand, docs,
                        List.of(RootCatalogue.INSTANCE, NonL0AsHomeApp.INSTANCE)));
        assertTrue(ex.getMessage().contains("must be an L0_Catalogue"));
    }

    // RFC 0011 note: the previous "rejects_staleParentReference" test
    // (a child whose parent() returns a different L0 INSTANCE than its
    // container) is no longer expressible in this codebase — the CRTP
    // self-bound on L<N>_Catalogue forces every child in subCatalogues()
    // to be typed L<N+1>_Catalogue<ThisCatalogue, ?>, so the wildcard
    // escape hatch the old test relied on doesn't compile.
}
