package Services;

import Entities.Decision;
import Entities.ModerationAction;
import Entities.Statut;
import Entities.TargetType;
import Utils.MyBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ModerationActionCRUD implements IntrefaceCRUD<ModerationAction> {
    private final Connection conn;
    private final ProjetCRUD projetCRUD;
    private final EvenementCRUD evenementCRUD;

    public ModerationActionCRUD() {
        this(MyBD.getInstance().getConn());
    }

    public ModerationActionCRUD(Connection conn) {
        this(conn, new ProjetCRUD(conn), new EvenementCRUD(conn));
    }

    public ModerationActionCRUD(Connection conn, ProjetCRUD projetCRUD, EvenementCRUD evenementCRUD) {
        this.conn = conn;
        this.projetCRUD = projetCRUD;
        this.evenementCRUD = evenementCRUD;
    }

    @Override
    public void ajouter(ModerationAction m) throws SQLException {
        validateAction(m, true);
        String req = "INSERT INTO moderation_actions(type_cible,id_cible,decision,commentaire,cree_le,admin_id) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, m.getTargetType().name());
            pst.setInt(2, m.getTargetId());
            pst.setString(3, m.getDecision().name());
            pst.setString(4, m.getCommentaire());
            pst.setTimestamp(5, Timestamp.valueOf(m.getCreatedAt() == null ? LocalDateTime.now() : m.getCreatedAt()));
            pst.setInt(6, m.getAdminId());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(ModerationAction m) throws SQLException {
        if (m.getId() <= 0) {
            throw new IllegalArgumentException("L'identifiant de l'action de moderation est obligatoire.");
        }
        validateAction(m, false);
        String req = "UPDATE moderation_actions SET type_cible=?, id_cible=?, decision=?, commentaire=?, cree_le=?, admin_id=? WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setString(1, m.getTargetType().name());
            pst.setInt(2, m.getTargetId());
            pst.setString(3, m.getDecision().name());
            pst.setString(4, m.getCommentaire());
            pst.setTimestamp(5, Timestamp.valueOf(m.getCreatedAt() == null ? LocalDateTime.now() : m.getCreatedAt()));
            pst.setInt(6, m.getAdminId());
            pst.setInt(7, m.getId());
            ensureRowUpdated(pst.executeUpdate(), "Aucune action de moderation trouvee avec l'id " + m.getId());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM moderation_actions WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            ensureRowUpdated(pst.executeUpdate(), "Aucune action de moderation trouvee avec l'id " + id);
        }
    }

    @Override
    public List<ModerationAction> afficher() throws SQLException {
        String req = "SELECT * FROM moderation_actions ORDER BY id DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(req)) {
            List<ModerationAction> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapAction(rs));
            }
            return list;
        }
    }

    @Override
    public ModerationAction findById(int id) throws SQLException {
        String req = "SELECT * FROM moderation_actions WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapAction(rs) : null;
            }
        }
    }

    public void moderateProjet(int adminId, int projetId, Decision decision, String commentaire) throws SQLException {
        conn.setAutoCommit(false);
        try {
            ModerationAction action = new ModerationAction(adminId, TargetType.PROJET, projetId, decision, commentaire);
            action.setCreatedAt(LocalDateTime.now());
            ajouter(action);
            projetCRUD.updateStatut(projetId, toStatut(decision));
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void moderateEvenement(int adminId, int eventId, Decision decision, String commentaire) throws SQLException {
        conn.setAutoCommit(false);
        try {
            ModerationAction action = new ModerationAction(adminId, TargetType.EVENEMENT, eventId, decision, commentaire);
            action.setCreatedAt(LocalDateTime.now());
            ajouter(action);
            evenementCRUD.updateStatut(eventId, toStatut(decision));
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private ModerationAction mapAction(ResultSet rs) throws SQLException {
        ModerationAction action = new ModerationAction();
        action.setId(rs.getInt("id"));
        action.setAdminId(rs.getInt("admin_id"));
        action.setTargetType(TargetType.valueOf(rs.getString("type_cible")));
        action.setTargetId(rs.getInt("id_cible"));
        action.setDecision(Decision.valueOf(rs.getString("decision")));
        action.setCommentaire(rs.getString("commentaire"));
        Timestamp creeLe = rs.getTimestamp("cree_le");
        if (creeLe != null) {
            action.setCreatedAt(creeLe.toLocalDateTime());
        }
        return action;
    }

    private void validateAction(ModerationAction action, boolean isCreate) {
        if (action == null) {
            throw new IllegalArgumentException("L'action de moderation est obligatoire.");
        }
        if (action.getAdminId() <= 0) {
            throw new IllegalArgumentException("L'identifiant admin est obligatoire.");
        }
        if (action.getTargetType() == null) {
            throw new IllegalArgumentException("Le type de cible est obligatoire.");
        }
        if (action.getTargetId() <= 0) {
            throw new IllegalArgumentException("L'identifiant cible est obligatoire.");
        }
        if (action.getDecision() == null) {
            throw new IllegalArgumentException("La decision est obligatoire.");
        }
        if (!isCreate && action.getId() <= 0) {
            throw new IllegalArgumentException("L'identifiant de l'action de moderation est obligatoire.");
        }
    }

    private Statut toStatut(Decision decision) {
        return decision == Decision.VALIDER ? Statut.VALIDE : Statut.REFUSE;
    }

    private void ensureRowUpdated(int updatedRows, String message) throws SQLException {
        if (updatedRows == 0) {
            throw new SQLException(message);
        }
    }
}
