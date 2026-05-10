package Entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Projet {
    private int id;
    private String titre;
    private String secteur;
    private String descriptionCourte;
    private String descriptionLongue;
    private String photoUrls;
    private Statut statut;
    private BigDecimal objectifTnd = BigDecimal.ZERO;
    private int dureeCampagneJours = 30;
    private String modeRemboursement = "STANDARD";
    private BigDecimal tauxInteretPct;
    private Integer dureeRemboursementMois;
    private BigDecimal margeBruteEstimeeTnd;
    private BigDecimal resultatNetEstimeTnd;
    private int entrepreneurId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Projet() {
    }

    public Projet(int id, String titre, String description, Statut statut, int entrepreneurId) {
        this.id = id;
        this.titre = titre;
        this.descriptionCourte = description;
        this.descriptionLongue = description;
        this.statut = statut;
        this.entrepreneurId = entrepreneurId;
        this.secteur = "NON_RENSEIGNE";
    }

    public Projet(String titre, String description, Statut statut, int entrepreneurId) {
        this.titre = titre;
        this.descriptionCourte = description;
        this.descriptionLongue = description;
        this.statut = statut;
        this.entrepreneurId = entrepreneurId;
        this.secteur = "NON_RENSEIGNE";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProjet() {
        return id;
    }

    public void setIdProjet(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSecteur() {
        return secteur;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public String getDescriptionCourte() {
        return descriptionCourte;
    }

    public void setDescriptionCourte(String descriptionCourte) {
        this.descriptionCourte = descriptionCourte;
    }

    public String getDescriptionLongue() {
        return descriptionLongue;
    }

    public void setDescriptionLongue(String descriptionLongue) {
        this.descriptionLongue = descriptionLongue;
    }

    public String getDescription() {
        return descriptionCourte;
    }

    public void setDescription(String description) {
        this.descriptionCourte = description;
        if (this.descriptionLongue == null || this.descriptionLongue.isBlank()) {
            this.descriptionLongue = description;
        }
    }

    public String getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(String photoUrls) {
        this.photoUrls = photoUrls;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public BigDecimal getObjectifTnd() {
        return objectifTnd;
    }

    public void setObjectifTnd(BigDecimal objectifTnd) {
        this.objectifTnd = objectifTnd;
    }

    public int getDureeCampagneJours() {
        return dureeCampagneJours;
    }

    public void setDureeCampagneJours(int dureeCampagneJours) {
        this.dureeCampagneJours = dureeCampagneJours;
    }

    public String getModeRemboursement() {
        return modeRemboursement;
    }

    public void setModeRemboursement(String modeRemboursement) {
        this.modeRemboursement = modeRemboursement;
    }

    public BigDecimal getTauxInteretPct() {
        return tauxInteretPct;
    }

    public void setTauxInteretPct(BigDecimal tauxInteretPct) {
        this.tauxInteretPct = tauxInteretPct;
    }

    public Integer getDureeRemboursementMois() {
        return dureeRemboursementMois;
    }

    public void setDureeRemboursementMois(Integer dureeRemboursementMois) {
        this.dureeRemboursementMois = dureeRemboursementMois;
    }

    public BigDecimal getMargeBruteEstimeeTnd() {
        return margeBruteEstimeeTnd;
    }

    public void setMargeBruteEstimeeTnd(BigDecimal margeBruteEstimeeTnd) {
        this.margeBruteEstimeeTnd = margeBruteEstimeeTnd;
    }

    public BigDecimal getResultatNetEstimeTnd() {
        return resultatNetEstimeTnd;
    }

    public void setResultatNetEstimeTnd(BigDecimal resultatNetEstimeTnd) {
        this.resultatNetEstimeTnd = resultatNetEstimeTnd;
    }

    public int getEntrepreneurId() {
        return entrepreneurId;
    }

    public void setEntrepreneurId(int entrepreneurId) {
        this.entrepreneurId = entrepreneurId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Projet{id=" + id
                + ", titre='" + titre + '\''
                + ", statut=" + statut
                + ", entrepreneurId=" + entrepreneurId
                + ", secteur='" + secteur + '\''
                + "}";
    }
}
