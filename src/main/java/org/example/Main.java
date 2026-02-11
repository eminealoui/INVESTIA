package org.example;

import java.sql.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3307/investdb";
        String user = "root";
        String password = ""; // XAMPP : vide

        try (Connection cnx = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Connexion Java -> MySQL OK");

            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, msg FROM test_connexion");

            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("msg"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}