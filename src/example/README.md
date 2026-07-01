# Sion → Sierre worked example (TIB)

A minimal, self-contained worked example of Triandis' Theory of Interpersonal
Behaviour (TIB) running on beddem_simulator's own decision engine
(`framework.agent.reasoning.*`). It reproduces the running example from the
BedDeM paper (Table 1): an **18 km trip from Sion to Sierre** with three modes —
**car, train, bike**.

Unlike `StandardDummyAgent` (where only *time* and *cost* are implemented and
the other determinants are stubbed), every TIB determinant here has a real
value, so **all input weights influence the outcome**.

## Files

| File | Role |
|------|------|
| `SionMobilityOption.java` | one mobility alternative + its 8 determinant inputs |
| `SionTIBDecision.java` | TIB decision component wiring all determinants onto the engine |
| `SionToSierreExample.java` | builds the Table 1 scenario; has a `main()` and `euByService()` |
| `SionTask.java` | minimal task to satisfy the framework contract |
| `../../test/example/SionToSierreTest.java` | the JUnit 4 test |

## Cost semantics (lower = better)

Every determinant is expressed as a **cost**: a lower value means a more
preferred option (price in CHF, time in hours, and ranks 1 = best … 3 = worst
for norm/role/self/comfort). The engine normalises and weights these up the TIB
tree, and the agent chooses the option with the **lowest** aggregated expected
utility (EU). This matches beddem_simulator's own `pickBestOpt`, which selects
the minimum.

Expected EU (paper Table 1):

| Mode | EU (lower = better) |
|------|---------------------|
| **Car**  | **≈ 1.13**  ← chosen |
| Train | ≈ 3.07 |
| Bike  | ≈ 4.81 |

Ranking: **Car < Train < Bike**, so the agent picks the **car**.

## How to run the test

### Option A — standalone, no Repast (quickest)

The example depends only on `framework.*` + `example.*` and JUnit 4, so it
compiles without Repast/Eclipse. From the repository root:

```bash
# Point these at a working JDK and a JUnit 4 jar (+ hamcrest).
# JUnit 4.13.2 and hamcrest ship with the Repast/Eclipse plugins, e.g.:
#   org.junit_4.13.2.v20230809-1000.jar, org.hamcrest.core_2.2.0*.jar
JUNIT=/path/to/junit-4.13.2.jar
HAMCREST=/path/to/hamcrest-core.jar

mkdir -p out
# compile the engine, the example, and the test
javac -d out \
  $(find src/framework src/example -name '*.java') \
  -cp "$JUNIT:$HAMCREST" test/example/SionToSierreTest.java

# run the JUnit 4 test
java -cp "out:$JUNIT:$HAMCREST" org.junit.runner.JUnitCore example.SionToSierreTest

# or just print the EU table without JUnit:
java -cp out example.SionToSierreExample
```

Expected `main()` output:

```
=== Sion -> Sierre (18 km) | EU, lower = better ===
  Car    EU = 1.1268
  Train  EU = 3.0675
  Bike   EU = 4.8056
Chosen (lowest EU): Car
```

(EU values are shown to 4 dp; the test asserts them to ±0.05.)

### Option B — with ant / Repast (the CI pipeline)

The test is registered in `build.xml`'s `run-test` target, so it runs with the
existing pipeline. This path compiles the whole project, so it needs Repast
Simphony + Eclipse configured (see the top-level `README.md`):

```bash
ant build   "-DECLIPSE_HOME=/path/to/eclipse"
ant run-test "-DECLIPSE_HOME=/path/to/eclipse"
```

Results are written as JUnit XML under `junit/`.

### Option C — Eclipse

Right-click `test/example/SionToSierreTest.java` → **Run As → JUnit Test**.

## What the test checks

- all three modes are evaluated;
- the agent picks the option with the **lowest** EU (car);
- the ranking is **Car < Train < Bike**;
- the EU values match the paper's Table 1 (±0.05);
- all EU values are positive and finite.
