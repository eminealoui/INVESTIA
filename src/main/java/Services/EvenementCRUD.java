package Services;
import Entities.Evenement;
import Entities.Statut;
import Utils.MyBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class EvenementCRUD implements IntrefaceCRUD<Evenement>{
    Connection conn;

    public EvenementCRUD() {
        conn = MyBD.getInstance().getConn();
    }

    @Override
    public void ajouter(Evenement e) throws SQLException {
        String req = "INSERT INTO evenements(titre,description,statut,entrepreneur_id,date_event) VALUES (?,?,?,?,?)";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, e.getTitre());
        pst.setString(2, e.getDescription());
        pst.setString(3, e.getStatut().name());
        pst.setInt(4, e.getEntrepreneurId());
        if (e.getDateEvent() != null) pst.setDate(5, Date.valueOf(e.getDateEvent()));
        else pst.setNull(5, Types.DATE);
        pst.executeUpdate();
        System.out.println("✅ Evenement ajouté !");
    }

    @Override
    public void modifier(Evenement e) throws SQLException {
        String req = "UPDATE evenements SET titre=?, description=?, statut=?, entrepreneur_id=?, date_event=? WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, e.getTitre());
        pst.setString(2, e.getDescription());
        pst.setString(3, e.getStatut().name());
        pst.setInt(4, e.getEntrepreneurId());
        if (e.getDateEvent() != null) pst.setDate(5, Date.valueOf(e.getDateEvent()));
        else pst.setNull(5, Types.DATE);
        pst.setInt(6, e.getId());
        pst.executeUpdate();
        System.out.println("✅ Evenement modifié !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM evenements WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("✅ Evenement supprimé !");
    }

    @Override
    public List<Evenement> afficher() throws SQLException {
        String req = "SELECT * FROM evenements";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(req);

        List<Evenement> list = new ArrayList<>();
        while (rs.next()) {
            Date d = rs.getDate("date_event");
            LocalDate ld = (d != null) ? d.toLocalDate() : null;

            Evenement e = new Evenement(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    Statut.valueOf(rs.getString("statut")),
                    rs.getInt("entrepreneur_id"),
                    ld
            );
            list.add(e);
        }
        return list;
    }

    public void updateStatut(int eventId, Statut statut) throws SQLException {
        String req = "UPDATE evenements SET statut=? WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(req);
        pst.setString(1, statut.name());
        pst.setInt(2, eventId);
        pst.executeUpdate();
    }
}
