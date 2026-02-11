package Entities;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Evenement {
    private int id;
    private String titre;
    private String description;
    private Statut statut;
    private int entrepreneurId;
    private LocalDate dateEvent;
    private LocalDateTime createdAt;

    public Evenement() {}

    public Evenement(int id, String titre, String description, Statut statut, int entrepreneurId, LocalDate dateEvent) {
        this.id = id; this.titre = titre; this.description = description; this.statut = statut;
        this.entrepreneurId = entrepreneurId; this.dateEvent = dateEvent;
    }

    public Evenement(String titre, String description, Statut statut, int entrepreneurId, LocalDate dateEvent) {
        this.titre = titre; this.description = description; this.statut = statut;
        this.entrepreneurId = entrepreneurId; this.dateEvent = dateEvent;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public int getEntrepreneurId() { return entrepreneurId; }
    public void setEntrepreneurId(int entrepreneurId) { this.entrepreneurId = entrepreneurId; }

    public LocalDate getDateEvent() { return dateEvent; }
    public void setDateEvent(LocalDate dateEvent) { this.dateEvent = dateEvent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Evenement{id=" + id + ", titre='" + titre + "', statut=" + statut + ", entrepreneurId=" + entrepreneurId + ", dateEvent=" + dateEvent + "}";
    }
}
