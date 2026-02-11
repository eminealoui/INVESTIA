package Services;
import Entities.Projet;
import Entities.Statut;
import Utils.MyBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ProjetCRUD implements IntrefaceCRUD<Projet>  {
    Connection conn;

    public ProjetCRUD() {
        conn = MyBD.getInstance().getConn();
    }

    @Override
    public void ajouter(Projet p) throws SQLException {
        String req = "INSERT INTO projets(titre,description,statut,entrepreneur_id) VALUES (?,?,?,?)";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, p.getTitre());
        pst.setString(2, p.getDescription());
        pst.setString(3, p.getStatut().name());
        pst.setInt(4, p.getEntrepreneurId());
        pst.executeUpdate();
        System.out.println("✅ Projet ajouté !");
    }

    @Override
    public void modifier(Projet p) throws SQLException {
        String req = "UPDATE projets SET titre=?, description=?, statut=?, entrepreneur_id=? WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, p.getTitre());
        pst.setString(2, p.getDescription());
        pst.setString(3, p.getStatut().name());
        pst.setInt(4, p.getEntrepreneurId());
        pst.setInt(5, p.getId());
        pst.executeUpdate();
        System.out.println("✅ Projet modifié !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM projets WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("✅ Projet supprimé !");
    }

    @Override
    public List<Projet> afficher() throws SQLException {
        String req = "SELECT * FROM projets";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(req);

        List<Projet> list = new ArrayList<>();
        while (rs.next()) {
            Projet p = new Projet(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    Statut.valueOf(rs.getString("statut")),
                    rs.getInt("entrepreneur_id")
            );
            list.add(p);
        }
        return list;
    }

    // ✅ utile pour modération
    public void updateStatut(int projetId, Statut statut) throws SQLException {
        String req = "UPDATE projets SET statut=? WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, statut.name());
        pst.setInt(2, projetId);
        pst.executeUpdate();
    }

}
