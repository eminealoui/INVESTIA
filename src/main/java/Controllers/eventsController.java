package Controllers;

import Entities.Evenement;
import Entities.Statut;
import Services.EvenementCRUD;
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
import java.time.LocalDate;
import java.util.Optional;

public class eventsController {

    // ====== ADAPTE ICI SI TES STATUTS ONT D’AUTRES NOMS ======
    private static final Statut STATUT_PENDING = Statut.EN_ATTENTE;
    private static final Statut STATUT_VALID   = Statut.VALIDE;
    private static final Statut STATUT_REFUSED = Statut.REFUSE;
    // =========================================================

    @FXML private Label totalLabel, validLabel, refusedLabel, pendingLabel;

    @FXML private TextField searchField;
    @FXML private TableView<Evenement> tableEvents;
    @FXML private TableColumn<Evenement, Number> colId;
    @FXML private TableColumn<Evenement, String> colTitle;
    @FXML private TableColumn<Evenement, String> colDesc;
    @FXML private TableColumn<Evenement, String> colDate;
    @FXML private TableColumn<Evenement, Number> colEntrepreneur;
    @FXML private TableColumn<Evenement, String> colStatus;
    @FXML private TableColumn<Evenement, Void> colActions;

    private final EvenementCRUD crud = new EvenementCRUD();
    private final ObservableList<Evenement> masterData = FXCollections.observableArrayList();
    private FilteredList<Evenement> filtered;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()));
        colTitle.setCellValueFactory(d -> new SimpleStringProperty(safe(d.getValue().getTitre())));
        colDesc.setCellValueFactory(d -> new SimpleStringProperty(safe(d.getValue().getDescription())));
        colEntrepreneur.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getEntrepreneurId()));

        colDate.setCellValueFactory(d -> {
            LocalDate date = d.getValue().getDateEvent();
            return new SimpleStringProperty(date == null ? "" : date.toString());
        });

        colStatus.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getStatut() == null ? "" : d.getValue().getStatut().name()
        ));

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
        filtered = new FilteredList<>(masterData, e -> true);
        tableEvents.setItems(filtered);

        searchField.textProperty().addListener((obs, oldV, q) -> {
            String query = q == null ? "" : q.trim().toLowerCase();
            filtered.setPredicate(ev -> {
                if (query.isEmpty()) return true;
                String dateStr = ev.getDateEvent() == null ? "" : ev.getDateEvent().toString();
                return safe(ev.getTitre()).toLowerCase().contains(query)
                        || safe(ev.getDescription()).toLowerCase().contains(query)
                        || dateStr.toLowerCase().contains(query)
                        || String.valueOf(ev.getEntrepreneurId()).contains(query)
                        || (ev.getStatut() != null && ev.getStatut().name().toLowerCase().contains(query))
                        || String.valueOf(ev.getId()).contains(query);
            });
            updateStats(filtered);
        });
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {

            private final Label approve = icon("✔", "actionIcon", "actionApprove");
            private final Label reject  = icon("✖", "actionIcon", "actionReject");
            private final Label del     = icon("🗑", "actionIcon", "actionDelete");
            private final HBox box = new HBox(10, approve, reject, del);

            {
                box.setAlignment(Pos.CENTER);

                approve.setOnMouseClicked(e -> {
                    Evenement ev = getTableView().getItems().get(getIndex());
                    toggleToStatus(ev, STATUT_VALID);
                });

                reject.setOnMouseClicked(e -> {
                    Evenement ev = getTableView().getItems().get(getIndex());
                    toggleToStatus(ev, STATUT_REFUSED);
                });

                del.setOnMouseClicked(e -> {
                    Evenement ev = getTableView().getItems().get(getIndex());
                    confirmDelete(ev);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void toggleToStatus(Evenement ev, Statut target) {
        try {
            Statut current = ev.getStatut() == null ? STATUT_PENDING : ev.getStatut();
            Statut next = current == target ? STATUT_PENDING : target;

            crud.updateStatut(ev.getId(), next);
            toast("✅ Statut mis à jour: " + next.name());
            refresh();
        } catch (SQLException ex) {
            ex.printStackTrace();
            toast("❌ Erreur update statut");
        }
    }

    private void confirmDelete(Evenement ev) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation");
        a.setHeaderText("Supprimer cet événement ?");
        a.setContentText("Événement: " + safe(ev.getTitre()) + " (ID " + ev.getId() + ")");

        Optional<ButtonType> res = a.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        try {
            crud.supprimer(ev.getId());
            toast("✅ Événement supprimé");
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
            toast("❌ Erreur chargement événements");
        }
    }

    private void updateStats(Iterable<Evenement> list) {
        int total = 0, valid = 0, refused = 0, pending = 0;
        for (Evenement ev : list) {
            total++;
            Statut st = ev.getStatut() == null ? STATUT_PENDING : ev.getStatut();
            if (st == STATUT_VALID) valid++;
            else if (st == STATUT_REFUSED) refused++;
            else pending++;
        }
        totalLabel.setText(String.valueOf(total));
        validLabel.setText(String.valueOf(valid));
        refusedLabel.setText(String.valueOf(refused));
        pendingLabel.setText(String.valueOf(pending));
    }

    private void toast(String msg) {
        if (tableEvents == null || tableEvents.getScene() == null) {
            System.out.println(msg);
            return;
        }
        Parent root = tableEvents.getScene().getRoot();
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
