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

- `boolean isInterrupted()`: Interrupt flag by the game engine, signaling that this player has exhausted the available execution time and has to terminate. Submitting a move while this flag is raised has no effect. This flag is raised when `d.getExecution()` milliseconds have elapsed since the invocation of `suggestMove`.
- `Logger log`: You may log messages in the console using this object, eg. `log.info("message")`. Avoid other means of I/O.
- `String getOption(String name)`: Get a player-specific parameter (see below for more information).

Example of `suggestMove` that submits a random move:

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

A `Player` subclass, besides `suggestMove`, can optionally implement `Map<String, String> defaultOptions()`. Options are player-specific values that can differentiate the player behavior slightly. Every option that is used by the player has to be passed in `defaultOptions()` and can then be manipulated using `setOption()` and queried using `getOption()`.

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
