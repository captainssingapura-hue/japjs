---
name: homing-skills-bootstrap
description: Use this skill the moment an agent encounters a project that ships the Homing framework's skills bundle (look for `.claude/skills/homing-skills-bootstrap/` on disk or an `io.github.captainssingapura-hue.homing.js:homing-skills` Maven dependency in `pom.xml`). The skill orients the agent — explains the bundle's purpose, points at the index skill that lists every available skill, names the CLI command for re-dumping after framework updates, and gives heuristics for when to load which skill. **Load this FIRST** before any other Homing skill. Triggers — "homing-skills", "homing framework skills", "what skills are available here", "set up homing-skills", "I see a .claude/skills/ folder with homing in it". Skip if the project has no Homing dependency.
---

# Homing Skills — Bootstrap

You're reading this because a project on your filesystem ships the **Homing framework's skills bundle** at `<project>/.claude/skills/`. This doc orients you.

## What homing-skills is

A Maven artifact (`io.github.captainssingapura-hue.homing.js:homing-skills`) that ships **typed Java records wrapping `SKILL.md` files**. The framework dogfoods its own Doc model (RFC 0004, RFC 0005-ext2) — every skill is simultaneously:

- a classpath resource at `claude-skills/<slug>/SKILL.md`, dumpable to disk via a CLI
- a typed `Doc` record browsable by humans in a mini-studio web UI
- a sealed entry in `SkillsManifest.ALL` (grep-able, type-checked, never silently forgotten)

The Dual-Audience Skills doctrine (filed in `homing-studio`'s doctrines catalogue) explains the design — the same `.md` file serves agents *and* humans, single source of truth.

## How to discover what's available

**Read the index skill next**: `<project>/.claude/skills/homing-skills-index/SKILL.md`.

It carries a full table of contents — every shipped skill with its slug, one-line summary, and trigger phrases. The index is **generated from `SkillsManifest.ALL`** at dump time, so it can't drift from reality. Treat it as the catalog you route prompts against.

If the index is missing or stale, re-dump the bundle:

```bash
mvn -pl homing-skills exec:java \
    -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli \
    -Dexec.args="--dump .claude/skills"
```

Where `homing-skills` is the local Maven module name (or the artifact's `<artifactId>` if you cloned the framework). The CLI writes every `<slug>/SKILL.md` under the target, overwriting in place. Idempotent; safe to re-run.

## How to use this bundle

1. **Read the index.** It's a TOC, not a tutorial.
2. **Match the user's prompt against the trigger phrases** in each skill's YAML `description`. The framework's description fields are dense by design — they list explicit triggers and explicit skip conditions.
3. **Load the matched skill.** Read its full body before answering.
4. **Compose if needed.** Some skills cross-reference others — e.g., the migration skill assumes you understand the typed catalogue model documented elsewhere. Follow the trails.
5. **Don't re-derive what the framework already typed.** Skills carry the framework's discipline — Pure-Component Views, Owned References, Typed Catalogue Levels. If a skill prescribes a record shape, use it.

## Common scenarios

| User says… | Likely skill(s) |
|---|---|
| "Set up a new studio on homing-studio-base" | `create-homing-studio` |
| "Add a new theme to my studio" | `create-homing-theme` |
| "Add a custom card / status widget / list-row variant" | `create-homing-component` (MPA, compose-first) |
| "How do I install / use this skills bundle" | `use-homing-skills` |
| "Upgrade my studio from 0.0.11" | `migrate-from-0-0-11` (breaking-change recipe) |
| "I see homing in a project, what now" | this doc + `homing-skills-index` |

## Self-learning loop

After loading a skill, if you encounter terminology you don't fully understand (RFC numbers, doctrine names, primitives like `Cue` / `Layer` / `L<N>_Catalogue`), the homing-studio's running studio UI is the canonical reference. Boot it:

```bash
mvn -pl homing-studio exec:java \
    -Dexec.mainClass=hue.captains.singapura.js.homing.studio.StudioServer
```

Then browse `http://localhost:8080`. The doctrines, RFCs, and architecture docs are all there with full cross-references. The studio's `DocBrowser` flat index is searchable; the structured catalogue (Doctrines / RFCs / Journeys / Building Blocks / Releases) groups by theme.

The framework cross-references heavily — a skill that mentions "RFC 0011" expects you to be able to read that RFC if you need the deep version of the rationale.

## What this skill does NOT do

- It doesn't teach the framework itself — that's the studio's doctrines + RFCs.
- It doesn't replace specific-task skills — load `create-homing-studio` for studio bootstrapping, not this.
- It doesn't provide code. The other skills do.

This is the entry point. Load `homing-skills-index` next.
