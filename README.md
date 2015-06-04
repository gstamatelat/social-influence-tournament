# Social Influence Tournament

Tournament edition of social-influence for courses

## Changelog

### v1.3 - Working

- Extra method isInterrupted on Player class gets whether the game mechanism wants the Player to terminate. More convenient that manually keeping track of time using System.currentTimeMillis().

### v1.2

- Added extra players: LocalSearchDistancePlayer, GreedyDistancePlayer,  ComplementaryGreedyDistancePlayer.

### v1.1

- Fields numOfMoves, budget and execution merged on object d in Player class of type GraphDefinition.
- Field move of Player was replaced by movePtr of type MovePointer with .set() and .get() methods.
- Graph type no longer a field but added method getMeta() on Graph returning a characteristic string for each type of graph.
- verbose field was removed and replaced by tournament field inside GraphDefinition.