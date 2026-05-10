package Services;

import Entities.Role;
import Entities.User;
import Utils.MyBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class UserCRUD implements IntrefaceCRUD<User> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private final Connection conn;

    public UserCRUD() {
        this(MyBD.getInstance().getConn());
    }

    public UserCRUD(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void ajouter(User u) throws SQLException {
        validateUser(u, true);
        String req = "INSERT INTO user(email,password,role,statut_verification,nom,prenom,telephone,cin,date_naissance,nationalite,adresse,ville,active,google_id,face_image_path,face_captured_at,face_descriptor,last_seen_at) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            bindUser(pst, u);
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(User u) throws SQLException {
        if (u.getId() <= 0) {
            throw new IllegalArgumentException("L'identifiant utilisateur est obligatoire.");
        }
        validateUser(u, false);
        String req = "UPDATE user SET email=?, password=?, role=?, statut_verification=?, nom=?, prenom=?, telephone=?, cin=?, date_naissance=?, nationalite=?, adresse=?, ville=?, active=?, google_id=?, face_image_path=?, face_captured_at=?, face_descriptor=?, last_seen_at=? WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            bindUser(pst, u);
            pst.setInt(19, u.getId());
            ensureRowUpdated(pst.executeUpdate(), "Aucun utilisateur trouve avec l'id " + u.getId());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM user WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            ensureRowUpdated(pst.executeUpdate(), "Aucun utilisateur trouve avec l'id " + id);
        }
    }

    @Override
    public List<User> afficher() throws SQLException {
        String req = "SELECT * FROM user ORDER BY id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(req)) {
            List<User> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapUser(rs));
            }
            return list;
        }
    }

    @Override
    public User findById(int id) throws SQLException {
        String req = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String req = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setString(1, normalize(email));
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        }
    }

    public void setActive(int id, boolean active) throws SQLException {
        String req = "UPDATE user SET active=? WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setBoolean(1, active);
            pst.setInt(2, id);
            ensureRowUpdated(pst.executeUpdate(), "Aucun utilisateur trouve avec l'id " + id);
        }
    }

    public void toggleActive(int id) throws SQLException {
        String req = "UPDATE user SET active = NOT active WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            ensureRowUpdated(pst.executeUpdate(), "Aucun utilisateur trouve avec l'id " + id);
        }
    }

    public void toggleStatus(int userId, boolean newStatus) throws SQLException {
        setActive(userId, newStatus);
    }

    private void bindUser(PreparedStatement pst, User u) throws SQLException {
        pst.setString(1, normalize(u.getEmail()));
        pst.setString(2, u.getPassword());
        pst.setString(3, u.getRole() == null ? null : u.getRole().name());
        pst.setString(4, isBlank(u.getStatutVerification()) ? "VERIFIE" : u.getStatutVerification().trim().toUpperCase(Locale.ROOT));
        pst.setString(5, trimToNull(u.getNom()));
        pst.setString(6, trimToNull(u.getPrenom()));
        pst.setString(7, trimToNull(u.getTelephone()));
        pst.setString(8, trimToNull(u.getCin()));
        setLocalDate(pst, 9, u.getDateNaissance());
        pst.setString(10, trimToNull(u.getNationalite()));
        pst.setString(11, trimToNull(u.getAdresse()));
        pst.setString(12, trimToNull(u.getVille()));
        pst.setBoolean(13, u.isActive());
        pst.setString(14, trimToNull(u.getGoogleId()));
        pst.setString(15, trimToNull(u.getFaceImagePath()));
        setLocalDateTime(pst, 16, u.getFaceCapturedAt());
        pst.setString(17, trimToNull(u.getFaceDescriptor()));
        setLocalDateTime(pst, 18, u.getLastSeenAt());
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(parseRole(rs.getString("role")));
        u.setStatutVerification(rs.getString("statut_verification"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setTelephone(rs.getString("telephone"));
        u.setCin(rs.getString("cin"));
        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            u.setDateNaissance(dateNaissance.toLocalDate());
        }
        u.setNationalite(rs.getString("nationalite"));
        u.setAdresse(rs.getString("adresse"));
        u.setVille(rs.getString("ville"));
        u.setActive(rs.getBoolean("active"));
        u.setGoogleId(rs.getString("google_id"));
        u.setFaceImagePath(rs.getString("face_image_path"));
        u.setFaceCapturedAt(toLocalDateTime(rs.getTimestamp("face_captured_at")));
        u.setFaceDescriptor(rs.getString("face_descriptor"));
        u.setLastSeenAt(toLocalDateTime(rs.getTimestamp("last_seen_at")));
        return u;
    }

    private void validateUser(User u, boolean isCreate) {
        if (u == null) {
            throw new IllegalArgumentException("L'utilisateur est obligatoire.");
        }
        if (isBlank(u.getEmail()) || !EMAIL_PATTERN.matcher(u.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("L'email est invalide.");
        }
        if (isBlank(u.getPassword())) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire.");
        }
        if (u.getRole() == null) {
            throw new IllegalArgumentException("Le role est obligatoire.");
        }
        if (u.getCin() != null && u.getCin().trim().length() > 20) {
            throw new IllegalArgumentException("Le CIN ne doit pas depasser 20 caracteres.");
        }
        if (u.getTelephone() != null && u.getTelephone().trim().length() > 20) {
            throw new IllegalArgumentException("Le telephone ne doit pas depasser 20 caracteres.");
        }
        if (u.getDateNaissance() != null && u.getDateNaissance().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de naissance est invalide.");
        }
        if (!isCreate && u.getId() <= 0) {
            throw new IllegalArgumentException("L'identifiant utilisateur est obligatoire.");
        }
    }

    private void ensureRowUpdated(int updatedRows, String message) throws SQLException {
        if (updatedRows == 0) {
            throw new SQLException(message);
        }
    }

    private void setLocalDate(PreparedStatement pst, int index, LocalDate value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.DATE);
        } else {
            pst.setDate(index, Date.valueOf(value));
        }
    }

    private void setLocalDateTime(PreparedStatement pst, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            pst.setNull(index, Types.TIMESTAMP);
        } else {
            pst.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private Role parseRole(String dbRole) {
        if (isBlank(dbRole)) {
            return null;
        }
        return Role.valueOf(dbRole.trim().toUpperCase(Locale.ROOT));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
