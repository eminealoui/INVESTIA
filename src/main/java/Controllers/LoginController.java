package Controllers;

import Entities.Role;
import Entities.User;
import Services.UserCRUD;
import Utils.sceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.util.Duration;


import java.sql.SQLException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label msgLabel;

    private final UserCRUD userCRUD = new UserCRUD();

    @FXML
    private void onLogin() {
        String email = emailField.getText().trim();
        String pass = passwordField.getText().trim();

        try {
            User u = userCRUD.findByEmail(email);   // on va l’ajouter juste après
            if (u == null) {
                msgLabel.setText("Email introuvable");
                return;
            }
            if (!u.getPassword().equals(pass)) {
                msgLabel.setText("Mot de passe incorrect");
                return;
            }
            if (u.getRole() != Role.ADMIN) {
                msgLabel.setText("Accès réservé à l'admin !");
                return;
            }

            msgLabel.setText("✅ Connexion OK");
            sceneManager.switchTo("/dashboard_admin.fxml", "Dashboard Admin");

        } catch (SQLException e) {
            msgLabel.setText("Erreur BD: " + e.getMessage());
        }
    }
    public void onForgotPassword() {
        System.out.println("Forgot password clicked (à faire plus tard)");
    }
    @FXML
    public void initialize() {
        // Attendre que la scène soit prête, ensuite on récupère les "featureCard"
        Platform.runLater(() -> {
            if (emailField == null || emailField.getScene() == null) return;

            var nodes = emailField.getScene().getRoot().lookupAll(".featureCard");
            for (Node n : nodes) {
                addHoverScale(n);
            }
        });


    }
    private void addHoverScale(Node node) {
        ScaleTransition stIn = new ScaleTransition(Duration.millis(140), node);
        stIn.setToX(1.02);
        stIn.setToY(1.02);

        ScaleTransition stOut = new ScaleTransition(Duration.millis(140), node);
        stOut.setToX(1.00);
        stOut.setToY(1.00);

        node.setOnMouseEntered(e -> stIn.playFromStart());
        node.setOnMouseExited(e -> stOut.playFromStart());
    }



}
