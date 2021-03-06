/*
 * Copyright 2012 JUGChennai.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.jugchennai.urbantravellers.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The class that models the game-board this is the underlying class which reports
 * about position of the players in the board and makes changes in their
 * position for the given dice value also helps to decide which player has won 
 * the race.
 *
 * @author prasannakumar
 */
public class GameBoard {

    protected Map<String, Player> playerMap = new HashMap();
    private SignalPoint signalPoints[];
    private int lastPos = 16;
    private int maxPlayers = 6;
    private GameBoardConfig boardConfig;

    protected GameBoard() {}
    
    protected GameBoard(GameBoardConfig boardConfig) {
        this.boardConfig = boardConfig;
        this.lastPos = boardConfig.getLastPosOnBoard();
        this.signalPoints = boardConfig.getSigPos();
        this.maxPlayers = boardConfig.getMaxNoOfPlayer();
    }

    /**
     * A player should be enrolled to the game until the max allowed players
     * reached
     *
     * @param player
     * @throws Exception
     */
    public void addPlayerToBoard(Player player) throws Exception {
        if (playerMap.size() <= maxPlayers) {
            playerMap.put(player.getName(), player);
        } else {
            throw new Exception("Max player reached");
        }
    }

    /**
     * player position on the board has to be updated each time dice is rolled
     *
     * @param name
     * @param diceValue
     * @return
     */
    public Player movePlayerPositionOnBoard(String name, int diceValue) {
        Player player = playerMap.get(name);
        SignalPoint nearPoint = findNearestPoint(player.getPosition());
        boolean honorDice = false;
        if (nearPoint.isPositionInSignal(player.getPosition())) {
            int ed = nearPoint.getValueToPassThrough(player.getPosition(), diceValue);
            if (ed == diceValue) {
                honorDice = true;
            }
        } else {
            honorDice = true;
        }
        if (honorDice) {
            player.setDiceValue(diceValue);
            player.setPosition(player.getPosition() + diceValue);
        }
        return player;
    }

    /**
     * short circuit the game flow by finding if the given player has reach the
     * last position in the game board
     *
     * @param p
     * @return
     */
    public boolean hasPlayerWon(Player p) {
        return p.getPosition() == lastPos;
    }

    /**
     * obtains the game config which is used by the game controller class to
     * change any behavior of the game board
     *
     * @return
     */
    public GameBoardConfig getBoardConfig() {
        return boardConfig;
    }

    /**
     * local helper method to find the nearest proximate signal point
     *
     * @param pos
     * @return
     */
    private SignalPoint findNearestPoint(int pos) {
        SignalPoint spoint = null;
        for (SignalPoint point : signalPoints) {
            if (pos <= point.getSignalPos()) {
                spoint = point;
                break;
            }
        }
        return spoint;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.lastPos;
        hash = 13 * hash + this.maxPlayers;
        hash = 13 * hash + Objects.hashCode(this.boardConfig);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameBoard other = (GameBoard) obj;
        if (this.lastPos != other.lastPos) {
            return false;
        }
        if (this.maxPlayers != other.maxPlayers) {
            return false;
        }
        if (this.boardConfig != other.boardConfig) {
            return false;
        }
        return true;
    }
}