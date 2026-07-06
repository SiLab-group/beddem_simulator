#!/usr/bin/env python3
"""Plot the model's decision results.

Reads ``output/decisions.csv`` (written by DummyReporter during a simulation
run) and plots the expected utility (EU) of each mode per trip, with the chosen
mode highlighted. It does NOT recompute the decision -- it visualises what the
model produced.

Usage:  python analysis/plot_tib.py [agent_id]
Output: analysis/tib_decision_agent<id>.png  (one agent), or
        analysis/tib_decisions.png            (all agents, no arg)

Generate the input first by running the simulation (see README) so that
output/decisions.csv exists.
"""
import csv
import os
import sys
from collections import defaultdict

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CSV = os.path.join(ROOT, "output", "decisions.csv")
OUTDIR = os.path.dirname(os.path.abspath(__file__))
COLOR = {"car": "#2a78d6", "train": "#1baf7a", "biking": "#eda100",
         "walking": "#e34948", "bus": "#eb6834", "tram": "#4a3aa7"}
LABEL = {"car": "Car", "train": "Train", "biking": "Bike", "walking": "Walk", "bus": "Bus", "tram": "Tram"}


def load():
    """agent -> location, and agent -> first-trip {mode: (eu, chosen)}."""
    if not os.path.exists(CSV):
        print("No " + CSV + " yet -- run the simulation first (see README). Nothing to plot.")
        sys.exit(0)
    rows = list(csv.DictReader(open(CSV, newline="")))
    loc = {}
    trips = defaultdict(lambda: defaultdict(dict))   # agent -> start_time -> mode -> (eu, chosen)
    for r in rows:
        loc[r["agent"]] = r["location"]
        trips[r["agent"]][float(r["start_time"])][r["mode"]] = (float(r["eu"]), r["chosen"] == "1")
    first = {a: t[min(t)] for a, t in trips.items()}   # each agent's earliest trip
    return loc, first


def draw(ax, agent, location, modes):
    order = sorted(modes, key=lambda m: modes[m][0])
    ax.barh(range(len(order)), [modes[m][0] for m in order][::-1],
            color=[COLOR.get(m, "#888") for m in order][::-1])
    ax.set_yticks(range(len(order)))
    ax.set_yticklabels([LABEL.get(m, m) for m in order[::-1]], fontsize=9)
    for i, m in enumerate(order[::-1]):
        eu, chosen = modes[m]
        ax.text(eu, i, "  %.2f" % eu + ("  ← chosen" if chosen else ""), va="center", fontsize=8)
    ax.set_title("Agent %s (%s)" % (agent, location), fontsize=11)
    ax.margins(x=0.3)
    ax.set_xlabel("expected utility (lower = chosen)", fontsize=8)


def main():
    loc, first = load()
    if len(sys.argv) > 1:
        a = sys.argv[1]
        if a not in first:
            print("Agent %s not in %s" % (a, CSV)); sys.exit(0)
        fig, ax = plt.subplots(figsize=(6, 3))
        draw(ax, a, loc[a], first[a])
        out = os.path.join(OUTDIR, "tib_decision_agent%s.png" % a)
    else:
        ags = sorted(first, key=lambda x: int(x))
        n = len(ags)
        fig, axes = plt.subplots((n + 1) // 2, 2, figsize=(11, 2.6 * ((n + 1) // 2)))
        axes = axes.ravel()
        for i, a in enumerate(ags):
            draw(axes[i], a, loc[a], first[a])
        for j in range(n, len(axes)):
            axes[j].axis("off")
        out = os.path.join(OUTDIR, "tib_decisions.png")
    fig.tight_layout()
    fig.savefig(out, dpi=130)
    print("Wrote " + out)


if __name__ == "__main__":
    main()
