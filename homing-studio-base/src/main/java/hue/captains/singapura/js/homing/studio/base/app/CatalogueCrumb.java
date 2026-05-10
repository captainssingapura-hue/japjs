package hue.captains.singapura.js.homing.studio.base.app;

/**
 * One breadcrumb in a {@link CatalogueData} chain. {@code href} null = current
 * page (rendered as plain text without a link).
 */
public record CatalogueCrumb(String text, String href) {}
