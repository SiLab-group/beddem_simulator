#!/usr/bin/env python3
"""Summarise the model's decision results as Markdown (table + text bar charts).

Reads ``output/decisions.csv`` (written by DummyReporter during a simulation
run) and prints GitHub-flavoured Markdown to stdout: an overview table plus a
per-trip Unicode bar chart of each mode's expected utility (EU, lower = better),
with the chosen mode highlighted. Text bars render inline in a PR comment and in
the workflow run summary, so no image hosting is needed.

Usage:  python analysis/summarize_decisions.py
"""
import csv
import os
import sys
from collections import defaultdict

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CSV = os.path.join(ROOT, "output", "decisions.csv")
LABEL = {"car": "Car", "train": "Train", "biking": "Bike",
         "walking": "Walk", "bus": "Bus", "tram": "Tram"}
BAR_WIDTH = 24   # max characters for the longest bar in a trip


def load():
    rows = list(csv.DictReader(open(CSV, newline="")))
    loc = {}
    # agent -> start_time -> {mode: (eu, chosen)}
    trips = defaultdict(lambda: defaultdict(dict))
    for r in rows:
        loc[r["agent"]] = r["location"]
        trips[r["agent"]][float(r["start_time"])][r["mode"]] = (
            float(r["eu"]), r["chosen"] == "1")
    return loc, trips


def table(loc, trips):
    out = ["| Agent | Location | Trip start | Modes considered (EU) | Chosen |",
           "|:-----:|:---------|:----------:|:----------------------|:------:|"]
    for a in sorted(trips, key=lambda x: int(x)):
        for t in sorted(trips[a]):
            modes = trips[a][t]
            chosen = next((m for m, (eu, c) in modes.items() if c), None)
            ranked = sorted(modes.items(), key=lambda kv: kv[1][0])
            cells = []
            for m, (eu, c) in ranked:
                name = LABEL.get(m, m)
                cells.append("**%s %.2f**" % (name, eu) if c
                             else "%s %.2f" % (name, eu))
            chosen_lbl = LABEL.get(chosen, chosen) if chosen else "—"
            out.append("| %s | %s | %g | %s | **%s** |"
                       % (a, loc[a], t, ", ".join(cells), chosen_lbl))
    return "\n".join(out)


def bars(loc, trips):
    out = []
    for a in sorted(trips, key=lambda x: int(x)):
        for t in sorted(trips[a]):
            modes = trips[a][t]
            ranked = sorted(modes.items(), key=lambda kv: kv[1][0])
            mx = max(eu for _, (eu, _) in ranked) or 1.0
            out.append("Agent %s (%s) — trip start %g" % (a, loc[a], t))
            for m, (eu, c) in ranked:
                fill = max(1, round(eu / mx * BAR_WIDTH))
                name = LABEL.get(m, m).ljust(5)
                out.append("  %s %s %.2f%s"
                           % (name, "█" * fill, eu, "  <- chosen" if c else ""))
            out.append("")
    return "```\n" + "\n".join(out).rstrip() + "\n```"


def main():
    if not os.path.exists(CSV):
        print("> **No decision output found.** `output/decisions.csv` was not "
              "produced by the run — nothing to summarise.")
        return
    loc, trips = load()
    if not trips:
        print("> **`output/decisions.csv` is empty** — nothing to summarise.")
        return

    print("## TIB decision results\n")
    print("Expected utility per mode (**lower = chosen**), from the batch "
          "run's `output/decisions.csv`.\n")
    print(table(loc, trips))
    print("\n### Decision charts (EU per mode, shorter = preferred)\n")
    print(bars(loc, trips))
    print("\n_A higher-resolution `tib_decisions.png` chart is attached to this "
          "workflow run under **Artifacts**._")


if __name__ == "__main__":
    main()
