package sdokb.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sdokb.ui.KevinBaconUI;

/**
 * KevinBaconGameStateManager runs the game. Note that it does so completely
 * independent of the presentation of the game.
 *
 * @author Richard McKenna & _________________
 */
public class KevinBaconGameStateManager
{

   
    // THE GAME WILL ALWAYS BE IN
    // ONE OF THESE THREE STATES
    public enum KevinBaconGameState
    {

        GAME_NOT_STARTED,
        GAME_IN_PROGRESS,
        GAME_OVER
    }

    // THIS IS THE STATE FOR THE GAME IN PROGRESS
    private KevinBaconGameState currentGameState;

    // THIS IS THE GAME CURRENTLY BEING PLAYED
    private KevinBaconGameData gameInProgress;
    
    // WHEN THE STATE OF THE GAME CHANGES IT WILL NEED TO BE
    // REFLECTED IN THE USER INTERFACE, SO THIS CLASS NEEDS
    // A REFERENCE TO THE UI
    private KevinBaconUI ui;

    private String lastActorId;
    
    // HOLDS ALL OF THE COMPLETED GAMES. NOTE THAT THE GAME
    // IN PROGRESS IS NOT ADDED UNTIL IT IS COMPLETED
    private ArrayList<KevinBaconGameData> gamesHistory;

    private Map<String, Integer> gameHistoryStats;
    
    // THE ACTOR/FILMS GAME GRAPH DATA STRUCTURE
    private KevinBaconGameGraphManager gameGraphManager;

    // THIS IS 
    private final String NEWLINE_DELIMITER = "\n";

    /**
     * This constructor initializes this class for use, but does not start a
     * game.
     *
     * @param initUI A reference to the graphical user interface, this game
     * state manager needs to inform it of when this state changes so that it
     * can display the appropriate changes.
     */
    public KevinBaconGameStateManager(KevinBaconUI initUI)
    {
        // STORE THIS FOR LATER
        ui = initUI;

        // WE HAVE NOT STARTED A GAME YET
        currentGameState = KevinBaconGameState.GAME_NOT_STARTED;

        // NO GAMES HAVE BEEN PLAYED YET, BUT INITIALIZE
        // THE DATA STRCUTURE FOR PLACING COMPLETED GAMES
        gamesHistory = new ArrayList();
        gameHistoryStats = new HashMap();
        gameHistoryStats.put("total", 0);
        gameHistoryStats.put("losses", 0);
        gameHistoryStats.put("wins", 0);
        gameHistoryStats.put("perfect_wins", 0);
        
        // THE FIRST GAME HAS NOT BEEN STARTED YET
        gameInProgress = null;

        // THIS IS THE ACTUAL GAME GRAPH THAT WE'LL
        // WALK TO FIND CONNECTIONS BETWEEN ACTORS & FILMS
        gameGraphManager = new KevinBaconGameGraphManager();
    }

    // ACCESSOR METHODS
    public KevinBaconGameGraphManager getGameGraphManager()
    {
        return gameGraphManager;
    }

    public KevinBaconGameData getGameInProgress()
    {
        return gameInProgress;
    }

    public int getNumGamesPlayed()
    {
        return gamesHistory.size();
    }
    public int getNumGamesWon() {
        return gameHistoryStats.get("wins");
    }

    public int getNumPerfectWins() {
        return gameHistoryStats.get("perfect_wins");
    }

    public int getNumGamesLost() {
        return gameHistoryStats.get("losses");
    }

    public Iterator<KevinBaconGameData> getGamesHistoryIterator()
    {
        return gamesHistory.iterator();
    }

    public boolean isGameNotStarted()
    {
        return currentGameState == KevinBaconGameState.GAME_NOT_STARTED;
    }

    public boolean isGameOver()
    {
        return currentGameState == KevinBaconGameState.GAME_OVER;
    }

    public boolean isGameInProgress()
    {
        return currentGameState == KevinBaconGameState.GAME_IN_PROGRESS;
    }

    /**
     * This method starts a new game, initializing all the necessary data for
     * that new game as well as recording the current game (if it exists) in the
     * games history data structure. It also lets the user interface know about
     * this change of state such that it may reflect this change.
     */
    public void startNewGame()
    {
        // IS THERE A GAME ALREADY UNDERWAY?
        // YES, SO END THAT GAME AS A LOSS
        if (!isGameNotStarted() && (!gamesHistory.contains(gameInProgress)))
        {
            addGameHistory(gameInProgress);
        }

        // IF THERE IS A GAME IN PROGRESS AND THE PLAYER HASN'T WON, THAT MEANS
        // THE PLAYER IS QUITTING, SO WE NEED TO SAVE THE GAME TO OUR HISTORY
        // DATA STRUCTURE. NOTE THAT IF THE PLAYER WON THE GAME, IT WOULD HAVE
        // ALREADY BEEN SAVED SINCE THERE WOULD BE NO GUARANTEE THE PLAYER WOULD
        // CHOOSE TO PLAY AGAIN
        if (isGameInProgress() && !gameInProgress.isKevinBaconFound())
        {
            // QUIT THE GAME, WHICH SETS THE END TIME
            gameInProgress.endGameAsLoss();

            // MAKE SURE THE STATS PAGE KNOWS ABOUT THE COMPLETED GAME
            ui.getDocManager().addGameResultToStatsPage(gameInProgress);
        }

        // AND NOW MAKE A NEW GAME
        makeNewGame();

        // AND UPDATE THE GAME DISPLAY
        ui.resetUI();
        ui.getDocManager().updateActorInGamePage();
        // ui.getDocManager().printDoc(gameDoc);
        // ui.getDocManager().updateGuessesList(ui.getGSM().getGameInProgress().getStartingActor());

        // LOAD ALL THE FILMS INTO THE COMBO BOX
        ArrayList<String> startingActorFilmIds = gameInProgress.getStartingActor().getFilmIDs();
        ui.reloadComboBox(startingActorFilmIds);
    }

    /**
     * This method chooses a secret word and uses it to create a new game,
     * effectively starting it.
     */
    public void makeNewGame()
    {
        // FIRST PICK THE ACTOR
        Actor startingActor = gameGraphManager.pickRandomActor();
        ArrayList<Connection> shortestPath = gameGraphManager.findShortestPathToKevinBacon(startingActor);

        // THEN MAKE THE GAME WITH IT
        gameInProgress = new KevinBaconGameData(startingActor, shortestPath);

        // THE GAME IS OFFICIALLY UNDERWAY
        currentGameState = KevinBaconGameState.GAME_IN_PROGRESS;
    }

    public void addGameHistory(KevinBaconGameData game) {
        gamesHistory.add(game);
        
        int win = game.isKevinBaconFound() ? 1 : 0;
        int perfect_win = game.isPerfectWin() ? 1: 0;
        int loss = game.isKevinBaconFound() ? 0: 1;
        gameHistoryStats.put("total", gameHistoryStats.get("total") + 1);
        gameHistoryStats.put("losses", gameHistoryStats.get("losses") + loss);
        gameHistoryStats.put("wins", gameHistoryStats.get("wins") + win);
        gameHistoryStats.put("perfect_wins", gameHistoryStats.get("perfect_wins") + perfect_win); 
    }
    /**
     * This method processes the guess, checking to make sure it's in the
     * dictionary in use and then updating the game accordingly.
     *
     * @param guess The word that the player is guessing is the secret word.
     * Note that it must be in the dictionary.
     *
     * @throws sdokb.game.DeadEndException
     */
    public void processGuess(IMDBObject guess) throws
            DeadEndException
    {
        // ONLY PROCESS GUESSES IF A GAME IS IN PROGRESS
        if (!isGameInProgress())
        {
            return;
        }
        System.out.println("Guess id was  " + guess.getId());
        ArrayList<String> nonCircularEdges = gameInProgress.getNonRepeatingIds(guess.getId(), gameGraphManager);
        System.out.println("No of nonCircularEdges" + nonCircularEdges.toString());
        // DEAD END, NOWHERE TO GO
        if (lastActorId == null) {
            lastActorId = gameInProgress.getStartingActor().getId();
        }
        
        Connection lastNode = gameInProgress.getLastConnection();
        if (gameInProgress.isWaitingForFilm()) {
            lastNode = new Connection(lastActorId, guess.getId());
            gameInProgress.setLastConnection(lastNode);
        }
        
        if (nonCircularEdges.isEmpty())
        {
            lastActorId = null;

            // END THE GAME IN A LOSS
            currentGameState = KevinBaconGameState.GAME_OVER;
            gameInProgress.endGameAsLoss();
            gameInProgress.addGamePath(lastNode);

            addGameHistory(gameInProgress);
            ui.enableGuessComboBox(false);
            ui.getDocManager().updateGuessesList();
            ui.getDocManager().addGameResultToStatsPage(gameInProgress);
            throw new DeadEndException(guess.toString());
        }
        
     
        if (gameInProgress.isWaitingForFilm()) {
            Film _film =  gameGraphManager.getFilm(guess.getId());
            
            // check if we won
            for (String actorId : nonCircularEdges) {
                if (actorId.equals(gameGraphManager.kevinBacon.getId())) {
                    gameInProgress.endGameAsWin(gameGraphManager.kevinBacon);
                    currentGameState = KevinBaconGameState.GAME_OVER;
                    gameInProgress.addGamePath(lastNode);
                    addGameHistory(gameInProgress);
                    ui.enableGuessComboBox(false);
                    ui.getDocManager().updateGuessesList();
                    ui.getDocManager().addGameResultToStatsPage(gameInProgress);
                    lastActorId = null;

                    return;
                }
            }
            gameInProgress.addGuessMap(guess.getId(), _film);

            gameInProgress.setWaitingForFilm(false);
            ui.reloadComboBox(nonCircularEdges);
        } else 
        {
             Actor _actor = gameGraphManager.getActor(guess.getId());
             lastActorId = guess.getId();
             lastNode.setActor2Id(guess.getId());
             gameInProgress.setLastConnection(lastNode);
             gameInProgress.addGamePath(lastNode);

             if (nonCircularEdges == null) 
             
             {
                lastActorId = null;
                gameInProgress.endGameAsLoss();
                currentGameState = KevinBaconGameState.GAME_OVER;
                addGameHistory(gameInProgress);
                ui.enableGuessComboBox(false);
                ui.getDocManager().addGameResultToStatsPage(gameInProgress);
                return;
             } 
             else
                 
             {
                ui.getDocManager().updateGuessesList();
                            
                // Reset lastNode after we add the Node to the game path.
                gameInProgress.setLastConnection(null);

                this.gameInProgress.setWaitingForFilm(true);
                gameInProgress.addGuessMap(guess.getId(), _actor);
                ui.reloadComboBox(nonCircularEdges); 
            }
        }
    }
}
