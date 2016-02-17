## UNRELEASED

- `AbstractSearchPlayer` now prints number of tries too.
- `mutateMove` accepts the jump probability as argument.
- Add `mutateMove` as protected method in AbstractSearchPlayer.
- Add `initialMove` as protected method in AbstractSearchPlayer.

## v2.1

- Added declarations for tournament graphs.

## v2.0

- Major overhaul to interfaces due to massive changes in social-influence library.
- Convert to gradle.

## v1.6

- Added method `public Vertex getVertexFromIndex(int index)` that returns a vertex based on its index. Index is a deterministic, per-graph attribute in [0, N), indicating the rank of the ID of the specific vertex in the ordered ID list.
- Included compiled class files of student players.

## v1.5

- Fixed a bug on the generator of the TwoWheels graph.

## v1.4

- Renamed `SimpleCirclePlayer` to `SimpleCyclePlayer` and `AdvancedCirclePlayer` to `AdvancedCyclePlayer`.
- `SimpleCyclePlayer` has been updated. This player will begin with a random ID to avoid a situation where there is no ID 1 (like when there are multiple graphs in the same framework instance). Furthermore, it better handles rounding on the `period` and achieves theoretical optimal performance.
- `AdvancedCyclePlayer` has been updated. It better handles rounding on the `period` and achieves theoretical optimal performance similar to `SimpleCyclePlayer`.
- Distance maps are now calculated using the Floyd-Warshall algorithm, instead of multiple Dijkstra executions.
- `VertexPair` class has been merged to the main project.
- Increased verbosity and error reporting.

## v1.3

- Extra method `isInterrupted()` on `Player` class gets whether the game mechanism wants the player to terminate. More convenient that manually keeping track of time using `System.currentTimeMillis()`.

## v1.2

- Added extra players: `LocalSearchDistancePlayer`, `GreedyDistancePlayer`, `ComplementaryGreedyDistancePlayer`.

## v1.1

- Fields `numOfMoves`, `budget` and `execution` merged on object `d` in `Player` class of type `GraphDefinition`.
- Field `move` of `Player` was replaced by `movePtr` of type `MovePointer` with `set()` and `get()` methods.
- Graph type no longer a field but added method `getMeta()` on `Graph` returning a characteristic string for each type of graph.
- `verbose` field was removed and replaced by `tournament` field inside `GraphDefinition`.

## v1.0

- Initial release
