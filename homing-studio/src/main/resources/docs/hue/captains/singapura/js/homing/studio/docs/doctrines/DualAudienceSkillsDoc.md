# Doctrine — Dual-Audience Skills

> **Skills are not only for agents. Every skill serves two readers — the agent that follows the SKILL.md to execute a task, and the human that reads it to understand the framework, audit a recipe, or evaluate whether to adopt it. One source of truth — a single Markdown file — flows to both modes: dumped as-is into `.claude/skills/` for agent consumption, and rendered with chrome (header, navigation, theme, cross-references) inside a studio for human browsing. If either mode silently rots — the agent CLI stops dumping a skill, or the human studio stops listing one — the skill is broken; both modes ship together or neither does. This is the meta-doctrine governing how the framework distributes its own help: see [First-User Discipline](#ref:first-user) for why.**

This doctrine exists because **a skill that only an agent can read is a hostage situation**: the framework's recipes become opaque to anyone who isn't running Claude Code. Conversely, a recipe that's only a wiki page is **invisible to the agents that should be following it**. Both audiences deserve first-class access to the same content.

---

## What this doctrine commits to

Five sentences, five commitments:

1. **Single source of truth.** Each skill is one Markdown file. There is no "agent version" and "human version" — both audiences read the same bytes.
2. **Two delivery modes, both first-class.** The CLI dumps SKILL.md as-is to a user-chosen location (agent-facing). A mini-studio renders the same files as typed [Docs](#ref:cc) with header, theme, and cross-reference resolution (human-facing).
3. **Distribution ships both modes together.** The skills artifact (a Maven library — consumed via `mvn exec:java` from any host project) supports `dump` (agent install) AND `serve` (human browse) from the same main class. Shipping one without the other is a doctrine violation.
4. **Cross-references work cross-mode.** A skill can link to other typed Docs via `[label](#ref:<name>)`. Agents see it as Markdown; humans see it resolved to a navigable page. Same anchor, two consumers.
5. **Skills are content, not code.** A skill never depends on a runtime, never executes anything, never imports framework Java types. The dual-audience commitment falls apart the moment a skill assumes one specific reader's environment.

---

## The two modes

### Agent mode — `dump`

```
java -jar homing-skills.jar dump --to <dir>
```

Writes every shipped `SKILL.md` verbatim into `<dir>/<skill-slug>/SKILL.md`. Default `<dir>` is `.claude/skills/` if the current directory has a `.claude/` folder, else the current directory. The dump is **lossless**: the bytes the agent reads are byte-identical to the bytes the human reads.

The agent then discovers the skill through Claude Code's normal skill-loading mechanism. From the agent's perspective, the skills came from the local filesystem; the runnable jar's role is purely distribution.

### Human mode — `serve`

```
java -jar homing-skills.jar serve [--port 8083]
```

Boots a small Homing studio dedicated to the skills bundle. Each skill appears as a typed Doc in a `SkillsCatalogue`; the [DocReader](#ref:cc) renders the markdown with a sidebar TOC, theme picker, and cross-references. Humans see exactly the content the agent will follow — and can preview a skill before committing to it.

The mini-studio uses the framework's standard chrome (`Header`, `Catalogue`, `DocReader`) — no special rendering for skills. They're just typed Docs with one extra hint: a "copy-as-CLI" snippet at the top showing the exact `dump` command for that skill.

---

## What this doctrine bans

- **No "agent-only" skills.** A SKILL.md file with content that only makes sense to an agent (e.g. raw tool-call sequences, opaque prompt fragments) violates the dual-audience commitment. Skills must read as recipes a human can follow with their hands.
- **No "human-only" docs masquerading as skills.** A doc that's *only* meant for browsing — a doctrine, an RFC, a retrospective — is a [Doc](#ref:cc), not a skill. The skills catalogue must list things both audiences would meaningfully consume.
- **No skill-bundle that ships only one mode.** If the runnable jar can `dump` but can't `serve`, or can `serve` but can't `dump`, it's incomplete and shouldn't ship. Build out both before tagging a release.
- **No tooling-coupled content.** A skill that references "click this button in IDE X" or "set this Claude Code-specific option" couples the recipe to one tooling stack. Reference behaviour, not interface; agents and humans both adapt to behaviour.
- **No silent format drift between modes.** If the studio renders the markdown differently (e.g. strips frontmatter, rewrites code fences), the agent and the human are no longer reading the same bytes — silently violates commitment 1.

---

## What this doctrine permits

- **Studio-side enrichment that doesn't alter the source.** The mini-studio can wrap each skill page in chrome (header, theme, sidebar TOC). It can prepend a "copy-as-CLI" hint, append a "see also" footer derived from `references()`. None of these touch the SKILL.md bytes the agent reads.
- **Per-skill metadata in YAML frontmatter.** A skill MAY declare `name`, `description`, `tags`, `slug` etc. in YAML frontmatter — both modes parse it (agents for invocation rules, studio for catalogue tile fields). The body below the frontmatter stays the agent's recipe.
- **Multiple skill bundles.** Downstream projects may ship their own runnable skills jar following the same doctrine. The framework's bundle is *one* example, not the only one.
- **Per-mode optimisation of the **chrome**.** Themes, catalogue layout, search — these are studio concerns. Agents don't care. Optimise freely on the studio side without touching the SKILL.md content.

---

## Why both modes matter

- **Discoverability for newcomers.** A human evaluating Homing for the first time browses the skills studio; reading three SKILL.md pages tells them more about the framework's intent than any reference doc would. "What can the framework help me do?" is answered by skills.
- **Audit-ability for reviewers.** Security teams, license auditors, code reviewers can see exactly what the agent will be told to do — without installing Claude Code, without unpacking a jar by hand, without spelunking GitHub. Open the studio, read the skill, done.
- **Drift detection.** When the human-facing rendering breaks (a skill stops listing, a cross-reference 404s, a code fence renders wrong), the framework gets a visible signal that something's off. An agent-only distribution model has no such signal.
- **Two-way feedback.** A human reading a skill can spot ambiguities ("step 3 is unclear") that an agent would silently work around or fail at. Human review of skills hardens them for the agent path.
- **Versioning + browseability of the catalogue.** A studio of skills can show "what's available in this version", "what changed since the last release", "which skills cross-reference which doctrines". A bag of `.md` files in `.claude/skills/` cannot.

---

## Where this doctrine doesn't apply

- **Personal scratch skills** — a developer's own `.claude/skills/local-thing/SKILL.md` that helps only them with their own project doesn't need a studio. It's not distributed; it's not shared. The doctrine governs *distributed* skills.
- **Internal-only operational runbooks** — if a recipe genuinely cannot be shown publicly (security-sensitive deploy steps, customer-specific procedures), it lives elsewhere and isn't distributed as a skill.
- **Tooling-specific documentation** — "how to configure Claude Code itself" is not a skill in the dual-audience sense; it's tool documentation. Lives in the tool's own docs.

---

## How to know we're following it

Concrete markers — the framework's own skills bundle is the first proof:

- `homing-skills` ships a runnable jar with **both** `dump` and `serve` subcommands.
- Each `SKILL.md` lives once on the classpath at `claude-skills/<slug>/SKILL.md`. Both modes read the same path.
- The mini-studio includes a "Skills" catalogue listing every shipped skill as a typed `Doc`. Each Doc's `resourcePath()` overrides to the same `claude-skills/<slug>/SKILL.md` path the CLI dumps.
- The framework's documentation (this doctrine, the README, the catalogue tile descriptions) refers to skills by their slug — agents and humans use the same identifier.

When a future feature lands and you can't point at "the studio's own use of this skill *as both modes*," the bundle isn't done.

---

## See also

- [First-User Discipline](#ref:first-user) — the meta-doctrine that says the framework dogfoods every primitive. Skills bundles dogfood the studio (skills are typed Docs) AND dogfood Claude Code (skills are SKILL.md files).
- [Catalogues as Containers](#ref:cc) — why skills appear as a typed `Catalogue` in the mini-studio.
- [Plans as Living Containers](#ref:pc) — peer doctrine; skills and plans are both *typed living content*, distinguished by purpose: a plan tracks work over time, a skill tells someone how to do something.
