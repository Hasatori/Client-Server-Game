package client.model.map;

import com.sun.javafx.scene.traversal.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Map {
    private final String mapPath;
    private final int playerNumber;
    private final String playerName;

    public String getName() {
        return name;
    }

    private final Logger LOGGER = LoggerFactory.getLogger(Map.class);
    private String name;

    public synchronized List<List<MapPart>> getMapParts() {
        List<List<MapPart>> arrayListMapParts = new LinkedList<>();

        for (int i = 0; i < mapParts.length; i++) {
            List<MapPart> row = new LinkedList<>();
            for (int column = 0; column < mapParts[i].length; column++) {
                row.add(mapParts[i][column]);
            }
            arrayListMapParts.add(row);
        }
        return Collections.unmodifiableList(arrayListMapParts);
    }

    public MapPart[][] mapParts;
    private final List<Player> players = new LinkedList<>();
    private Player player;
    private Player secondPlayer;
    private final List<Target> targets = new LinkedList<>();

    public Map(String name, boolean multiplayer, int playerNumber, String playerName) {
        this.playerNumber = playerNumber;
        this.playerName = playerName;
        if (multiplayer) {
            this.mapPath = "plans/multiplayer/";
        } else {
            this.mapPath = "plans/singleplayer/";
        }
        this.mapParts = new LevelLoader().load(mapPath + name + ".txt");
        fillLists();
        setPlayers(playerNumber, playerName);
        this.name = name;
        loadNeighbours();
    }

    public Map(String name, boolean multiplayer, int playerNumber, String playerName, String secondPlayerName) {
        this(name, multiplayer, playerNumber, playerName);
        this.secondPlayer.setName(secondPlayerName);
    }

    private void fillLists() {
        for (int row = 0; row < mapParts.length; row++) {
            for (int column = 0; column < mapParts[row].length; column++) {
                if (mapParts[row][column] instanceof Player) {
                    players.add((Player) mapParts[row][column]);
                }
                if (mapParts[row][column] instanceof Target) {
                    targets.add((Target) mapParts[row][column]);
                }
            }
        }
    }

    private void setPlayers(int index, String name) {
        if (players.size() == 1) {
            player = players.get(0);
        } else if (index == 0) {
            player = players.get(0);
            secondPlayer = players.get(1);
        } else {
            player = players.get(1);
            secondPlayer = players.get(0);
        }
        player.setName(name);
    }

    public synchronized void movePlayer(Direction direction) {
        System.out.println("Moving to " + direction.toString());
        player.setDirection(direction);
        this.movePart(direction, player);
    }

    public synchronized void moverOtherPlayer(Direction direction) {
        System.out.println("Moving to " + direction.toString());
        secondPlayer.setDirection(direction);
        this.movePart(direction, secondPlayer);
    }

    public synchronized boolean checkWinCondition() {
        for (Target target : targets) {
            if (!target.isCovered()) {
                return false;
            }
        }
        return true;
    }

    private synchronized void movePart(Direction direction, MapPart mapPart) {
        MapPart neighbour = mapPart.getNeighbour(direction);
        if (neighbour instanceof Door) {
            Door door = (Door) neighbour;
            this.mapParts = new LevelLoader().load(mapPath + door.getToMapName() + ".txt");
            players.clear();
            targets.clear();
            fillLists();

            setPlayers(playerNumber, playerName);
            this.name = door.getToMapName();
            loadNeighbours();
            return;
        }
        if (neighbour instanceof Box && !(mapPart instanceof Box)) {
            System.out.println("Neighbour is box");
            movePart(direction, neighbour);
        }
        neighbour = mapPart.getNeighbour(direction);

        if (neighbour instanceof Floor || neighbour instanceof Target) {
            System.out.println("Neighbour is floor");
            switch (direction) {
                case DOWN:
                    mapParts[mapPart.getPosition().row + 1][mapPart.getPosition().column] = mapPart;
                    break;
                case UP:
                    mapParts[mapPart.getPosition().row - 1][mapPart.getPosition().column] = mapPart;
                    break;
                case LEFT:
                    mapParts[mapPart.getPosition().row][mapPart.getPosition().column - 1] = mapPart;
                    break;
                case RIGHT:
                    mapParts[mapPart.getPosition().row][mapPart.getPosition().column + 1] = mapPart;
                    break;
            }
            mapParts[mapPart.getPosition().row][mapPart.getPosition().column] = neighbour;
            switchPositions(mapPart, neighbour);
            if (neighbour instanceof Target) {
                ((Target) neighbour).setCovered(mapPart);
            }
            for (Target target : targets) {
                if (neighbour.getPosition().equals(target.getInitialPosition())) {
                    switchParts(neighbour, target);
                    target.setUncovered();
                    break;
                }
            }

        }
        loadNeighbours();

    }

    private void switchPositions(MapPart firstPart, MapPart secondPart) {
        Position firstPartPosition = firstPart.getPosition();
        Position secondPartPosition = secondPart.getPosition();
        firstPart.setPosition(secondPartPosition);
        secondPart.setPosition(firstPartPosition);
    }

    private void switchParts(MapPart firstPart, MapPart secondPart) {
        mapParts[firstPart.getPosition().row][firstPart.getPosition().column] = secondPart;
        mapParts[secondPart.getPosition().row][secondPart.getPosition().column] = firstPart;
        switchPositions(firstPart, secondPart);

    }

    private void loadNeighbours() {
        for (int rowNum = 0; rowNum < mapParts.length; rowNum++) {
            for (int column = 0; column < mapParts[rowNum].length; column++) {

                if (rowNum != 0) {
                    mapParts[rowNum][column].setTop(mapParts[rowNum - 1][column]);

                }
                if (rowNum != mapParts.length - 1) {
                    mapParts[rowNum][column].setBottom(mapParts[rowNum + 1][column]);
                }
                if (column != 0) {
                    mapParts[rowNum][column].setLeft(mapParts[rowNum][column - 1]);
                }
                if (column != mapParts[rowNum].length - 1) {
                    mapParts[rowNum][column].setRight(mapParts[rowNum][column + 1]);
                }
            }
        }

    }

}