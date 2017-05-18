package gr.james.influence.game;

@FunctionalInterface
public interface TournamentHandler {
    void progressChanged(int done, int total);
}
