# Expenses Tracker (Java Swing)

A small desktop application to track personal expenses, written in plain Java with
Swing. You can record expenses, give each one a category with a colour, sort and
edit the list, and see a running total. Everything lives in memory: there is no
database or file persistence, so the data resets every time you close the app.

The project is intentionally dependency-free (no Maven/Gradle) so the domain model
and the UI wiring are easy to read end to end.

## Requirements

- **JDK 17 or newer** (compiled and tested with JDK 26). The code uses switch
  expressions and pattern matching for `instanceof`.
- A desktop environment, since the main entry point is a Swing window.

## Running

Use the helper script, which compiles everything into `out/` and launches the GUI:

```bash
./run.sh
```

Or do the two steps manually:

```bash
javac -d out *.java
java -cp out App
```

The compiled classes land in `out/` (its `*.class` files are git-ignored).

There is also a console-only demo that exercises the domain classes without any UI:

```bash
java -cp out Main
```

Both `App` and `Main` seed a few demo categories and expenses at startup so the
window is not empty on first launch.

## What it does

- **Expense list** in a sortable `JTable` (click a column header to sort).
- **Create, edit and delete expenses** through a modal form
  (description, amount, category and date).
- **Manage categories** in a dedicated dialog: add, rename, recolour and delete.
- **Colour badges** for categories, both in the expense table and the category
  manager, with automatic black/white text chosen for contrast.
- **Running total** shown in the footer, recalculated after every change.
- **Currency and date formatting** (`$1,234.56`, `yyyy-MM-dd HH:mm`).

## Project layout

The sources live at the repository root. They fall into three groups.

### Domain (UI-agnostic)

| File | Responsibility |
|------|----------------|
| `Color.java` | Enum of the 12 available category colours (`RED`, `LIGHT_GRAY`, …). |
| `Category.java` | A named, coloured category with a `UUID`. `toString()` returns the name so it renders nicely in lists and combo boxes. |
| `Expense.java` | An amount, description, optional `Category`, a `LocalDateTime` and its formatted string, plus a `UUID`. |
| `ExpenseTracker.java` | Owns the `ArrayList<Expense>` and the CRUD operations, the total, and clearing a deleted category from expenses. |
| `CategoryTracker.java` | Owns the `ArrayList<Category>` and its CRUD operations. |

### UI adapter (Swing table models)

| File | Responsibility |
|------|----------------|
| `ExpenseTableModel.java` | `AbstractTableModel` over `ExpenseTracker`. Columns: Description, Amount, Category, Date. Routes add/update/remove to the tracker and fires the matching `fireTable…` events. |
| `CategoryTableModel.java` | `AbstractTableModel` over `CategoryTracker`. Columns: Category and a live count of how many expenses use it. |

### Swing presentation

| File | Responsibility |
|------|----------------|
| `App.java` | GUI entry point. Forces the locale to English, installs the system look-and-feel, seeds demo data and opens `MainWindow`. |
| `MainWindow.java` | The main `JFrame`: menu bar, toolbar, table, footer total, and all the add/edit/delete actions. |
| `ExpenseFormDialog.java` | Modal form to create/edit an expense. |
| `CategoryManagerDialog.java` | Modal manager listing categories with Add / Edit / Delete. |
| `CategoryFormDialog.java` | Modal form to create/edit a category (name + colour). |
| `AmountCellRenderer.java` | Right-aligns and formats the amount column. |
| `CategoryCellRenderer.java` | Draws the coloured category badge (or a grey "No category"). |
| `ColorCellRenderer.java` | Draws a colour swatch plus pretty name (e.g. `Light Gray`) in the category form's combo box. |
| `ColorUtil.java` | Bridges the domain `Color` enum to `java.awt.Color`, and picks a readable text colour via luminance. |

### Entry points and tooling

| File | Responsibility |
|------|----------------|
| `Main.java` | Console demo of the domain (no UI). |
| `run.sh` | Compiles to `out/` and launches `App`. |

## Architecture

The code is split into three layers, and the rule that keeps it clean is:
**the UI never touches the `ArrayList`s directly — it always goes through the
trackers, via the table models.**

```
   App / Main              (entry points)
        |
   MainWindow              (JFrame — menus, toolbar, footer)
        |
   ExpenseTableModel       (AbstractTableModel — adapts domain to the JTable)
        |
   ExpenseTracker          (domain — the ArrayList<Expense>)
        |
   Expense / Category / Color
```

`MainWindow` holds the `ExpenseTracker`, the `CategoryTracker` and one
`ExpenseTableModel`. The table is populated by the model, which reads each row
from the tracker. Mutating actions go model → tracker → `fireTable…`, so the
`JTable` repaints itself.

### Typical flow — adding an expense

1. The user clicks **Add**. `MainWindow.addExpense()` checks that at least one
   category exists.
2. An `ExpenseFormDialog` opens, reading `categoryTracker.getCategories()` for the
   dropdown and validating the input on save.
3. On confirm, `MainWindow` calls `tableModel.addExpense(...)`, which delegates to
   `ExpenseTracker` and fires `fireTableRowsInserted(...)`.
4. `MainWindow.refreshTotal()` asks the domain for `getTotal()` and updates the
   footer.

Editing and deleting follow the same pattern (the row index is translated with
`table.convertRowIndexToModel(...)` first, because the table is sortable).

### Category deletion cascades

`CategoryTracker.removeCategory(index, expenseTracker)` first asks the
`ExpenseTracker` to clear that category from any expense that used it, then removes
the category. Those expenses end up with a `null` category and render as
**No category** (grey). The category manager warns you beforehand when a category
is still in use, and `MainWindow` refreshes the expense table after the manager
closes so the change is visible immediately.

## Using the app

- **Add expense** — toolbar *Add*, menu *File → Add expense*, or `Ctrl/Cmd+N`.
- **Edit expense** — select a row and use the toolbar *Edit*, double-click the row,
  or `Ctrl/Cmd+E`.
- **Delete expense** — select a row and use the toolbar *Delete*, the `Delete` key
  while the table has focus, or `Ctrl/Cmd+Backspace`. A confirmation is shown.
- **Manage categories** — toolbar *Categories*, menu *Categories → Manage
  categories…*, or `Ctrl/Cmd+K`. In the dialog you can add, edit (double-click) and
  delete (Delete key) categories.
- **Exit** — *File → Exit* or `Ctrl/Cmd+Q`.

## Notes and limitations

- **In-memory only.** Nothing is persisted; closing the app discards all data. The
  demo data in `App.java` is recreated on each launch.
- **English UI.** `App` calls `Locale.setDefault(Locale.ENGLISH)` so the built-in
  Swing buttons (OK, Cancel, Yes/No, …) are stable regardless of the machine's
  locale. All user-facing strings are English literals.
- The date is stored both as a `LocalDateTime` and as a preformatted string
  (`yyyy-MM-dd HH:mm:ss`) on the `Expense`.
- There is no build tool and no test suite; compilation is `javac -d out *.java`.

## Documentation

The `docs/` folder contains PlantUML sources and rendered PNGs:

- `uml-class-diagram.puml` — class diagram of the domain and UI packages.
- `uml-secuencia-ui-dominio.puml` — sequence diagram showing how the UI calls the
  domain for each operation.

(Titles and labels in these diagrams are in Spanish.)
