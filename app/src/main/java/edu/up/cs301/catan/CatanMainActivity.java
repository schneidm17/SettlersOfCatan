package edu.up.cs301.catan;

import java.util.ArrayList;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;

/**
 * this is the primary activity for Catan
 *
 * @author Oney, Goldey, Schneider
 * @version November 2015
 */
public class CatanMainActivity extends GameMainActivity {

    // the port number that this game will use when playing over the network
    private static final int PORT_NUMBER = 2278;

    /**
     * Create the default configuration for this game:
     * - one human player vs. several computer player
     * - minimum of 3 player, maximum of 4
     *
     * @return the new configuration object, representing the default configuration
     */
    @Override
    public GameConfig createDefaultConfig() {

        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // Catan has two player types:  human and computer
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new CatanHumanPlayer(name);
            }
        });
        playerTypes.add(new GamePlayerType("Dumb Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new CatanComputerPlayer(name);
            }
        });
        playerTypes.add(new GamePlayerType("Smart Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new CatanSmartComputerPlayer(name);
            }
        });

        // Create a game configuration class for Counter:
        GameConfig defaultConfig = new GameConfig(playerTypes, 3, 4, "Catan", PORT_NUMBER);
        defaultConfig.addPlayer("Human", 0); // player 1: a human player
        defaultConfig.addPlayer("Computer", 1); // player 2: a computer player
        defaultConfig.addPlayer("Computer", 2); // player 3: a computer player
        defaultConfig.setRemoteData("Remote Player", "", 0);

        return defaultConfig;
    }//createDefaultConfig

    /**
     * create a local game
     *
     * @return the local game, a counter game
     */
    @Override
    public LocalGame createLocalGame() {

        return new CatanLocalGame();

    }

}
