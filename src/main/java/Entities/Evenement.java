package Entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Evenement {
    private int id;
    private String titre;
    private String description;
    private String mode = "PRESENTIEL";
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private String meetingLink;
    private Statut statut;
    private int projectId;
    private int organisateurId;

    public Evenement() {
    }

    public Evenement(int id, String titre, String description, Statut statut, int organisateurId, LocalDate dateEvent) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.organisateurId = organisateurId;
        if (dateEvent != null) {
            this.dateDebut = dateEvent.atStartOfDay();
            this.dateFin = dateEvent.atTime(23, 59);
        }
    }

    public Evenement(String titre, String description, Statut statut, int organisateurId, LocalDate dateEvent) {
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.organisateurId = organisateurId;
        if (dateEvent != null) {
            this.dateDebut = dateEvent.atStartOfDay();
            this.dateFin = dateEvent.atTime(23, 59);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public LocalDate getDateEvent() {
        return dateDebut == null ? null : dateDebut.toLocalDate();
    }

    public void setDateEvent(LocalDate dateEvent) {
        if (dateEvent == null) {
            this.dateDebut = null;
            this.dateFin = null;
            return;
        }
        this.dateDebut = dateEvent.atStartOfDay();
        this.dateFin = dateEvent.atTime(23, 59);
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getOrganisateurId() {
        return organisateurId;
    }

    public void setOrganisateurId(int organisateurId) {
        this.organisateurId = organisateurId;
    }

    public int getEntrepreneurId() {
        return organisateurId;
    }

    public void setEntrepreneurId(int entrepreneurId) {
        this.organisateurId = entrepreneurId;
    }

    @Override
    public String toString() {
        return "Evenement{id=" + id
                + ", titre='" + titre + '\''
                + ", statut=" + statut
                + ", projectId=" + projectId
                + ", organisateurId=" + organisateurId
                + ", dateDebut=" + dateDebut
                + ", dateFin=" + dateFin
                + "}";
    }
}
