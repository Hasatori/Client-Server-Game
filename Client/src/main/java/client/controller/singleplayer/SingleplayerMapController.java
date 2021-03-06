package client.controller.singleplayer;

import com.sun.javafx.scene.traversal.Direction;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.controller.Controller;
import client.controller.MapController;
import client.controller.StartController;
import client.model.map.*;
import client.view.DialogFactory;
import client.view.MapView;

import java.io.IOException;

import java.util.Optional;

public class SingleplayerMapController extends Controller implements MapController {

    private final Logger LOGGER = LoggerFactory.getLogger(SingleplayerMapController.class);
    private Map map;
    private MapView view;
    private String mapName;
    private String playerName;

    public SingleplayerMapController(Stage stage, String mapName, String playerName) {
        super(stage);
        this.mapName = mapName;
        this.playerName = playerName;
        createMap();
    }

    private void createMap() {
        this.map = MapFactory.getInstance().getMap(mapName, playerName);
        try {
            this.view = new MapView(this, this.map.getMapParts(), this.map.getName());
        } catch (IOException e) {
            LOGGER.error("Failed to create MapView", e);
        }
    }

    @Override
    public void loadView() {
        this.stage.setScene(this.view);
        this.stage.show();
    }

    @Override
    public void movePlayer(KeyCode keyCode) {
        try {
            Direction direction = Direction.valueOf(keyCode.toString());
            map.movePlayer(direction, playerName);
            stage.setScene(new MapView(this, map.getMapParts(), map.getName()));
            this.mapName = map.getName();
            if (map.checkWinCondition()) {
                LOGGER.info("Game ended.{} has won", playerName);
                DialogFactory.getAlert(Alert.AlertType.INFORMATION, "Game ended", "You have won").showAndWait();
                new SingleplayerController(stage).loadView();
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void restartMap() {
        createMap();
        loadView();
    }

    public void quitMap() {
        Optional<ButtonType> result = DialogFactory.getConfirmDialog("Quiting game", "Are you sure?", "You are about to quit the game.").showAndWait();
        if (result.get() == ButtonType.OK) {
                new SingleplayerController(stage).loadView();
        }
    }
}
