package hue.captains.singapura.js.homing.studio.base.tracker;

/** A dependency on another phase: phaseId of the prerequisite, plus the why. */
public record Dependency(String phaseId, String reason) {}
