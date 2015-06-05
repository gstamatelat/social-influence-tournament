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

- `Graph g`: Represents the graph which will host the influence game.
- `GameDefinition d`: Contains the game-specific fields:
	- `int numOfMoves`: Maximum amount of vertices to influence.
	- `double budget`: Maximum sum of weights for all chosen move vertices.
	- `long execution`: Time in milliseconds available to the player to complete their execution.
	- `boolean tournament`: Flag raised when the player is competing in a tournament context.
- `MovePointer movePtr`: Used to submit a move with `movePtr.set()`. Subsequent calls will overwrite the previous move.
- `boolean isInterrupted()`: Interrupt flag by the game engine, signaling that this player has exhausted the available execution time and has to terminate. Submitting a move while this flag is raised has no effect. This flag is raised when `d.execution` milliseconds have elapsed since the invocation of `getMove()`.
- `Map<String, String> options`: Player-specific parameters, passed by the caller.

`Player` class must overload `void getMove()` and optionally `Player putDefaultOptions()`.

Example of `getMove()` that submits a random move:

```java
public void getMove() {
	Move m = new Move();
	RandomVertexIterator rvi = new RandomVertexIterator(g);
	while (m.getVerticesCount() < this.d.getNumOfMoves()) {
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

### v1.3 - Working

- Extra method `isInterrupted()` on `Player` class gets whether the game mechanism wants the player to terminate. More convenient that manually keeping track of time using `System.currentTimeMillis()`.

### v1.2

- Added extra players: `LocalSearchDistancePlayer`, `GreedyDistancePlayer`, `ComplementaryGreedyDistancePlayer`.

### v1.1

- Fields `numOfMoves`, `budget` and `execution` merged on object `d` in `Player` class of type `GraphDefinition`.
- Field `move` of `Player` was replaced by `movePtr` of type `MovePointer` with `set()` and `get()` methods.
- Graph type no longer a field but added method `getMeta()` on `Graph` returning a characteristic string for each type of graph.
- `verbose` field was removed and replaced by `tournament` field inside `GraphDefinition`.
