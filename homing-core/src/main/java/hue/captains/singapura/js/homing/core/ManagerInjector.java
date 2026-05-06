package hue.captains.singapura.js.homing.core;

/**
 * A source {@link EsModule} that wants its runtime <i>manager</i> auto-imported
 * into any consumer module that imports from it.
 *
 * <p>When the framework's module-serving action serves a consumer whose
 * {@link ImportsFor} reaches a {@code ManagerInjector}, it prepends one
 * {@code import { <exportName> as <bindName> } from "<managerPath>"} line for
 * each distinct injector to the generated body. This is the generic
 * counterpart to the framework-specific {@code withCssManager} /
 * {@code withHrefManager} hooks — but driven by a marker interface so types
 * outside of {@code homing-server} (e.g., {@code DocGroup} in
 * {@code homing-studio-base}) can opt in without any compile-time coupling
 * back to the server module.</p>
 *
 * <p>Implementations are typically interfaces with default methods, so concrete
 * record-style modules pick up the wiring automatically.</p>
 */
public interface ManagerInjector {

    /** The manager EsModule whose {@code .js} should be imported into consumers. */
    EsModule<?> manager();

    /** The user-visible binding name in consumer code (e.g. {@code "docs"}, {@code "css"}). */
    String managerBindName();

    /** The runtime exported name from the manager's JS file (e.g. {@code "DocManagerInstance"}). */
    String managerExportName();
}
