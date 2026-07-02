# Sion → Sierre worked example (TIB)

A self-contained worked example of Triandis' Theory of Interpersonal Behaviour
(TIB) on beddem_simulator's own decision engine. It reproduces the BedDeM paper
(Table 1): an **18 km trip from Sion to Sierre** with three modes — car, train,
bike. Unlike `StandardDummyAgent` (only *time*/*cost* implemented), **every
determinant here has a real value, so all input weights matter**.

## Result

Every determinant is a **cost** (lower = better), and the agent picks the option
with the lowest aggregated expected utility (EU):

| Mode | EU (lower = better) |
|------|---------------------|
| **Car** | **≈ 1.13** ← chosen |
| Train | ≈ 3.07 |
| Bike | ≈ 4.81 |

## Run it

Depends only on `framework.*` + `example.*` (+ JUnit 4 for the test), so it runs
**without Repast/Eclipse**. From the repo root:

```bash
mkdir -p out
javac -d out $(find src/framework src/example -name '*.java')

# print the EU table
java -cp out example.SionToSierreExample

# or run the JUnit test (junit + hamcrest ship with the Repast/Eclipse plugins)
javac -d out -cp junit.jar:hamcrest.jar test/example/SionToSierreTest.java
java -cp out:junit.jar:hamcrest.jar org.junit.runner.JUnitCore example.SionToSierreTest
```

In CI the test runs via `build.xml`'s `run-test` target on pull requests to
`master`; in Eclipse, right-click the test → **Run As → JUnit Test**.

## Files

- `SionMobilityOption.java` — one mode + its 8 determinant inputs
- `SionTIBDecision.java` — wires all determinants onto the TIB engine
- `SionToSierreExample.java` — builds the scenario (`main()` + `euByService()`)
- `../../test/example/SionToSierreTest.java` — the JUnit 4 test
