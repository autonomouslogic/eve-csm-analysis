# AGENTS.md

Guidance for AI coding agents working in this repository.

## What this project is

Analysis of EVE Online Council of Stellar Management (CSM) election results, published as a static MkDocs site.
A Java 21 CLI parses the official ballot files and audit logs for each election, computes statistics, and renders
Markdown pages and CSV/JSON data into `docs/`, which MkDocs (Material theme + Vega-Lite charts) turns into the site.

## Repository layout

```
csmNN/                    One directory per election (csm12 … csm20). Raw inputs, committed.
  votes.blt               Ballots in BLT format (see Meek's method PDF, section 3.1)
  auditLog.txt            Official audit log produced by CCP's WrightTalley.py
  WrightTalley.py         CCP's STV tally script (Python 2). Copied verbatim per election.
  csm.json                Hand-written metadata: CCP picks and news-post URLs
src/main/java/com/autonomouslogic/evecsmanalysis/
  Main.java               Entry point. Commands: `process`, `thymeleaf`. Discovers csm* dirs.
  Processor.java          `process`: parse inputs, run analysis, write build/csm/*.json
  CsmAnalyser.java        Totals, winners/eliminations per round, least significant rank
  LeastSignificantRankRunner.java  Re-runs WrightTalley.py in Docker on truncated ballots
  AnalysisRenderer.java   `thymeleaf`: render docs/*.md and docs/data/* from build JSON
  AlumniFactory.java      Builds the cross-election alumni table
  parser/                 Hand-written line parsers for .blt and auditLog.txt
  models/                 Lombok @Value/@Builder + Jackson models
  csv/                    CSV writers for docs/data/
src/main/resources/templates/  Thymeleaf TEXT-mode templates: csm.md, index.md, alumni.md
src/test/java/...         JUnit 5 parser tests, run against every csm* dir
docs/                     MkDocs source. Mostly GENERATED, see below.
build/csm/                Intermediate JSON (gitignored)
```

## Pipeline

1. `make process` (`./gradlew csmProcess`) — for every `csm*` dir: parse `votes.blt` and `auditLog.txt`, write
   `build/csm/csmNN-{votes,auditLog,analysis}.json`.
2. `make thymeleaf` (`./gradlew csmThymeleaf`) — read the analysis JSON plus `csm.json`, render:
   - `docs/csmNN.md`
   - `docs/index.md`, `docs/alumni.md`
   - `docs/data/csm-votes.csv`, `docs/data/csm-candidates.csv`, `docs/data/alumni.json`, and per election
     `docs/data/csmNN-rank-distribution.csv` and `docs/data/csmNN-jaccard.csv` (heatmap sources on `docs/csmNN.md`)
3. `make mkdocs-serve` / `make mkdocs-build` — needs `make init` first (creates `.venv`).

Generated files under `docs/` are committed. Never hand-edit them; change the template or Java code and re-run
`make thymeleaf`. The hand-written pages are the metric explanations linked from `docs/csmNN.md`:
`docs/least-significant-rank.md`, `docs/votes-at-election.md`, `docs/ballots-without-a-winner.md` and
`docs/candidate-statistics.md`.

## Commands

| Task | Command |
|---|---|
| Python venv for MkDocs | `make init` |
| Tests | `make test` |
| Lint (CI enforced) | `make lint` |
| Auto-format | `make format` |
| Full regenerate | `make process thymeleaf` |
| Local site | `make mkdocs-serve` |

Run `make format` before committing Java or Gradle changes; CI fails on `spotlessCheck`.

## Requirements and gotchas

- **Docker is required for `make process`.** The least-significant-rank calculation runs `WrightTalley.py` in a
  `python:2` container 9 times per election (ranks 2–10, in parallel on virtual threads), with a relative bind mount
  (`-v .:/data`). It reprocesses every election on each run, so it is slow. `make test` does not need Docker.
- **Every directory in the repo root whose name starts with `csm` is treated as an election**, and its suffix is
  parsed as an integer. Do not create other `csm*` directories (scratch, backups, etc.).
- **Each election dir must be complete** (`votes.blt`, `auditLog.txt`, `WrightTalley.py`, `csm.json`). The
  parameterised tests iterate all `csm*` dirs, so a half-added election breaks `make test` as well as `make process`.
- Candidate names in the audit log parser are matched by `[a-zA-Z0-9 '_-]+`. A candidate whose name contains other
  characters (dots, non-ASCII, etc.) will cause a parse failure; extend the regex in `AuditLogParser` and add a test.
- `BallotParser` only supports a single withdrawn candidate (see `@todo` in `parseWithdrawals`).
- EVE's `.blt` files do not terminate each ballot line with `0` (unlike the spec); the parser accounts for this.
- `WrightTalley.py` is CCP's script, kept byte-for-byte as published for each election (csm18/csm19 differ from the
  rest). Do not modify or "fix" it.
- The spelling "talley" is used throughout (code, logs, file names). Keep it consistent rather than renaming
  piecemeal.
- Thymeleaf templates use TEXT mode with inline syntax (`[(${...})]`, `[# th:each ...]`), not HTML attributes.
- Name matching between `csm.json` `ccpPicks` and audit-log winners is by exact string (winners are trimmed).

## Adding a new election

1. Create `csmNN/` with `votes.blt`, `auditLog.txt`, `WrightTalley.py` from CCP's published results, using these
   exact file names.
2. Create `csmNN/csm.json`:
   ```json
   {
     "ccpPicks": ["Name One", "Name Two"],
     "urls": {
       "CSM NN Candidate Announcements": "https://www.eveonline.com/news/view/...",
       "Here’s Your CSM NN!": "https://www.eveonline.com/news/view/..."
     }
   }
   ```
3. `make test` then `make process thymeleaf`.
4. Add `docs/csmNN.md` to git along with the regenerated `docs/` files.

## Conventions

### Code

- Java 21, Lombok (`@Value`, `@Builder(toBuilder = true)`, `@Jacksonized`, `@RequiredArgsConstructor`,
  `@Log4j2`, `@SneakyThrows`). Follow the existing style.
- Logging is Log4j2 (via Lombok `@Log4j2`) bridged to SLF4J.
- Formatting: Palantir Java Format, **indentation is tabs**, not spaces (Spotless). Gradle files use tabs too.
  Run `make format` before committing.
- Use imports, not fully-qualified class names inline (e.g. `FileUtils`, not `org.apache.commons.io.FileUtils`).
- Concurrency: prefer plain synchronous code on virtual threads (`util/VirtualThreads`) for new work. RxJava3 is
  still used in `LeastSignificantRankRunner`; don't spread it further. Use thread-safe collections for any state
  shared between parallel tasks.

### Tests

- Single test: `./gradlew test --tests "com.autonomouslogic.evecsmanalysis.parser.AuditLogParserTest"`.
- Do not add message strings to `assert*` calls (e.g. `assertEquals(a, b)`, not `assertEquals(a, b, "message")`).
- When fixing a bug: write the test first, verify it fails with the old behaviour, then implement the fix and
  verify the test passes. Do not start implementing until the failing test exists.

### Generated and third-party files

- Do not hand-edit generated output in `docs/` (see Pipeline) or anything under `build/`.
- Do not modify `csmNN/votes.blt`, `csmNN/auditLog.txt` or `csmNN/WrightTalley.py`; they are CCP's published data.

### Git

- Commits follow Conventional Commits (`feat:`, `fix:`, `chore(deps):`); PR titles are validated by CI.
  Use `feat: CSM NN` for a new election. PR numbers are appended to commit subjects on merge.
- Commit messages must be a **single line**, no body. **Never** add a `Co-Authored-By` trailer (or any co-author
  trailer).
- Dependencies are managed by Renovate; avoid manual version bumps unless needed.

### Keep this file current

Whenever you change anything this file documents (pipeline, commands, layout, gotchas, conventions), update the
relevant section in the same change so it never drifts from the code.

## CI

- `build.yml`: `make test` (Java 21) and `make init mkdocs-build` (Python 3.14).
- `format.yml`: `make lint`.
- `semantic-pr.yml`: PR title must be a Conventional Commit.
