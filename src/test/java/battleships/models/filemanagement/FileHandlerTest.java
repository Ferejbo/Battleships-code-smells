package battleships.models.filemanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import battleships.models.Game;
import battleships.models.GameUtils;

public class FileHandlerTest {

    private FileHandler fileHandler = new FileHandler();
    private String fileName = "testfiles/test";
    private Game game;

    @BeforeEach
    void init() {
        game = new Game(GameUtils.boardWidth, GameUtils.boardHeight);
    }

    @Test
    void testGetReceiptFile() {
        File file = fileHandler.getReceiptFile(fileName);
        String expectedRelativePath = "/target/classes/battleships/savedgame/testfiles/test.txt";
        String actualRelativePath = file.getAbsolutePath().substring(System.getProperty("user.dir").length(),
                file.getAbsolutePath().length());
        assertEquals(expectedRelativePath, actualRelativePath);
    }

    @Test
    void testDeleteGame() {
        File file = fileHandler.getReceiptFile(fileName);
        try {
            assertFalse(fileHandler.gameIsSaved(fileName));
        } catch (FileNotFoundException e1) {
            fail("failed to load file");
        }
        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
            writer.print("Some content");
            writer.close();
        } catch (FileNotFoundException e) {
            fail("Failed to load file");
        }
        String actual = null;
        try {
            actual = Files.readString(file.toPath());
        } catch (IOException ex) {
            fail("Could not load saved file");
        }
        assertEquals("Some content", actual);
        try {
            assertTrue(fileHandler.gameIsSaved(fileName));
        } catch (FileNotFoundException e1) {
            fail("failed to load file");
        }
        try {
            fileHandler.deleteSave(fileName);
        } catch (FileNotFoundException e) {
            fail("Failed to load file");
        }
        try {
            actual = Files.readString(file.toPath());
        } catch (IOException ex) {
            fail("Could not load saved file");
        }
        assertEquals("", actual);
    }

    @Test
    void testWriteGameBoard() {
        try {
            fileHandler.deleteSave(fileName);
        } catch (FileNotFoundException e1) {
            fail("Could not find file");
        }
        try {
            fileHandler.writeGameState(fileName, game);
        } catch (FileNotFoundException e) {
            fail("Could not find file");
        }
        String actual = null;
        try {
            actual = Files.readString(fileHandler.getReceiptFile(fileName).toPath());
        } catch (IOException ex) {
            fail("Could not load saved file");
        }
        assertEquals(game.serialize() + "\n", actual);
        assertThrows(
                FileNotFoundException.class,
                () -> fileHandler.writeGameState("iosdjkfh", game),
                "File not found exception");
    }

    @Test
    void testReadGameState() {
        game.randomizeCurrentBoard();
        game.switchPlayer();
        game.randomizeCurrentBoard();
        game.endPlacementPhase();
        game.fireShot(2, 2);
        game.fireShot(2, 3);
        game.switchPlayer();
        game.fireShot(2, 3);

        assertThrows(
                IllegalStateException.class,
                () -> fileHandler.readGameState(fileName),
                "No saved game exception");
        try {
            fileHandler.writeGameState(fileName, game);
        } catch (FileNotFoundException e) {
            fail("Failed to load file");
        }
        Game game2 = null;
        try {
            game2 = fileHandler.readGameState(fileName);
        } catch (FileNotFoundException e) {
            fail("Failed to load file");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            fail("No game saved");
        }
        assertEquals(game, game2);
        assertThrows(
                FileNotFoundException.class,
                () -> fileHandler.readGameState("lsdfjk"),
                "Nu such file exception");

    }

    @AfterEach
    void resetFile() {
        File writeFile = fileHandler.getReceiptFile(fileName);
        String filePath = writeFile.getAbsolutePath();
        writeFile.delete();
        writeFile = new File(filePath);
        try {
            writeFile.createNewFile();
        } catch (IOException e) {
            fail("Failed creating new file");
        }
    }
}
