#!/usr/bin/env python3
"""Generate SAMPLES.md, the samples list in each project README, and the gap matrix in the
root README, from samples.json.

Usage (from anywhere):  python scripts/generate-samples-md.py

SAMPLES.md is generated: edit samples.json and re-run this script instead of editing the
markdown. READMEs are hand-written except for the blocks between these markers, which this
script rewrites:

  <!-- BEGIN GENERATED SAMPLES LIST (project: <run.cwd>) --> ... <!-- END GENERATED SAMPLES LIST -->
  <!-- BEGIN GENERATED GAP MATRIX --> ... <!-- END GENERATED GAP MATRIX -->

scripts/validate-catalog.py fails when any of them drift from samples.json.
Needs only the Python 3.8+ standard library.
"""

import json
import os
import re
import sys
from collections import OrderedDict
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
CATALOG = ROOT / "samples.json"
SAMPLES_MD = ROOT / "SAMPLES.md"

# Same keys as PkiSuiteSamples' generator, so the two catalogs can be rendered together.
LANGUAGES = OrderedDict([
    ("dotnet", ".NET"),
    ("java", "Java"),
    ("nodejs", "Node.js"),
    ("php", "PHP"),
    ("python", "Python"),
    ("ruby", "Ruby"),
])

FRAMEWORKS = {
    "aspnet-mvc": "ASP.NET MVC",
    "aspnet-core-angular": "ASP.NET Core + Angular",
    "spring-boot": "Spring Boot",
    "express": "Express",
    "php": "PHP",
    "flask": "Flask",
    "django": "Django",
    "rails": "Rails",
    "console": "Console",
}

PRODUCTS = {
    "web-pki": "Web PKI",
    "rest-pki": "Rest PKI",
    "rest-pki-core": "Rest PKI Core",
    "pki-express": "PKI Express",
    "pki-sdk": "PKI SDK",
    "amplia": "Amplia",
    "cloudhub": "CloudHub",
    "signer": "Signer",
}

# Feature areas, in display order. scripts/validate-catalog.py rejects an `area` missing here.
AREAS = OrderedDict([
    ("documents", "Create documents"),
    ("flows", "Flows: participants, order and rules"),
    ("signature-formats", "Signature formats (CAdES, XAdES)"),
    ("signing", "Sign through the API"),
    ("embedded-signing", "Embedded signing"),
    ("tracking", "Track and list documents"),
    ("webhooks", "Webhooks"),
    ("downloads", "Download documents"),
    ("lifecycle", "Document lifecycle"),
    ("folders", "Folders"),
    ("billing", "Billing"),
])

# One row per project (`run.cwd`). scripts/validate-catalog.py rejects a `run.cwd` missing here.
PROJECTS = OrderedDict([
    ("dotnet/console", {
        "stack": ".NET Framework 4.8 console app, NuGet `packages.config`",
        "command": "Open `Console.sln` in Visual Studio and press F5",
        "pick": "dotnet/console/Console/Program.cs",
        "config": "dotnet/console/Console/Scenarios/Scenario.cs",
    }),
    ("dotnet/SkillFriendlySignatureApp/SkillFriendlySignatureApp", {
        "stack": ".NET 10 console tool for a coding agent (Windows), with PKI SDK",
        "command": "`dotnet run -- certs` (or `list`, `sign`)",
        "pick": None,
        "pick_note": "not a scenario project: one command per run",
        "config": "dotnet/SkillFriendlySignatureApp/SkillFriendlySignatureApp/appsettings.json.example",
    }),
    ("java/console", {
        "stack": "Java 8 console app, Gradle wrapper",
        "command": "`./gradlew run` (`gradlew.bat run` on Windows)",
        "pick": "java/console/src/main/java/com/lacunasoftware/signer/sample/App.java",
        "config": "java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/Scenario.java",
    }),
    ("nodejs", {
        "stack": "TypeScript scripts, run with ts-node",
        "command": "`npm install && npx ts-node scenarios/<scenario>.ts`",
        "pick": None,
        "config": "nodejs/scenarios/scenario.ts",
    }),
    ("php", {
        "stack": "PHP command-line script, Composer",
        "command": "`composer install && php index.php`",
        "pick": "php/index.php",
        "config": "php/Scenarios/Scenario.php",
    }),
])

STATUS_LEGEND = [
    ("current", "Recommended for new integrations."),
    ("legacy", "Works and is still supported, but prefer the `current` alternative for new code."),
    ("deprecated", "Kept for reference only. Do not copy into new code."),
]


def load_catalog():
    return json.loads(CATALOG.read_text(encoding="utf-8"))


def task_of(entry):
    return entry["id"][: -(len(entry["product"]) + 1)]


def area_heading(area):
    return AREAS.get(area) or (area or "other").replace("-", " ").capitalize()


def anchor(text):
    keep = "".join(c for c in text.lower() if c.isalnum() or c in " -")
    return keep.replace(" ", "-")


def languages_in(catalog):
    present = {e["language"] for e in catalog}
    return [lang for lang in LANGUAGES if lang in present]


def by_area(catalog):
    """{area: {id: [entries]}}, areas in AREAS order, tasks in catalog order."""
    groups = OrderedDict((a, OrderedDict()) for a in AREAS)
    for e in catalog:
        groups.setdefault(e.get("area"), OrderedDict()).setdefault(e["id"], []).append(e)
    return OrderedDict((a, ids) for a, ids in groups.items() if ids)


def code_cell(entries, link_text="code", prefix=""):
    links = [f"[{link_text}]({prefix}{e['path']})" for e in entries]
    return "<br>".join(links) if links else "—"


def doc_cell(entry):
    links = [f"[docs]({entry['docs_url']})"]
    if entry.get("demo_url"):
        links.append(f"[demo]({entry['demo_url']})")
    return " · ".join(links)


def render(catalog):
    langs = languages_in(catalog)
    groups = by_area(catalog)
    out = [
        "<!-- Generated by scripts/generate-samples-md.py from samples.json. Do not edit by hand. -->",
        "",
        "# Samples by task",
        "",
        "Every Lacuna Signer sample in this repository, grouped by feature area and task, with one "
        "column per language. Each link opens the scenario file that implements the task. The "
        "machine-readable version, with related files, run commands and docs links, is "
        "[samples.json](samples.json); its schema is "
        "[schema/samples.schema.json](schema/samples.schema.json).",
        "",
        "## Status legend",
        "",
        "| Status | Meaning |",
        "|---|---|",
    ]
    out += [f"| `{s}` | {m} |" for s, m in STATUS_LEGEND]
    out += [
        "",
        "## How to run each project",
        "",
        "Every project is a console program that runs one scenario per execution. Its base class "
        "reads the Signer instance URL and API key from the `SIGNER_ENDPOINT` and `SIGNER_API_KEY` "
        "environment variables and, when they are unset, uses the sandbox and a shared sample key. "
        "Read [AGENTS.md](AGENTS.md) before running one: each run creates real documents on that "
        "instance. The exception is the .NET Skill-friendly signature app, a command-line tool that "
        "signs the user's own pending documents: it targets DropSigner by default and reads its "
        "own configuration, with no shared key (see its README).",
        "",
        "| Project | Stack | Command | Pick the scenario in | Signer URL and API key |",
        "|---|---|---|---|---|",
    ]
    for cwd in OrderedDict((e["run"]["cwd"], None) for e in catalog):
        p = PROJECTS.get(cwd, {})
        pick = (f"[{Path(p['pick']).name}]({p['pick']})" if p.get("pick")
                else p.get("pick_note", "each scenario is its own script"))
        config = f"[{Path(p['config']).name}]({p['config']})" if p.get("config") else "—"
        out.append(f"| [`{cwd}`]({cwd}) | {p.get('stack', '—')} | {p.get('command', '—')} | {pick} | {config} |")

    out += ["", "## Areas", ""]
    out += [f"- [{area_heading(a)}](#{anchor(area_heading(a))}): {len(ids)} task{'s' if len(ids) > 1 else ''}"
            for a, ids in groups.items()]

    header = "| Task | Status | " + " | ".join(LANGUAGES[l] for l in langs) + " | Docs |"
    rule = "|---|---|" + "---|" * len(langs) + "---|"
    for area, ids in groups.items():
        out += ["", f"## {area_heading(area)}", "", header, rule]
        for sample_id, entries in ids.items():
            first = entries[0]
            statuses = sorted({e["status"] for e in entries})
            row = [f"**{first['title']}**<br>`{task_of(first)}`", " / ".join(f"`{s}`" for s in statuses)]
            for lang in langs:
                row.append(code_cell([e for e in entries if e["language"] == lang]))
            row.append(doc_cell(first))
            out.append("| " + " | ".join(row) + " |")
    out.append("")
    return "\n".join(out)


README_BEGIN = re.compile(r"<!-- BEGIN GENERATED SAMPLES LIST \(project: ([^)]+)\) -->")
README_END = "<!-- END GENERATED SAMPLES LIST -->"
MATRIX_BEGIN = "<!-- BEGIN GENERATED GAP MATRIX -->"
MATRIX_END = "<!-- END GENERATED GAP MATRIX -->"
SKIP_DIRS = {".git", "node_modules", "vendor", "bin", "obj", "packages", ".vs", "build", "dist"}


def readmes():
    """README.md files under ROOT, skipping dependencies, build output and nested checkouts."""
    for folder, dirs, files in os.walk(ROOT):
        # Skip hidden folders and nested checkouts (git worktrees, submodules): their READMEs
        # belong to another copy of the repository.
        dirs[:] = [d for d in dirs if d not in SKIP_DIRS and not d.startswith(".")
                   and not (Path(folder) / d / ".git").exists()]
        if "README.md" in files:
            yield Path(folder) / "README.md"


def render_project_list(catalog, cwd, readme_dir):
    entries = [e for e in catalog if e["run"]["cwd"] == cwd]
    show_command = len({e["run"]["command"] for e in entries}) > 1
    head = "| Sample | Area | Entry file | Status |" + (" Command |" if show_command else "")
    rows = [head, "|---|---|---|---|" + ("---|" if show_command else "")]
    for e in entries:
        link = os.path.relpath(ROOT / e["path"], readme_dir).replace(os.sep, "/")
        row = (f"| {e['title']}<br>`{task_of(e)}` | {area_heading(e.get('area'))} "
               f"| [{Path(e['path']).name}]({link}) | `{e['status']}` |")
        if show_command:
            row += f" `{e['run']['command']}` |"
        rows.append(row)
    return "\n".join(rows)


def render_gap_matrix(catalog, readme_dir):
    langs = languages_in(catalog)
    prefix = os.path.relpath(ROOT, readme_dir).replace(os.sep, "/")
    prefix = "" if prefix == "." else prefix + "/"
    rows = ["| Area | Task | " + " | ".join(LANGUAGES[l] for l in langs) + " |",
            "|---|---|" + ":-:|" * len(langs)]
    for area, ids in by_area(catalog).items():
        for sample_id, entries in ids.items():
            row = [area_heading(area), f"{entries[0]['title']}<br>`{task_of(entries[0])}`"]
            for lang in langs:
                row.append(code_cell([e for e in entries if e["language"] == lang], "✓", prefix))
            rows.append("| " + " | ".join(row) + " |")
    counts = {lang: len({e["id"] for e in catalog if e["language"] == lang}) for lang in langs}
    total = len({e["id"] for e in catalog})
    rows.append("| | **Tasks covered** | " + " | ".join(f"**{counts[l]} of {total}**" for l in langs) + " |")
    return "\n".join(rows)


def render_readme(catalog, path):
    out = path.read_text(encoding="utf-8").replace("\r\n", "\n")
    begin = README_BEGIN.search(out)
    if begin:
        end = out.index(README_END, begin.end())
        body = render_project_list(catalog, begin.group(1), path.parent)
        out = out[: begin.end()] + "\n" + body + "\n" + out[end:]
    if MATRIX_BEGIN in out:
        start = out.index(MATRIX_BEGIN) + len(MATRIX_BEGIN)
        end = out.index(MATRIX_END, start)
        out = out[:start] + "\n" + render_gap_matrix(catalog, path.parent) + "\n" + out[end:]
    return out


def generated_files(catalog):
    """Every file this script writes, as {path: expected content}."""
    files = {SAMPLES_MD: render(catalog)}
    for path in readmes():
        text = path.read_text(encoding="utf-8")
        if README_BEGIN.search(text) or MATRIX_BEGIN in text:
            files[path] = render_readme(catalog, path)
    return files


def main():
    for path, content in generated_files(load_catalog()).items():
        path.write_text(content, encoding="utf-8", newline="\n")
        print(f"wrote {path.relative_to(ROOT).as_posix()}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
