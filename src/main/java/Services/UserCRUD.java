package Services;
import Entities.Role;
import Entities.User;
import Utils.MyBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserCRUD implements IntrefaceCRUD<User>{
    Connection conn;

    public UserCRUD() {
        conn = MyBD.getInstance().getConn();
    }

    @Override
    public void ajouter(User u) throws SQLException {
        String req = "INSERT INTO users(nom,email,password,role,is_active) VALUES (?,?,?,?,?)";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, u.getNom());
        pst.setString(2, u.getEmail());
        pst.setString(3, u.getPassword());
        pst.setString(4, u.getRole().name());
        pst.setBoolean(5, u.isActive());

        pst.executeUpdate();
        System.out.println("✅ User ajouté !");
    }

    @Override
    public void modifier(User u) throws SQLException {
        String req = "UPDATE users SET nom=?, email=?, password=?, role=? , is_active=? WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, u.getNom());
        pst.setString(2, u.getEmail());
        pst.setString(3, u.getPassword());
        pst.setString(4, u.getRole().name());
        pst.setBoolean(5, u.isActive());
        pst.setInt(6, u.getId());
        pst.executeUpdate();
        System.out.println("✅ User modifié !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM users WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("✅ User supprimé !");
    }

    @Override
    public List<User> afficher() throws SQLException {
        String req = "SELECT * FROM users";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(req);

        List<User> list = new ArrayList<>();
        while (rs.next()) {
            User u = new User(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("password"),
                    Role.valueOf(rs.getString("role"))
            );
            u.setActive(rs.getBoolean("is_active"));
            list.add(u);
        }
        return list;
    }
    public User findByEmail(String email) throws SQLException {
        String req = "SELECT * FROM users WHERE email = ?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setNom(rs.getString("nom"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setRole(Role.valueOf(rs.getString("role")));
            u.setActive(rs.getBoolean("is_active"));

            return u;
        }
        return null;
    }
    public void setActive(int id, boolean active) throws SQLException {
        String req = "UPDATE users SET is_active=? WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setBoolean(1, active);
        pst.setInt(2, id);
        pst.executeUpdate();
    }
    public void toggleActive(int id) throws SQLException {
        String req = "UPDATE users SET is_active = NOT is_active WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, id);
        pst.executeUpdate();
    }
    public void toggleStatus(int userId, boolean newStatus) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setBoolean(1, newStatus);
        pst.setInt(2, userId);
        pst.executeUpdate();
    }



}
