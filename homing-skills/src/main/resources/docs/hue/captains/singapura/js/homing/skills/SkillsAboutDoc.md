# About this skills bundle

The `homing-skills` artifact is a small **Maven library** that ships a curated set of skills (markdown recipes) for working with the Homing framework. It exists because **skills are not only for agents** — humans should be able to browse the same content, evaluate it, audit it, and read it the same way an agent would.

Per the **Dual-Audience Skills** doctrine, this bundle ships two modes from a single source of truth. Both modes are invoked from a host Maven project via `mvn exec:java` — there's no fat-jar.

## Mode 1 — serve a mini-studio (default)

```
mvn exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli
```

Boots a small Homing studio on `http://localhost:8083` listing every skill as a typed `Doc` with rendered markdown, sidebar TOC, theme picker, and cross-references. This is what you're reading now.

## Mode 2 — dump skills as files

```
mvn exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli \
              -Dexec.args="--dump"
```

Writes every shipped `SKILL.md` verbatim into `<host-project>/.claude/skills/<slug>/SKILL.md`. The bytes the agent reads are byte-identical to the bytes you see rendered here.

After dumping, Claude Code (or any compatible skill-aware agent) running in the same workspace can invoke the skills automatically.

## Getting started

The first skill in the catalogue — **Use homing-skills from your project** — is the one-page recipe for consuming this bundle: one `<dependency>` block, one `mvn exec:java` command per mode. If you're new here, start there.

## What's included

The home page lists every skill in this bundle. Each entry links to the rendered markdown — the **same content** the CLI dumps to disk for agent use.

## Adding skills (for contributors)

If you're contributing back to `homing-skills`:

1. Add the `SKILL.md` under `.claude/skills/<slug>/SKILL.md` at the repo root.
2. Create a `<Name>SkillDoc.java` record under `homing-skills/.../skills/` — override `resourcePath()` to point at `claude-skills/<slug>/SKILL.md`.
3. Add the entry to `SkillsManifest.ALL`.

The Maven build copies the `.claude/skills/` tree into the jar at `claude-skills/`. Single source of truth — one `.md` file flows to both modes.

## See also

- The **Dual-Audience Skills** doctrine — the foundation
- The **First-User Discipline** doctrine — why the framework dogfoods its own primitives
- The main `homing-studio` (typically at `http://localhost:8080`) — the framework's full documentation hub
