---
name: use-homing-skills
description: Use this skill when the user wants to consume `homing-skills` (the framework's skills bundle) from their own Maven project — to dump skills as SKILL.md files for Claude Code, or to boot the mini-studio to browse them. Triggers — "add homing-skills", "install homing-skills", "how do I use homing-skills", "dump claude skills from homing", "run homing skills studio". Skip if the user is contributing TO the homing-skills bundle (use the framework's own build instead).
---

# Use `homing-skills` from your project

`homing-skills` is a standard Maven library. Add it as a dependency, then run its main class via `mvn exec:java`. Two modes, same source of truth:

- **Dump** — writes every shipped SKILL.md to `.claude/skills/<slug>/SKILL.md`. Agent-facing.
- **Serve** — boots a small Homing studio that renders the same skills in a browser. Human-facing.

## Step 1 — add the dependency

In your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.github.captainssingapura-hue.homing.js</groupId>
    <artifactId>homing-skills</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Any project with a `pom.xml` works — your downstream studio, a stub empty project, anything. The artifact transitively pulls in `homing-studio-base` and everything the serve mode needs.

## Step 2 — run

### Dump SKILL.md files (agent mode)

```
mvn exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli \
              -Dexec.args="--dump"
```

Writes to `<project-root>/.claude/skills/<slug>/SKILL.md`. The path defaults to `./.claude/skills/` when a `.claude/` folder already exists in the project root; otherwise pass an explicit target: `-Dexec.args="--dump path/to/dir"`.

### Serve the mini-studio (human mode)

```
mvn exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli
```

Then open `http://localhost:8083/` in a browser. Pass `-Dexec.args="--port 9000"` to change the port. Ctrl+C to stop.

## That's it

No plugin configuration needed in your `pom.xml`. Maven resolves `exec:java` to `org.codehaus.mojo:exec-maven-plugin` automatically on first invocation.

If you find yourself running these often, you can save typing by adding `exec-maven-plugin` named executions (see exec-maven-plugin docs) — pure convenience, not required.

## CLI flags

| Flag | Effect |
|---|---|
| `--dump`         | Dump to `./.claude/skills/` (requires the `.claude/` directory to exist) |
| `--dump <dir>`   | Dump to an explicit directory |
| `--port <N>`     | Serve on port `<N>` (default 8083) |
| `--help`         | Print usage |
| *(no args)*      | Serve on default port |

## How this fits the doctrine

- **One source of truth** — the SKILL.md files live once on the classpath at `claude-skills/<slug>/SKILL.md`. The dump writes those bytes verbatim; the studio renders them. Both modes read the same path.
- **Both modes, both first-class** — same artifact, same main class, two args.

For the principles behind this: see the **Dual-Audience Skills** doctrine in the framework's main studio.
