package battleships.models.filemanagement;

import java.io.FileNotFoundException;

import battleships.models.Game;

public interface IFileHandler {
    public Game readGameState(String filename) throws FileNotFoundException, IllegalStateException;

    public void writeGameState(String filename, Game game) throws FileNotFoundException;

    public boolean gameIsSaved(String filename) throws FileNotFoundException;

    public void deleteSave(String filename) throws FileNotFoundException;
}
