import Utils.sceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        sceneManager.init(stage);
        sceneManager.switchTo("/login.fxml", "Invest - Admin Login");
    }

    public static void main(String[] args) {
        launch();
    }
}
