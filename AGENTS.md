# AGENTS.md

<!--toc:start-->

- [AGENTS.md](#agentsmd)
  - [Commands](#commands)
  - [Architecture](#architecture)
  - [Gotchas](#gotchas)

<!--toc:end-->

Plain-Java Swing desktop app. No Maven/Gradle, no test suite, no CI. Sources are
all in the default package at the repo root; `docs/` holds PlantUML diagrams and
rendered PNGs. README is the user-facing overview.

## Commands

```bash
./run.sh                    # compile all *.java into out/ and launch the GUI (App)
javac -d out *.java         # compile only
java -cp out App            # GUI entry point (needs a desktop display)
java -cp out Main           # console demo; headless-safe smoke check
rm -rf out                  # clean build (javac does not prune out/)
```

- JDK 17+ required (local JDK 26; Dockerfile uses 25). Code relies on switch
  expressions and pattern matching for `instanceof`.
- There is no test suite. Verify changes with `rm -rf out && javac -d out *.java`,
  then `java -cp out Main`. Do not launch `App` in a headless environment.
- `out/` is git-ignored but not cleaned by `run.sh`/`javac`. Deleting a source
  file leaves its old `.class` behind (e.g. orphan `out/Window.class`), so use
  `rm -rf out` for a trustworthy build.
- New classes go at the root with no `package` declaration.

## Architecture

Layer rule: UI must not mutate the trackers' `ArrayList`s directly.

```
App / Main -> MainWindow -> ExpenseTableModel -> ExpenseTracker -> Expense / Category / Color
```

- `MainWindow` owns `ExpenseTracker`, `CategoryTracker`, one `ExpenseTableModel`.
  Add/edit/delete go model -> tracker -> `fireTable...`, which repaints the table.
- `CategoryTableModel` wraps `CategoryTracker` plus expense counts; category
  deletion is `CategoryTracker.removeCategory(index, expenseTracker)`, which
  clears matching expenses first. The misspelled method name
  `ExpenseTracker.removeCategoryOcurrences` is existing API; keep it or rename
  all callers.
- The expense `JTable` is auto-sorted. Convert view rows with
  `table.convertRowIndexToModel(...)` before any model/index mutation.

## Gotchas

- `Expense` keeps a preformatted `yyyy-MM-dd HH:mm:ss` string beside `rawDate`.
  Change the date through `setRawDate` so both stay in sync. The table formats
  dates separately as `yyyy-MM-dd HH:mm`.
- `ExpenseTracker.updateExpense` ignores `null` arguments, so it cannot clear a
  category. Only the category-deletion cascade sets a category to `null`
  (renders as "No category").
- `ExpenseFormDialog` rejects an empty description, non-positive amount, and a
  null category. New expenses require at least one existing category.
- `App` forces `Locale.ENGLISH` so built-in Swing button text is stable; all UI
  strings are English literals. Diagram titles/labels in `docs/` are Spanish.
- Docker image runs `App`, so it needs X11 (`DISPLAY` plus the X socket mount
  described in the `Dockerfile` comment).
