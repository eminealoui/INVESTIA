package Services;

import Entities.Evenement;
import Entities.Statut;
import Utils.MyBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EvenementCRUD implements IntrefaceCRUD<Evenement> {
    private final Connection conn;

    public EvenementCRUD() {
        this(MyBD.getInstance().getConn());
    }

    public EvenementCRUD(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void ajouter(Evenement e) throws SQLException {
        validateEvenement(e, true);
        String req = "INSERT INTO evenement(titre,description,mode,dateDebut,dateFin,lieu,meetingLink,statut,projectId,organisateurId) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            bindEvenement(pst, e);
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(Evenement e) throws SQLException {
        if (e.getId() <= 0) {
            throw new IllegalArgumentException("L'identifiant evenement est obligatoire.");
        }
        validateEvenement(e, false);
        String req = "UPDATE evenement SET titre=?, description=?, mode=?, dateDebut=?, dateFin=?, lieu=?, meetingLink=?, statut=?, projectId=?, organisateurId=? WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            bindEvenement(pst, e);
            pst.setInt(11, e.getId());
            ensureRowUpdated(pst.executeUpdate(), "Aucun evenement trouve avec l'id " + e.getId());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM evenement WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            ensureRowUpdated(pst.executeUpdate(), "Aucun evenement trouve avec l'id " + id);
        }
    }

    @Override
    public List<Evenement> afficher() throws SQLException {
        String req = "SELECT * FROM evenement ORDER BY id DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(req)) {
            List<Evenement> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapEvenement(rs));
            }
            return list;
        }
    }

    @Override
    public Evenement findById(int id) throws SQLException {
        String req = "SELECT * FROM evenement WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapEvenement(rs) : null;
            }
        }
    }

    public void updateStatut(int eventId, Statut statut) throws SQLException {
        String req = "UPDATE evenement SET statut=? WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setString(1, statut.name());
            pst.setInt(2, eventId);
            ensureRowUpdated(pst.executeUpdate(), "Aucun evenement trouve avec l'id " + eventId);
        }
    }

    private void bindEvenement(PreparedStatement pst, Evenement e) throws SQLException {
        pst.setString(1, e.getTitre().trim());
        pst.setString(2, emptyToNull(e.getDescription()));
        pst.setString(3, e.getMode().trim());
        pst.setTimestamp(4, Timestamp.valueOf(e.getDateDebut()));
        pst.setTimestamp(5, Timestamp.valueOf(e.getDateFin()));
        pst.setString(6, emptyToNull(e.getLieu()));
        pst.setString(7, emptyToNull(e.getMeetingLink()));
        pst.setString(8, e.getStatut().name());
        pst.setInt(9, e.getProjectId());
        pst.setInt(10, e.getOrganisateurId());
    }

    private Evenement mapEvenement(ResultSet rs) throws SQLException {
        Evenement e = new Evenement();
        e.setId(rs.getInt("id"));
        e.setTitre(rs.getString("titre"));
        e.setDescription(rs.getString("description"));
        e.setMode(rs.getString("mode"));
        Timestamp dateDebut = rs.getTimestamp("dateDebut");
        if (dateDebut != null) {
            e.setDateDebut(dateDebut.toLocalDateTime());
        }
        Timestamp dateFin = rs.getTimestamp("dateFin");
        if (dateFin != null) {
            e.setDateFin(dateFin.toLocalDateTime());
        }
        e.setLieu(rs.getString("lieu"));
        e.setMeetingLink(rs.getString("meetingLink"));
        e.setStatut(Statut.valueOf(rs.getString("statut")));
        e.setProjectId(rs.getInt("projectId"));
        e.setOrganisateurId(rs.getInt("organisateurId"));
        return e;
    }

    private void validateEvenement(Evenement e, boolean isCreate) {
        if (e == null) {
            throw new IllegalArgumentException("L'evenement est obligatoire.");
        }
        if (isBlank(e.getTitre())) {
            throw new IllegalArgumentException("Le titre de l'evenement est obligatoire.");
        }
        if (isBlank(e.getMode())) {
            throw new IllegalArgumentException("Le mode de l'evenement est obligatoire.");
        }
        if (e.getDateDebut() == null || e.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de debut et de fin sont obligatoires.");
        }
        if (e.getDateFin().isBefore(e.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit etre posterieure a la date de debut.");
        }
        if (e.getStatut() == null) {
            throw new IllegalArgumentException("Le statut de l'evenement est obligatoire.");
        }
        if (e.getProjectId() <= 0) {
            throw new IllegalArgumentException("Le projet associe est obligatoire.");
        }
        if (e.getOrganisateurId() <= 0) {
            throw new IllegalArgumentException("L'organisateur est obligatoire.");
        }
        if (!isCreate && e.getId() <= 0) {
            throw new IllegalArgumentException("L'identifiant evenement est obligatoire.");
        }
    }

    private void ensureRowUpdated(int updatedRows, String message) throws SQLException {
        if (updatedRows == 0) {
            throw new SQLException(message);
        }
    }

    private String emptyToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
