package Services;

import Entities.Projet;
import Entities.Statut;
import Utils.MyBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjetCRUD implements IntrefaceCRUD<Projet> {
    private final Connection conn;

    public ProjetCRUD() {
        this(MyBD.getInstance().getConn());
    }

    public ProjetCRUD(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void ajouter(Projet p) throws SQLException {
        validateProjet(p, true);
        String req = "INSERT INTO projet(statut,titre,secteur,description_courte,description_longue,photo_urls,objectif_tnd,duree_campagne_jours,mode_remboursement,taux_interet_pct,duree_remboursement_mois,marge_brute_estimee_tnd,resultat_net_estime_tnd,created_at,updated_at,entrepreneur_id) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            bindProjetForInsert(pst, p);
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(Projet p) throws SQLException {
        if (p.getId() <= 0) {
            throw new IllegalArgumentException("L'identifiant projet est obligatoire.");
        }
        validateProjet(p, false);
        String req = "UPDATE projet SET statut=?, titre=?, secteur=?, description_courte=?, description_longue=?, photo_urls=?, objectif_tnd=?, duree_campagne_jours=?, mode_remboursement=?, taux_interet_pct=?, duree_remboursement_mois=?, marge_brute_estimee_tnd=?, resultat_net_estime_tnd=?, updated_at=?, entrepreneur_id=? WHERE id_projet=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            bindProjetForUpdate(pst, p);
            pst.setInt(16, p.getId());
            ensureRowUpdated(pst.executeUpdate(), "Aucun projet trouve avec l'id " + p.getId());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM projet WHERE id_projet=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            ensureRowUpdated(pst.executeUpdate(), "Aucun projet trouve avec l'id " + id);
        }
    }

    @Override
    public List<Projet> afficher() throws SQLException {
        String req = "SELECT * FROM projet ORDER BY id_projet DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(req)) {
            List<Projet> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapProjet(rs));
            }
            return list;
        }
    }

    @Override
    public Projet findById(int id) throws SQLException {
        String req = "SELECT * FROM projet WHERE id_projet = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapProjet(rs) : null;
            }
        }
    }

    public void updateStatut(int projetId, Statut statut) throws SQLException {
        String req = "UPDATE projet SET statut=?, updated_at=? WHERE id_projet=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setString(1, statut.name());
            pst.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pst.setInt(3, projetId);
            ensureRowUpdated(pst.executeUpdate(), "Aucun projet trouve avec l'id " + projetId);
        }
    }

    public Integer findEntrepreneurProfileIdByUserId(int userId) throws SQLException {
        String req = "SELECT id_entrepreneur FROM profil_entrepreneur WHERE id_user = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? rs.getInt("id_entrepreneur") : null;
            }
        }
    }

    private void bindProjetForInsert(PreparedStatement pst, Projet p) throws SQLException {
        bindProjetCommon(pst, p);
        LocalDateTime now = LocalDateTime.now();
        pst.setTimestamp(14, Timestamp.valueOf(p.getCreatedAt() == null ? now : p.getCreatedAt()));
        pst.setTimestamp(15, Timestamp.valueOf(p.getUpdatedAt() == null ? now : p.getUpdatedAt()));
        pst.setInt(16, resolveEntrepreneurProfileId(p.getEntrepreneurId()));
    }

    private void bindProjetForUpdate(PreparedStatement pst, Projet p) throws SQLException {
        bindProjetCommon(pst, p);
        pst.setTimestamp(14, Timestamp.valueOf(p.getUpdatedAt() == null ? LocalDateTime.now() : p.getUpdatedAt()));
        pst.setInt(15, resolveEntrepreneurProfileId(p.getEntrepreneurId()));
    }

    private void bindProjetCommon(PreparedStatement pst, Projet p) throws SQLException {
        pst.setString(1, p.getStatut().name());
        pst.setString(2, p.getTitre().trim());
        pst.setString(3, p.getSecteur().trim());
        pst.setString(4, p.getDescriptionCourte().trim());
        pst.setString(5, emptyToNull(p.getDescriptionLongue()));
        pst.setString(6, emptyToNull(p.getPhotoUrls()));
        pst.setBigDecimal(7, defaultDecimal(p.getObjectifTnd()));
        pst.setInt(8, p.getDureeCampagneJours());
        pst.setString(9, p.getModeRemboursement().trim());
        setBigDecimal(pst, 10, p.getTauxInteretPct());
        setInteger(pst, 11, p.getDureeRemboursementMois());
        setBigDecimal(pst, 12, p.getMargeBruteEstimeeTnd());
        setBigDecimal(pst, 13, p.getResultatNetEstimeTnd());
    }

    private Projet mapProjet(ResultSet rs) throws SQLException {
        Projet p = new Projet();
        p.setId(rs.getInt("id_projet"));
        p.setStatut(Statut.valueOf(rs.getString("statut")));
        p.setTitre(rs.getString("titre"));
        p.setSecteur(rs.getString("secteur"));
        p.setDescriptionCourte(rs.getString("description_courte"));
        p.setDescriptionLongue(rs.getString("description_longue"));
        p.setPhotoUrls(rs.getString("photo_urls"));
        p.setObjectifTnd(rs.getBigDecimal("objectif_tnd"));
        p.setDureeCampagneJours(rs.getInt("duree_campagne_jours"));
        p.setModeRemboursement(rs.getString("mode_remboursement"));
        p.setTauxInteretPct(rs.getBigDecimal("taux_interet_pct"));
        int dureeMois = rs.getInt("duree_remboursement_mois");
        if (!rs.wasNull()) {
            p.setDureeRemboursementMois(dureeMois);
        }
        p.setMargeBruteEstimeeTnd(rs.getBigDecimal("marge_brute_estimee_tnd"));
        p.setResultatNetEstimeTnd(rs.getBigDecimal("resultat_net_estime_tnd"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            p.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            p.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        p.setEntrepreneurId(rs.getInt("entrepreneur_id"));
        return p;
    }

    private void validateProjet(Projet p, boolean isCreate) {
        if (p == null) {
            throw new IllegalArgumentException("Le projet est obligatoire.");
        }
        if (p.getStatut() == null) {
            throw new IllegalArgumentException("Le statut du projet est obligatoire.");
        }
        if (isBlank(p.getTitre())) {
            throw new IllegalArgumentException("Le titre du projet est obligatoire.");
        }
        if (isBlank(p.getSecteur())) {
            throw new IllegalArgumentException("Le secteur du projet est obligatoire.");
        }
        if (isBlank(p.getDescriptionCourte())) {
            throw new IllegalArgumentException("La description courte du projet est obligatoire.");
        }
        if (p.getDureeCampagneJours() <= 0) {
            throw new IllegalArgumentException("La duree de campagne doit etre positive.");
        }
        if (isBlank(p.getModeRemboursement())) {
            throw new IllegalArgumentException("Le mode de remboursement est obligatoire.");
        }
        if (p.getEntrepreneurId() <= 0) {
            throw new IllegalArgumentException("L'identifiant entrepreneur est obligatoire.");
        }
        if (!isCreate && p.getId() <= 0) {
            throw new IllegalArgumentException("L'identifiant projet est obligatoire.");
        }
    }

    private int resolveEntrepreneurProfileId(int entrepreneurId) throws SQLException {
        String directReq = "SELECT id_entrepreneur FROM profil_entrepreneur WHERE id_entrepreneur = ?";
        try (PreparedStatement pst = conn.prepareStatement(directReq)) {
            pst.setInt(1, entrepreneurId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_entrepreneur");
                }
            }
        }
        Integer resolved = findEntrepreneurProfileIdByUserId(entrepreneurId);
        if (resolved == null) {
            throw new SQLException("Aucun profil entrepreneur trouve pour l'identifiant " + entrepreneurId);
        }
        return resolved;
    }

    private void ensureRowUpdated(int updatedRows, String message) throws SQLException {
        if (updatedRows == 0) {
            throw new SQLException(message);
        }
    }

    private void setBigDecimal(PreparedStatement pst, int index, BigDecimal value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.DECIMAL);
        } else {
            pst.setBigDecimal(index, value);
        }
    }

    private void setInteger(PreparedStatement pst, int index, Integer value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.INTEGER);
        } else {
            pst.setInt(index, value);
        }
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String emptyToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
