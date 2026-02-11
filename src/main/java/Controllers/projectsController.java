package Controllers;

import Entities.Projet;
import Entities.Statut;
import Services.ProjetCRUD;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Optional;

public class projectsController {

    // ====== ADAPTE ICI SI TES STATUTS ONT D’AUTRES NOMS ======
    private static final Statut STATUT_PENDING = Statut.EN_ATTENTE; // exemple
    private static final Statut STATUT_VALID   = Statut.VALIDE;     // exemple
    private static final Statut STATUT_REFUSED = Statut.REFUSE;     // exemple
    // =========================================================

    @FXML private Label totalLabel, validLabel, refusedLabel, pendingLabel;

    @FXML private TextField searchField;
    @FXML private TableView<Projet> tableProjects;
    @FXML private TableColumn<Projet, Number> colId;
    @FXML private TableColumn<Projet, String> colTitle;
    @FXML private TableColumn<Projet, String> colDesc;
    @FXML private TableColumn<Projet, Number> colEntrepreneur;
    @FXML private TableColumn<Projet, String> colStatus;
    @FXML private TableColumn<Projet, Void> colActions;

    private final ProjetCRUD crud = new ProjetCRUD();
    private final ObservableList<Projet> masterData = FXCollections.observableArrayList();
    private FilteredList<Projet> filtered;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()));
        colTitle.setCellValueFactory(d -> new SimpleStringProperty(safe(d.getValue().getTitre())));
        colDesc.setCellValueFactory(d -> new SimpleStringProperty(safe(d.getValue().getDescription())));
        colEntrepreneur.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getEntrepreneurId()));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getStatut() == null ? "" : d.getValue().getStatut().name()
        ));

        // Badge statut (couleurs via CSS classes)
        colStatus.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            private final HBox wrap = new HBox(badge);
            {
                wrap.setAlignment(Pos.CENTER_LEFT);
                badge.getStyleClass().add("statusBadge");
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }

                badge.setText(item);
                badge.getStyleClass().removeAll("statusActive", "statusInactive", "statusPending");

                if (item.equalsIgnoreCase(STATUT_VALID.name())) badge.getStyleClass().add("statusActive");
                else if (item.equalsIgnoreCase(STATUT_REFUSED.name())) badge.getStyleClass().add("statusInactive");
                else badge.getStyleClass().add("statusPending");

                setGraphic(wrap);
            }
        });

        setupActionsColumn();
        refresh();
        setupSearch();
    }

    private void setupSearch() {
        filtered = new FilteredList<>(masterData, p -> true);
        tableProjects.setItems(filtered);

        searchField.textProperty().addListener((obs, oldV, q) -> {
            String query = q == null ? "" : q.trim().toLowerCase();
            filtered.setPredicate(p -> {
                if (query.isEmpty()) return true;
                return safe(p.getTitre()).toLowerCase().contains(query)
                        || safe(p.getDescription()).toLowerCase().contains(query)
                        || String.valueOf(p.getEntrepreneurId()).contains(query)
                        || (p.getStatut() != null && p.getStatut().name().toLowerCase().contains(query))
                        || String.valueOf(p.getId()).contains(query);
            });
            updateStats(filtered);
        });
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {

            // ✔ valider (vert/bleu selon ton css), ✖ refuser (rouge), 🗑 delete (rouge)
            private final Label approve = icon("✔", "actionIcon", "actionApprove");
            private final Label reject  = icon("✖", "actionIcon", "actionReject");
            private final Label del     = icon("🗑", "actionIcon", "actionDelete");
            private final HBox box = new HBox(10, approve, reject, del);

            {
                box.setAlignment(Pos.CENTER);

                approve.setOnMouseClicked(e -> {
                    Projet p = getTableView().getItems().get(getIndex());
                    toggleToStatus(p, STATUT_VALID);
                });

                reject.setOnMouseClicked(e -> {
                    Projet p = getTableView().getItems().get(getIndex());
                    toggleToStatus(p, STATUT_REFUSED);
                });

                del.setOnMouseClicked(e -> {
                    Projet p = getTableView().getItems().get(getIndex());
                    confirmDelete(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void toggleToStatus(Projet p, Statut target) {
        try {
            // toggle: si déjà target -> retourne PENDING, sinon -> target
            Statut current = p.getStatut() == null ? STATUT_PENDING : p.getStatut();
            Statut next = current == target ? STATUT_PENDING : target;

            crud.updateStatut(p.getId(), next);
            toast("✅ Statut mis à jour: " + next.name());
            refresh();
        } catch (SQLException ex) {
            ex.printStackTrace();
            toast("❌ Erreur update statut");
        }
    }

    private void confirmDelete(Projet p) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation");
        a.setHeaderText("Supprimer ce projet ?");
        a.setContentText("Projet: " + safe(p.getTitre()) + " (ID " + p.getId() + ")");

        Optional<ButtonType> res = a.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        try {
            crud.supprimer(p.getId());
            toast("✅ Projet supprimé");
            refresh();
        } catch (SQLException ex) {
            ex.printStackTrace();
            toast("❌ Erreur suppression");
        }
    }

    private void refresh() {
        try {
            masterData.setAll(crud.afficher());
            updateStats(filtered != null ? filtered : masterData);
        } catch (SQLException e) {
            e.printStackTrace();
            toast("❌ Erreur chargement projets");
        }
    }

    private void updateStats(Iterable<Projet> list) {
        int total = 0, valid = 0, refused = 0, pending = 0;
        for (Projet p : list) {
            total++;
            Statut st = p.getStatut() == null ? STATUT_PENDING : p.getStatut();
            if (st == STATUT_VALID) valid++;
            else if (st == STATUT_REFUSED) refused++;
            else pending++;
        }
        totalLabel.setText(String.valueOf(total));
        validLabel.setText(String.valueOf(valid));
        refusedLabel.setText(String.valueOf(refused));
        pendingLabel.setText(String.valueOf(pending));
    }

    // ===== Toast =====
    private void toast(String msg) {
        if (tableProjects == null || tableProjects.getScene() == null) {
            System.out.println(msg);
            return;
        }
        Parent root = tableProjects.getScene().getRoot();
        if (!(root instanceof StackPane sp)) {
            System.out.println(msg);
            return;
        }

        Label t = new Label(msg);
        t.getStyleClass().add("toast");
        StackPane.setAlignment(t, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(t, new Insets(0, 18, 18, 0));
        sp.getChildren().add(t);

        PauseTransition pt = new PauseTransition(Duration.seconds(2));
        pt.setOnFinished(e -> sp.getChildren().remove(t));
        pt.play();
    }

    private Label icon(String text, String... classes) {
        Label l = new Label(text);
        l.getStyleClass().addAll(classes);
        l.setMinSize(28, 28);
        l.setAlignment(Pos.CENTER);
        return l;
    }

    private String safe(String s) { return s == null ? "" : s; }
}
