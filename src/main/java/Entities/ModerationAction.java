package Entities;

import java.time.LocalDateTime;

public class ModerationAction {
    private int id;
    private int adminId;
    private TargetType targetType;
    private int targetId;
    private Decision decision;
    private String commentaire;
    private LocalDateTime createdAt;

    public ModerationAction() {
    }

    public ModerationAction(int id, int adminId, TargetType targetType, int targetId, Decision decision, String commentaire) {
        this.id = id;
        this.adminId = adminId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.decision = decision;
        this.commentaire = commentaire;
    }

    public ModerationAction(int adminId, TargetType targetType, int targetId, Decision decision, String commentaire) {
        this.adminId = adminId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.decision = decision;
        this.commentaire = commentaire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public String getTypeCible() {
        return targetType == null ? null : targetType.name();
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getIdCible() {
        return targetId;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreeLe() {
        return createdAt;
    }

    public void setCreeLe(LocalDateTime creeLe) {
        this.createdAt = creeLe;
    }

    @Override
    public String toString() {
        return "ModerationAction{id=" + id
                + ", adminId=" + adminId
                + ", targetType=" + targetType
                + ", targetId=" + targetId
                + ", decision=" + decision
                + ", commentaire='" + commentaire + '\''
                + ", createdAt=" + createdAt
                + "}";
    }
}
