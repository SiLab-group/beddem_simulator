#!/usr/bin/env python3
"""Summarise the model's decision results as a Markdown table.

Reads ``output/decisions.csv`` (written by DummyReporter during a simulation
run) and prints a GitHub-flavoured Markdown table to stdout: one row per trip,
listing every mode's expected utility (EU, lower = better) with the chosen mode
highlighted. Intended to be appended to $GITHUB_STEP_SUMMARY in CI so the real
decision output shows up on the workflow run, but it also just works locally.

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


def main():
    if not os.path.exists(CSV):
        print("> **No decision output found.** `output/decisions.csv` was not "
              "produced by the run — nothing to summarise.")
        return

    rows = list(csv.DictReader(open(CSV, newline="")))
    if not rows:
        print("> **`output/decisions.csv` is empty** — nothing to summarise.")
        return

    loc = {}
    # agent -> start_time -> {mode: (eu, chosen)}
    trips = defaultdict(lambda: defaultdict(dict))
    for r in rows:
        loc[r["agent"]] = r["location"]
        trips[r["agent"]][float(r["start_time"])][r["mode"]] = (
            float(r["eu"]), r["chosen"] == "1")

    print("## TIB decision results\n")
    print("Expected utility per mode (**lower = chosen**), from the batch run's "
          "`output/decisions.csv`.\n")
    print("| Agent | Location | Trip start | Modes considered (EU) | Chosen |")
    print("|:-----:|:---------|:----------:|:----------------------|:------:|")

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
            print("| %s | %s | %g | %s | **%s** |"
                  % (a, loc[a], t, ", ".join(cells), chosen_lbl))

    print()


if __name__ == "__main__":
    main()
