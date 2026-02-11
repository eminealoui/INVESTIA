package Services;
import Entities.*;
import Utils.MyBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ModerationActionCRUD implements IntrefaceCRUD<ModerationAction> {
    Connection conn;
    private final ProjetCRUD projetCRUD = new ProjetCRUD();
    private final EvenementCRUD evenementCRUD = new EvenementCRUD();

    public ModerationActionCRUD() {
        conn = MyBD.getInstance().getConn();
    }

    @Override
    public void ajouter(ModerationAction m) throws SQLException {
        String req = "INSERT INTO moderation_actions(admin_id,target_type,target_id,decision,commentaire) VALUES (?,?,?,?,?)";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, m.getAdminId());
        pst.setString(2, m.getTargetType().name());
        pst.setInt(3, m.getTargetId());
        pst.setString(4, m.getDecision().name());
        pst.setString(5, m.getCommentaire());
        pst.executeUpdate();
        System.out.println("✅ ModerationAction ajoutée !");
    }

    @Override
    public void modifier(ModerationAction m) throws SQLException {
        String req = "UPDATE moderation_actions SET admin_id=?, target_type=?, target_id=?, decision=?, commentaire=? WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, m.getAdminId());
        pst.setString(2, m.getTargetType().name());
        pst.setInt(3, m.getTargetId());
        pst.setString(4, m.getDecision().name());
        pst.setString(5, m.getCommentaire());
        pst.setInt(6, m.getId());
        pst.executeUpdate();
        System.out.println("✅ ModerationAction modifiée !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM moderation_actions WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("✅ ModerationAction supprimée !");
    }

    @Override
    public List<ModerationAction> afficher() throws SQLException {
        String req = "SELECT * FROM moderation_actions ORDER BY id DESC";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(req);

        List<ModerationAction> list = new ArrayList<>();
        while (rs.next()) {
            ModerationAction m = new ModerationAction(
                    rs.getInt("id"),
                    rs.getInt("admin_id"),
                    TargetType.valueOf(rs.getString("target_type")),
                    rs.getInt("target_id"),
                    Decision.valueOf(rs.getString("decision")),
                    rs.getString("commentaire")
            );
            list.add(m);
        }
        return list;
    }

    // ✅ LOGIQUE METIER SIMPLE : admin valide/refuse projet + log action
    public void moderateProjet(int adminId, int projetId, Decision decision, String commentaire) throws SQLException {
        conn.setAutoCommit(false);
        try {
            ajouter(new ModerationAction(adminId, TargetType.PROJET, projetId, decision, commentaire));

            Statut statut = (decision == Decision.VALIDER) ? Statut.VALIDE : Statut.REFUSE;
            projetCRUD.updateStatut(projetId, statut);

            conn.commit();
            System.out.println("✅ Projet modéré: " + statut);
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ✅ LOGIQUE METIER SIMPLE : admin valide/refuse event + log action
    public void moderateEvenement(int adminId, int eventId, Decision decision, String commentaire) throws SQLException {
        conn.setAutoCommit(false);
        try {
            ajouter(new ModerationAction(adminId, TargetType.EVENEMENT, eventId, decision, commentaire));

            Statut statut = (decision == Decision.VALIDER) ? Statut.VALIDE : Statut.REFUSE;
            evenementCRUD.updateStatut(eventId, statut);

            conn.commit();
            System.out.println("✅ Evenement modéré: " + statut);
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
