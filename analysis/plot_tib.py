#!/usr/bin/env python3
"""Render the TIB decision for one agent's trip as a PNG.

Reads the scenario CSVs in ``data/`` and reproduces beddem_simulator's TIB
aggregation (see StandardDummyAgent / framework.agent.reasoning). For the chosen
agent and trip it plots:

  * the expected utility (EU) per feasible mode  (lower = better, lowest chosen)
  * the per-determinant profile per mode          (scaled 0-1, lower preferred)

Usage:  python analysis/plot_tib.py [agent_id] [trip_index]
Output: analysis/tib_decision.png

This is an analysis/visualisation helper; it mirrors the Java engine's maths but
is not the model itself.
"""
import csv
import os
import sys

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(ROOT, "data")

CAR, TRAIN, BIKE = "#2a78d6", "#1baf7a", "#eda100"
MODE_COLOR = {"car": CAR, "train": TRAIN, "biking": BIKE, "walking": "#e34948", "bus": "#eb6834", "tram": "#4a3aa7"}

# Cost-like penalty tables (lower = better), keyed on the mode name in vehicle.csv.
SOCIAL_NORM = {"train": 1, "bus": 1, "tram": 1, "car": 2, "walking": 1, "biking": 1}
EMISSIONS = {"train": 1, "bus": 2, "tram": 1, "car": 3, "walking": 0, "biking": 0}
DISCOMFORT = {"train": 1, "bus": 2, "tram": 1, "car": 0, "walking": 3, "biking": 2}
INCONVENIENCE = {"train": 1, "bus": 2, "tram": 1, "car": 0, "walking": 0, "biking": 0}


def read_csv(name):
    with open(os.path.join(DATA, name), newline="") as f:
        return list(csv.DictReader(f))


def load():
    vehicles = {r["vehicle_id"]: {"name": r["name"].lower(),
                                  "speed": float(r["speed_km_per_hr"]),
                                  "cost": float(r["cost_per_km"])}
                for r in read_csv("vehicle.csv")}
    name_to_id = {v["name"]: vid for vid, v in vehicles.items()}
    locations = {r["loc_id"]: {n: r.get(n, "0") for n in ("train", "bus", "tram")}
                 for r in read_csv("location.csv")}
    agents = {r["id"]: r for r in read_csv("agent.csv")}
    schedule = read_csv("schedule.0.csv")
    return vehicles, name_to_id, locations, agents, schedule


def feasible_options(agent, vehicles, name_to_id, locations, trip):
    dist, limit, fund = float(trip["km"]), float(trip["time_limit"]), float(agent["funding"])
    owned = set(agent["resources"].split(";"))
    loc = locations[agent["loc_id"]]
    avail = set(owned)
    for mode_name, vid in (("train", "1"), ("bus", "2"), ("tram", "3")):
        if loc.get(mode_name) == "1":
            avail.add(vid)
    opts = []
    for vid in avail:
        v = vehicles[vid]
        t = dist / v["speed"]
        c = dist * v["cost"]
        if t < limit and c <= fund:
            opts.append({"name": v["name"], "time": t, "cost": c, "owned": vid in owned})
    return opts


def leaf_values(opts, agent):
    """Raw cost-like value per determinant per option (lower = better)."""
    return {
        "time": {o["name"]: o["time"] for o in opts},
        "cost": {o["name"]: o["cost"] for o in opts},
        "norm": {o["name"]: SOCIAL_NORM.get(o["name"], 2) for o in opts},
        "role": {o["name"]: EMISSIONS.get(o["name"], 2) for o in opts},
        "self": {o["name"]: (0.0 if o["owned"] else 2.0) for o in opts},
        "emotion": {o["name"]: DISCOMFORT.get(o["name"], 2) for o in opts},
        "facilitating": {o["name"]: INCONVENIENCE.get(o["name"], 2) for o in opts},
        "freq": {o["name"]: 1.0 for o in opts},  # first trip: no habit yet
    }


def rank(values):
    """Normalise by the sum of DISTINCT values, as Determinant.rankOptions does."""
    s = sum(set(values.values())) or 1.0
    return {k: v / s for k, v in values.items()}


def aggregate(children, options):
    """Parent raw value per option = sum of rank(child) * child_weight."""
    acc = {o: 0.0 for o in options}
    for weight, child_raw in children:
        cn = rank(child_raw)
        for o in options:
            acc[o] += cn[o] * weight
    return acc


def tib_eu(leaf, weights, options):
    price_time = aggregate([(weights["time"], leaf["time"]), (weights["cost"], leaf["cost"])], options)
    evaluation = aggregate([(weights["evaluation"], price_time)], options)          # belief is null
    attitude = evaluation                                                            # attitude has only evaluation
    affect = aggregate([(weights["emotion"], leaf["emotion"])], options)
    social = aggregate([(weights["norm"], leaf["norm"]), (weights["role"], leaf["role"]),
                        (weights["self"], leaf["self"])], options)
    intention = aggregate([(weights["affect"], affect), (weights["social"], social),
                           (weights["attitude"], attitude)], options)
    habits = aggregate([(weights["freq"], leaf["freq"])], options)
    root = aggregate([(weights["intention"], intention), (weights["habit"], habits),
                      (weights["facilitating"], leaf["facilitating"])], options)
    return root


def weights_of(agent):
    return {
        "time": float(agent["time_weight"]), "cost": float(agent["cost_weight"]),
        "norm": float(agent["norm_weight"]), "role": float(agent["role_weight"]),
        "self": float(agent["self_weight"]), "emotion": float(agent["emotion_weight"]),
        "facilitating": float(agent["facilitating_weight"]), "freq": float(agent["frequency_weight"]),
        "attitude": float(agent["attitude_weight"]), "social": float(agent["social_weight"]),
        "affect": float(agent["affect_weight"]), "intention": float(agent["intention_weight"]),
        "habit": float(agent["habit_weight"]), "evaluation": 1.0,
    }


def main():
    agent_id = sys.argv[1] if len(sys.argv) > 1 else "1"
    trip_idx = int(sys.argv[2]) if len(sys.argv) > 2 else 0

    vehicles, name_to_id, locations, agents, schedule = load()
    agent = agents[agent_id]
    trips = [t for t in schedule if t["agent_id"] == agent_id]
    trip = trips[trip_idx]

    opts = feasible_options(agent, vehicles, name_to_id, locations, trip)
    options = [o["name"] for o in opts]
    leaf = leaf_values(opts, agent)
    weights = weights_of(agent)
    eu = tib_eu(leaf, weights, options)
    winner = min(eu, key=eu.get)

    order = sorted(options, key=lambda m: eu[m])
    dets = ["time", "cost", "norm", "role", "self", "emotion", "facilitating", "freq"]

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(11, 4.2))
    fig.suptitle(f"Agent {agent_id} ({locations[agent['loc_id']].get('name', agent['loc_id']) if 'name' in locations[agent['loc_id']] else agent['loc_id']}) "
                 f"— {int(float(trip['km']))} km trip", fontsize=13)

    ax1.barh(order[::-1], [eu[m] for m in order[::-1]], color=[MODE_COLOR.get(m, "#888") for m in order[::-1]])
    ax1.set_title("Expected utility (lower = chosen)", fontsize=11)
    for i, m in enumerate(order[::-1]):
        ax1.text(eu[m], i, f"  {eu[m]:.2f}" + ("  ← chosen" if m == winner else ""), va="center", fontsize=9)
    ax1.margins(x=0.25)

    x = range(len(dets))
    n = len(order)
    w = 0.8 / n
    for i, m in enumerate(order):
        col = [leaf[d][m] for d in dets]
        mx = [max(leaf[d].values()) or 1 for d in dets]
        norm = [c / mmx for c, mmx in zip(col, mx)]
        ax2.bar([xi + i * w for xi in x], norm, width=w, label=m, color=MODE_COLOR.get(m, "#888"))
    ax2.set_title("Determinant profile (scaled, lower = preferred)", fontsize=11)
    ax2.set_xticks([xi + w * (n - 1) / 2 for xi in x])
    ax2.set_xticklabels(dets, rotation=40, ha="right", fontsize=9)
    ax2.set_ylim(0, 1)
    ax2.legend(fontsize=9, frameon=False)

    fig.tight_layout(rect=[0, 0, 1, 0.94])
    out = os.path.join(os.path.dirname(os.path.abspath(__file__)), f"tib_decision_agent{agent_id}.png")
    fig.savefig(out, dpi=130)
    print(f"Wrote {out}: options {options}, EU {{" + ", ".join(f'{m}:{eu[m]:.2f}' for m in order) + f"}}, chosen {winner}")


if __name__ == "__main__":
    main()
