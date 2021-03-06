package client.model.map;

import com.sun.javafx.scene.traversal.Direction;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import client.util.ResourceLoader;

import java.io.IOException;

/**
 * Map part class - Player.
 * This map part indicates the player's position.
 */
public class Player extends MapPart {

    private static final String FXML_PARTS_DIR = "fxml/parts/";
    private Direction direction;
    private String name;

    /**
     * @param position position of the player
     */
    public Player(Position position) {
        super(position);
        this.direction = Direction.DOWN;
    }

    /**
     * Getter for player's name.
     * @return player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the player
     * @param name name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Node getSource() throws IOException {
        switch (this.direction) {
            case UP:
                return FXMLLoader.load(ResourceLoader.gerResourceURL(FXML_PARTS_DIR + "playerUp.fxml"));
            case DOWN:
                return FXMLLoader.load(ResourceLoader.gerResourceURL(FXML_PARTS_DIR + "playerDown.fxml"));
            case LEFT:
                return FXMLLoader.load(ResourceLoader.gerResourceURL(FXML_PARTS_DIR + "playerLeft.fxml"));
            case RIGHT:
                return FXMLLoader.load(ResourceLoader.gerResourceURL(FXML_PARTS_DIR + "playerRight.fxml"));
            default:
                throw new IllegalStateException("Wrong direction set");
        }
    }

    /**
     * Sets direction of the player.
     * @param direction direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
