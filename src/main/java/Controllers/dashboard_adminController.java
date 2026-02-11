package Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import Utils.sceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class dashboard_adminController {

    @FXML private StackPane contentPane;
    @FXML private Button btnUsers;
    @FXML private Button btnProjects;
    @FXML private Button btnEvents;
    @FXML private Button btnHistory;

    @FXML
    public void initialize() {
        System.out.println("contentPane = " + contentPane);
    }

    private void setActive(Button active) {
        btnUsers.getStyleClass().remove("activeSideBtn");
        btnProjects.getStyleClass().remove("activeSideBtn");
        btnEvents.getStyleClass().remove("activeSideBtn");
        btnHistory.getStyleClass().remove("activeSideBtn");

        if (!active.getStyleClass().contains("activeSideBtn")) {
            active.getStyleClass().add("activeSideBtn");
        }
    }
    private void loadContent(String fxmlPath) {
        try {
            var url = getClass().getResource(fxmlPath);
            if (url == null) {
                throw new RuntimeException("FXML introuvable: " + fxmlPath);
            }
            Parent root = FXMLLoader.load(url);
            contentPane.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
            contentPane.getChildren().setAll(new javafx.scene.control.Label("Erreur chargement: " + fxmlPath));
        }
    }



    @FXML
    private void openUsers() {
        setActive(btnUsers);
        loadContent("/users.fxml");
    }

    @FXML
    private void openProjects() {
        setActive(btnProjects);
        loadContent("/projects.fxml");
    }

    @FXML
    private void openEvents() {
        setActive(btnEvents);
        loadContent("/events.fxml");
    }

    @FXML
    private void openHistory() {
        setActive(btnHistory);
        loadContent("/history.fxml");
    }


    @FXML
    private void logout() {
        sceneManager.switchTo("/login.fxml", "Invest - Admin Login");
    }
}
