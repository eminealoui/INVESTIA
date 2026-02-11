package Controllers;

import Entities.User;
import Services.UserCRUD;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;


import java.sql.SQLException;
import java.util.Optional;

public class UsersController {

    @FXML private Label totalLabel;
    @FXML private Label adminsLabel;
    @FXML private Label entreLabel;
    @FXML private Label invLabel;
    @FXML private VBox toastContainer;
    private static final String PLUS_PATH =
            "M19 11H13V5h-2v6H5v2h6v6h2v-6h6z";

    private static final String EDIT_PATH =
            "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zm18-11.5a1.003 1.003 0 0 0 0-1.42l-2.34-2.34a1.003 1.003 0 0 0-1.42 0l-1.83 1.83 3.75 3.75L21 5.75z";

    private static final String TRASH_PATH =
            "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zm13-14h-3.5l-1-1h-5l-1 1H5v2h14V5z";


    @FXML private TextField searchField;

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, Number> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;
    @FXML private TableColumn<User, Void> colActions;

    private final UserCRUD userCRUD = new UserCRUD();
    private final ObservableList<User> masterData = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;

    @FXML
    public void initialize() {

        // Base columns
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()));
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNom()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colRole.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRole())));

        // Status: "Active"/"Inactive"
        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().isActive() ? "Active" : "Inactive")
        );

        // Click badge -> toggle (KEEP toggleStatus)
        setupStatusBadgeToggle();

        // Actions icons (SVG) + delete from DB
        setupActionsColumn();

        // Filtering + sorting
        filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String q = (newV == null) ? "" : newV.trim().toLowerCase();

            filteredData.setPredicate(u -> {
                if (q.isEmpty()) return true;

                String id = String.valueOf(u.getId());
                String nom = safe(u.getNom());
                String email = safe(u.getEmail());
                String role = safe(String.valueOf(u.getRole()));
                String status = u.isActive() ? "active" : "inactive";

                return id.contains(q)
                        || nom.contains(q)
                        || email.contains(q)
                        || role.contains(q)
                        || status.contains(q);
            });
        });

        SortedList<User> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(tableUsers.comparatorProperty());
        tableUsers.setItems(sorted);

        // Load
        refresh();
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    private void setupStatusBadgeToggle() {
        colStatus.setCellFactory(col -> new TableCell<>() {

            private final Label badge = new Label();
            private final HBox wrap = new HBox(badge);

            {
                wrap.setAlignment(Pos.CENTER_LEFT);

                badge.getStyleClass().add("statusBadge");
                badge.setMinWidth(75);
                badge.setAlignment(Pos.CENTER);
                badge.setCursor(Cursor.HAND);

                badge.setOnMouseClicked(e -> {
                    User u = getRowUser();
                    if (u == null) return;

                    boolean newStatus = !u.isActive();
                    try {
                        // ✅ KEEP your logic
                        userCRUD.toggleStatus(u.getId(), newStatus);

                        u.setActive(newStatus);
                        tableUsers.refresh();
                        updateStats();

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showError("Erreur", "Impossible de changer le statut.");
                    }
                });
            }

            private User getRowUser() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) return null;
                return getTableView().getItems().get(getIndex());
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                badge.setText(item);
                badge.getStyleClass().removeAll("statusActive", "statusInactive");
                if ("Active".equalsIgnoreCase(item)) badge.getStyleClass().add("statusActive");
                else badge.getStyleClass().add("statusInactive");

                setGraphic(wrap);
            }
        });
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {

            private final Button btnAdd = iconButton(PLUS_PATH, "actionBtn", "actionAdd");
            private final Button btnEdit = iconButton(EDIT_PATH, "actionBtn", "actionEdit");
            private final Button btnDelete = iconButton(TRASH_PATH, "actionBtn", "actionDelete");

            private final HBox box = new HBox(10, btnAdd, btnEdit, btnDelete);

            {
                box.setAlignment(Pos.CENTER);

                btnAdd.setOnAction(e -> {
                    User u = getRowUser();
                    if (u == null) return;
                    System.out.println("ADD clicked user id=" + u.getId());
                    // TODO: open add (dialog) - si tu veux je te le fais
                });

                btnEdit.setOnAction(e -> {
                    User u = getRowUser();
                    if (u == null) return;
                    System.out.println("EDIT clicked user id=" + u.getId());
                    // TODO: open edit (dialog)
                });

                btnDelete.setOnAction(e -> {
                    User u = getRowUser();
                    if (u == null) return;
                    handleDelete(u);
                });
            }

            private User getRowUser() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) return null;
                return getTableView().getItems().get(getIndex());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private Button iconButton(String svgPathContent, String... styleClasses) {
        SVGPath svg = new SVGPath();
        svg.setContent(svgPathContent);
        svg.getStyleClass().add("actionIconSvg");

        Button b = new Button();
        b.setGraphic(svg);
        b.setCursor(Cursor.HAND);
        b.setFocusTraversable(false);
        b.getStyleClass().addAll(styleClasses);
        return b;
    }

    private void handleDelete(User u) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer utilisateur");
        confirm.setHeaderText("Confirmer la suppression");
        confirm.setContentText("ID: " + u.getId() + "\nEmail: " + u.getEmail());

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        try {
            // ✅ delete from DB using YOUR CRUD
            userCRUD.supprimer(u.getId());

            // ✅ remove from UI list
            masterData.removeIf(x -> x.getId() == u.getId());
            showToast("Utilisateur supprimé ✅", ToastType.SUCCESS);


            tableUsers.refresh();
            updateStats();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showToast("Erreur suppression ❌", ToastType.ERROR);

        }
    }

    private void refresh() {
        try {
            masterData.setAll(userCRUD.afficher());
            updateStats();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger les utilisateurs.");
        }
    }

    private void updateStats() {
        int total = masterData.size();
        long admins = masterData.stream().filter(u -> "ADMIN".equalsIgnoreCase(String.valueOf(u.getRole()))).count();
        long ent = masterData.stream().filter(u -> "ENTREPRENEUR".equalsIgnoreCase(String.valueOf(u.getRole()))).count();
        long inv = masterData.stream().filter(u -> "INVESTISSEUR".equalsIgnoreCase(String.valueOf(u.getRole()))).count();

        totalLabel.setText(String.valueOf(total));
        adminsLabel.setText(String.valueOf(admins));
        entreLabel.setText(String.valueOf(ent));
        invLabel.setText(String.valueOf(inv));
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // --- SVG paths (clean icons) ---

    private void showToast(String message, ToastType type) {
        if (toastContainer == null) return;

        Label toast = new Label(message);
        toast.getStyleClass().addAll("toast", type.cssClass);
        toast.setOpacity(0);

        Platform.runLater(() -> {
            toastContainer.getChildren().add(toast);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            PauseTransition stay = new PauseTransition(Duration.seconds(2.2));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(250), toast);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            fadeIn.setOnFinished(e -> stay.play());
            stay.setOnFinished(e -> fadeOut.play());
            fadeOut.setOnFinished(e -> toastContainer.getChildren().remove(toast));

            fadeIn.play();
        });
    }

    private enum ToastType {
        SUCCESS("toastSuccess"),
        ERROR("toastError"),
        INFO("toastInfo");

        final String cssClass;
        ToastType(String cssClass) { this.cssClass = cssClass; }
    }

}
