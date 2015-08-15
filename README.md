# Social Influence Tournament

Tournament edition of [social-influence](https://github.com/gstamatelat/social-influence) for courses.

## Import

1. Download and extract the zip.
2. In the terminal, type `gradlew eclipse` if you are using Eclipse or `gradlew idea` if you are using IntelliJ. `java` (or `java.exe`) needs to be in your PATH for this step to work.
3. Open the directory as existing project in the IDE you specified in the previous step.

Alternatively, you may import as gradle project in the IDE of your choice, assuming gradle is supported or you have installed the appropriate plugin.

## Usage

The user must implement a player by deriving the `Player` abstract class using the following template.

```java
package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.game.Player;

public class MyPlayer extends Player {

	@Override
	public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
		// Player logic here
	}

}
```

The method `suggestMove` has the following arguments:

- `Graph g`: Represents the graph which will host the influence game. You may not change this object; mutating will result in an `UnsupportedOperationException`.
- `GameDefinition d`: Contains the game-specific fields:
    - `int actions`: How many nodes you are allowed to influence at most. The `Move` object that you return must have at most this amount of nodes. It is recommended to exhaust this limit. Players that exceed this have their moves automatically sliced by the engine.
    - `double budget`: Maximum sum of weights for all chosen move vertices. This is most of the time equal to `actions`. If your player exceeds this amount, the engine will automatically normalize your move.
    - `long execution`: Time in milliseconds available to the player to complete their execution. Moves that are submitted after this period is exhausted will be ignored.
- `MovePointer movePtr`: Used to submit a move with `MovePointer.submit(Move)`. Subsequent calls will overwrite the previous one. When a move is submitted, a deep copy is performed first, thus any subsequent mutations will not affect the submitted object.

The `Player` class contains the following additional members that you can use:

- `boolean isInterrupted()`: Interrupt flag by the game engine, signaling that this player has exhausted the available execution time and has to terminate. Submitting a move while this flag is raised has no effect. This flag is raised when `d.getExecution()` milliseconds have elapsed since the invocation of `suggestMove(...)`.
- `Logger log`: You may log messages in the console using this object, eg. `log.info("message")`.
- `String getOption(String name)`: Get a player-specific parameter (see below for more information).

Example of `suggestMove(...)` that submits a random move:

```java
public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
    Move m = new Move();
    RandomVertexIterator rvi = new RandomVertexIterator(g);
    while (m.getVerticesCount() < d.getActions()) {
        m.putVertex(rvi.next(), 1.0);
    }
    movePtr.submit(m);
    log.info("{}", m);
}
```

### Options

A `Player` subclass, besides `void suggestMove(...)`, can optionally implement `Map<String, String> defaultOptions()`. Options are player-specific values that can differentiate the player behavior slightly. Every option that is used by the player has to be passed in `defaultOptions()` and can then be manipulated using `setOption()` and queried using `getOption()`.

Example of `defaultOptions()` taken from `BruteForcePlayer`:

```java
public Map<String, String> defaultOptions() {
    Map<String, String> defaultOptions = new HashMap<>();
    defaultOptions.put("weight_levels", "10");
    defaultOptions.put("epsilon", "0.0");
    defaultOptions.put("clever", "false");
    return defaultOptions;
}
```

This method declares that `BruteForcePlayer` is using 3 options and sets default values for these.

## Changelog

### v1.7

- Added `IndexIterator` that iterates over vertices in a graph in index-based ascending order.
- `Game` now ignores player vertices for the final opinion vector average.
- Renamed `numOfMoves` to `actions`.
- Convert to gradle.

### v1.6

- Added method `public Vertex getVertexFromIndex(int index)` that returns a vertex based on its index. Index is a deterministic, per-graph attribute in [0, N), indicating the rank of the ID of the specific vertex in the ordered ID list.
- Included compiled class files of student players.

### v1.5

- Fixed a bug on the generator of the TwoWheels graph.

### v1.4

- Renamed `SimpleCirclePlayer` to `SimpleCyclePlayer` and `AdvancedCirclePlayer` to `AdvancedCyclePlayer`.
- `SimpleCyclePlayer` has been updated. This player will begin with a random ID to avoid a situation where there is no ID 1 (like when there are multiple graphs in the same framework instance). Furthermore, it better handles rounding on the `period` and achieves theoretical optimal performance.
- `AdvancedCyclePlayer` has been updated. It better handles rounding on the `period` and achieves theoretical optimal performance similar to `SimpleCyclePlayer`.
- Distance maps are now calculated using the Floyd-Warshall algorithm, instead of multiple Dijkstra executions.
- `VertexPair` class has been merged to the main project.
- Increased verbosity and error reporting.

### v1.3

- Extra method `isInterrupted()` on `Player` class gets whether the game mechanism wants the player to terminate. More convenient that manually keeping track of time using `System.currentTimeMillis()`.

### v1.2

- Added extra players: `LocalSearchDistancePlayer`, `GreedyDistancePlayer`, `ComplementaryGreedyDistancePlayer`.

### v1.1

- Fields `numOfMoves`, `budget` and `execution` merged on object `d` in `Player` class of type `GraphDefinition`.
- Field `move` of `Player` was replaced by `movePtr` of type `MovePointer` with `set()` and `get()` methods.
- Graph type no longer a field but added method `getMeta()` on `Graph` returning a characteristic string for each type of graph.
- `verbose` field was removed and replaced by `tournament` field inside `GraphDefinition`.
