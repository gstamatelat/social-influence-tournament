# Social Influence Tournament

Tournament edition of social-influence for courses

## Usage

The user must implement a player by deriving the `Player` abstract class using the following template.

```java
package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.game.players.Player;

public class MyPlayer extends Player {

	@Override
	public void getMove() {
		// Player logic here
	}

}
```

`Player` class contains the following members:

- `Graph g`: Represents the graph which will host the influence game. You may not change this object.
- `GameDefinition d`: Contains the game-specific fields:
	- `int actions`: How many nodes you are allowed to influence at most. The Move object that you return must have at most this amount of nodes. It is recommended to exhaust this limit. Players that exceed this have their moves automatically sliced by the engine.
	- `double budget`: Maximum sum of weights for all chosen move vertices. This is most of the time equal to numOfNodes. If your player exceeds this amount, the game will automatically normalize your move.
	- `long execution`: Time in milliseconds available to the player to complete their execution.
	- `boolean tournament`: Flag raised when the player is competing in a tournament settings context. If this is ``false``, you may print additional messages that can help you debug your player. If not, consider optimizing for performance and avoid printing anything.
- `MovePointer movePtr`: Used to submit a move with `movePtr.set()`. Subsequent calls will overwrite the previous one.
- `boolean isInterrupted()`: Interrupt flag by the game engine, signaling that this player has exhausted the available execution time and has to terminate. Submitting a move while this flag is raised has no effect. This flag is raised when `d.execution` milliseconds have elapsed since the invocation of `getMove()`.
- `Map<String, String> options`: Player-specific parameters, passed by the caller.

`Player` class must overload `void getMove()` and optionally `Player putDefaultOptions()`.

Example of `getMove()` that submits a random move:

```java
public void getMove() {
	Move m = new Move();
	RandomVertexIterator rvi = new RandomVertexIterator(g);
	while (m.getVerticesCount() < this.d.getActions()) {
		m.putVertex(rvi.next(), 1.0);
	}
	if (!this.d.getTournament()) {
		Helper.log("Player: " + m);
	}
	this.movePtr.set(m);
}
```

Example of `putDefaultOptions()` taken from `BruteForcePlayer`:

```java
public Player putDefaultOptions() {
	this.options.put("weight_levels", "10");
	this.options.put("epsilon", String.valueOf(Finals.DEFAULT_EPSILON));
	this.options.put("clever", "false");
	return this;
}
```

## Changelog

### v1.7

- Added `IndexIterator` that iterates over vertices in a graph in index-based ascending order.
- `Game` now ignores player vertices for the final opinion vector average.
- Renamed `numOfMoves` to `actions`.

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
