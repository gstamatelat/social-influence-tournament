# Social Influence Tournament

Tournament edition of social-influence for courses

## Usage

`Player` class contains the following members:

- `Graph g`
- `GameDefinition d`
- `MovePointer movePtr`
- `boolean isInterrupted()`
- `Map<String, String> options`

`Player` class must overload `void getMove()` and optionally `Player putDefaultOptions()`.

Example of `void getMove()` that submits a random move:

```java
public void getMove() {
	Move m = new Move();
	RandomVertexIterator rvi = new RandomVertexIterator(g);
	while (m.getVerticesCount() < d.getNumOfMoves()) {
		m.putVertex(rvi.next(), 1.0);
	}
	if (!this.d.getTournament()) {
		Helper.log("Player: " + m);
	}
	movePtr.set(m);
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
