package hue.captains.singapura.js.homing.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CssGroupResolverTest {

    // --- Test CssGroups with a diamond dependency graph ---
    // Base (no deps) <- Left <- Root
    //                <- Right <-/

    record Base() implements CssGroup<Base> {
        static final Base INSTANCE = new Base();
        @Override public CssImportsFor<Base> cssImports() { return CssImportsFor.none(this); }
        @Override public List<CssClass<Base>> cssClasses() { return List.of(); }
    }

    record Left() implements CssGroup<Left> {
        static final Left INSTANCE = new Left();
        @Override public CssImportsFor<Left> cssImports() {
            return new CssImportsFor<>(this, List.of(Base.INSTANCE));
        }
        @Override public List<CssClass<Left>> cssClasses() { return List.of(); }
    }

    record Right() implements CssGroup<Right> {
        static final Right INSTANCE = new Right();
        @Override public CssImportsFor<Right> cssImports() {
            return new CssImportsFor<>(this, List.of(Base.INSTANCE));
        }
        @Override public List<CssClass<Right>> cssClasses() { return List.of(); }
    }

    record Root() implements CssGroup<Root> {
        static final Root INSTANCE = new Root();
        @Override public CssImportsFor<Root> cssImports() {
            return new CssImportsFor<>(this, List.of(Left.INSTANCE, Right.INSTANCE));
        }
        @Override public List<CssClass<Root>> cssClasses() { return List.of(); }
    }

    @Test
    void resolve_emptyList() {
        var result = CssGroupResolver.resolve(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void resolve_singleNoDeps() {
        var result = CssGroupResolver.resolve(List.of(Base.INSTANCE));
        assertEquals(List.of(Base.INSTANCE), result);
    }

    @Test
    void resolve_transitiveDeps_dependenciesFirst() {
        var result = CssGroupResolver.resolve(List.of(Root.INSTANCE));

        assertEquals(4, result.size());
        // Base must come before Left and Right; Root must be last
        assertTrue(result.indexOf(Base.INSTANCE) < result.indexOf(Left.INSTANCE));
        assertTrue(result.indexOf(Base.INSTANCE) < result.indexOf(Right.INSTANCE));
        assertTrue(result.indexOf(Left.INSTANCE) < result.indexOf(Root.INSTANCE));
        assertTrue(result.indexOf(Right.INSTANCE) < result.indexOf(Root.INSTANCE));
        assertEquals(Root.INSTANCE, result.getLast());
    }

    @Test
    void resolve_diamondDedup() {
        // Base appears in both Left and Right's imports — should only appear once
        var result = CssGroupResolver.resolve(List.of(Root.INSTANCE));
        long baseCount = result.stream().filter(c -> c instanceof Base).count();
        assertEquals(1, baseCount);
    }

    @Test
    void resolve_multipleRoots_dedup() {
        var result = CssGroupResolver.resolve(List.of(Left.INSTANCE, Right.INSTANCE));

        // Base, Left, Right — Base shared but only appears once
        assertEquals(3, result.size());
        assertTrue(result.indexOf(Base.INSTANCE) < result.indexOf(Left.INSTANCE));
        assertTrue(result.indexOf(Base.INSTANCE) < result.indexOf(Right.INSTANCE));
    }
}
