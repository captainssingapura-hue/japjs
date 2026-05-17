package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.tao.ontology.ValueObject;

/**
 * RFC 0012 — the framework's default {@link RuntimeParams}. One field
 * (port); resource reader falls through to the interface default
 * ({@code ResourceReader.fromSystemProperty()}).
 */
public record DefaultRuntimeParams(int port) implements RuntimeParams, ValueObject {}
