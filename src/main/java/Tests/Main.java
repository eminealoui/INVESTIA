package Tests;

import Entities.Evenement;
import Entities.Projet;
import Entities.Role;
import Entities.Statut;
import Entities.User;
import Services.EvenementCRUD;
import Services.ModerationActionCRUD;
import Services.ProjetCRUD;
import Services.UserCRUD;
import Utils.MyBD;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    static UserCRUD userCRUD;
    static ProjetCRUD projetCRUD;
    static EvenementCRUD evenementCRUD;
    static ModerationActionCRUD modCRUD;

    static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        MyBD.getInstance();

        userCRUD = new UserCRUD();
        projetCRUD = new ProjetCRUD();
        evenementCRUD = new EvenementCRUD();
        modCRUD = new ModerationActionCRUD();

        while (true) {
            try {
                System.out.println("\n========= MENU =========");
                System.out.println("1) USERS (Read/Update/Delete)");
                System.out.println("2) PROJETS (Read/Update/Delete)");
                System.out.println("3) EVENEMENTS (Read/Update/Delete)");
                System.out.println("4) Ajouter data demo (Admin/Projet/Event)");
                System.out.println("0) Quit");
                System.out.print("Choix: ");

                int choix = Integer.parseInt(sc.nextLine().trim());

                switch (choix) {
                    case 1 -> menuUsers();
                    case 2 -> menuProjets();
                    case 3 -> menuEvenements();
                    case 4 -> seedDemoData();
                    case 0 -> {
                        System.out.println("Bye");
                        return;
                    }
                    default -> System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
    }

    private static void menuUsers() throws SQLException {
        while (true) {
            System.out.println("\n--- USERS ---");
            System.out.println("1) Read (afficher)");
            System.out.println("2) Update (modifier par id)");
            System.out.println("3) Delete (supprimer par id)");
            System.out.println("0) Retour");
            System.out.print("Choix: ");

            int c = Integer.parseInt(sc.nextLine().trim());
            if (c == 0) {
                return;
            }

            switch (c) {
                case 1 -> {
                    List<User> users = userCRUD.afficher();
                    users.forEach(System.out::println);
                }
                case 2 -> updateUserById();
                case 3 -> deleteUserById();
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private static void updateUserById() throws SQLException {
        System.out.println("\nUsers existants:");
        userCRUD.afficher().forEach(System.out::println);

        System.out.print("Donner l'ID du user a modifier: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        User u = userCRUD.findById(id);
        if (u == null) {
            System.out.println("Aucun user avec id=" + id);
            return;
        }

        System.out.print("Nouveau nom (actuel=" + u.getNom() + "): ");
        String nom = sc.nextLine().trim();
        if (!nom.isEmpty()) {
            u.setNom(nom);
        }

        System.out.print("Nouveau password (laisser vide pour garder): ");
        String pass = sc.nextLine().trim();
        if (!pass.isEmpty()) {
            u.setPassword(pass);
        }

        System.out.print("Nouveau role (ADMIN/ENTREPRENEUR/INVESTISSEUR) (actuel=" + u.getRole() + "): ");
        String roleStr = sc.nextLine().trim();
        if (!roleStr.isEmpty()) {
            u.setRole(Role.valueOf(roleStr));
        }

        userCRUD.modifier(u);
        System.out.println("User modifie: " + u);
    }

    private static void deleteUserById() throws SQLException {
        System.out.println("\nUsers existants:");
        userCRUD.afficher().forEach(System.out::println);

        System.out.print("Donner l'ID du user a supprimer: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        User u = userCRUD.findById(id);
        if (u == null) {
            System.out.println("Aucun user avec id=" + id);
            return;
        }

        System.out.print("Confirmer suppression de " + u + " ? (y/n): ");
        String ok = sc.nextLine().trim().toLowerCase();
        if (!ok.equals("y")) {
            System.out.println("Annule.");
            return;
        }

        userCRUD.supprimer(id);
        System.out.println("User supprime id=" + id);
    }

    private static void menuProjets() throws SQLException {
        while (true) {
            System.out.println("\n--- PROJETS ---");
            System.out.println("1) Read (afficher)");
            System.out.println("2) Update (modifier par id)");
            System.out.println("3) Delete (supprimer par id)");
            System.out.println("0) Retour");
            System.out.print("Choix: ");

            int c = Integer.parseInt(sc.nextLine().trim());
            if (c == 0) {
                return;
            }

            switch (c) {
                case 1 -> projetCRUD.afficher().forEach(System.out::println);
                case 2 -> updateProjetById();
                case 3 -> deleteProjetById();
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private static void updateProjetById() throws SQLException {
        System.out.println("\nProjets existants:");
        projetCRUD.afficher().forEach(System.out::println);

        System.out.print("ID projet a modifier: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        Projet p = projetCRUD.findById(id);
        if (p == null) {
            System.out.println("Aucun projet avec id=" + id);
            return;
        }

        System.out.print("Nouveau titre (actuel=" + p.getTitre() + "): ");
        String titre = sc.nextLine().trim();
        if (!titre.isEmpty()) {
            p.setTitre(titre);
        }

        System.out.print("Nouvelle description courte (laisser vide pour garder): ");
        String desc = sc.nextLine().trim();
        if (!desc.isEmpty()) {
            p.setDescription(desc);
        }

        System.out.print("Nouveau statut (EN_ATTENTE/VALIDE/REFUSE) (actuel=" + p.getStatut() + "): ");
        String st = sc.nextLine().trim();
        if (!st.isEmpty()) {
            p.setStatut(Statut.valueOf(st));
        }

        projetCRUD.modifier(p);
        System.out.println("Projet modifie: " + p);
    }

    private static void deleteProjetById() throws SQLException {
        System.out.println("\nProjets existants:");
        projetCRUD.afficher().forEach(System.out::println);

        System.out.print("ID projet a supprimer: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        Projet p = projetCRUD.findById(id);
        if (p == null) {
            System.out.println("Aucun projet avec id=" + id);
            return;
        }

        System.out.print("Confirmer suppression de " + p + " ? (y/n): ");
        String ok = sc.nextLine().trim().toLowerCase();
        if (!ok.equals("y")) {
            System.out.println("Annule.");
            return;
        }

        projetCRUD.supprimer(id);
        System.out.println("Projet supprime id=" + id);
    }

    private static void menuEvenements() throws SQLException {
        while (true) {
            System.out.println("\n--- EVENEMENTS ---");
            System.out.println("1) Read (afficher)");
            System.out.println("2) Update (modifier par id)");
            System.out.println("3) Delete (supprimer par id)");
            System.out.println("0) Retour");
            System.out.print("Choix: ");

            int c = Integer.parseInt(sc.nextLine().trim());
            if (c == 0) {
                return;
            }

            switch (c) {
                case 1 -> evenementCRUD.afficher().forEach(System.out::println);
                case 2 -> updateEvenementById();
                case 3 -> deleteEvenementById();
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private static void updateEvenementById() throws SQLException {
        System.out.println("\nEvenements existants:");
        evenementCRUD.afficher().forEach(System.out::println);

        System.out.print("ID event a modifier: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        Evenement ev = evenementCRUD.findById(id);
        if (ev == null) {
            System.out.println("Aucun event avec id=" + id);
            return;
        }

        System.out.print("Nouveau titre (actuel=" + ev.getTitre() + "): ");
        String titre = sc.nextLine().trim();
        if (!titre.isEmpty()) {
            ev.setTitre(titre);
        }

        System.out.print("Nouvelle description (laisser vide pour garder): ");
        String desc = sc.nextLine().trim();
        if (!desc.isEmpty()) {
            ev.setDescription(desc);
        }

        System.out.print("Nouveau statut (EN_ATTENTE/VALIDE/REFUSE) (actuel=" + ev.getStatut() + "): ");
        String st = sc.nextLine().trim();
        if (!st.isEmpty()) {
            ev.setStatut(Statut.valueOf(st));
        }

        evenementCRUD.modifier(ev);
        System.out.println("Event modifie: " + ev);
    }

    private static void deleteEvenementById() throws SQLException {
        System.out.println("\nEvenements existants:");
        evenementCRUD.afficher().forEach(System.out::println);

        System.out.print("ID event a supprimer: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        Evenement ev = evenementCRUD.findById(id);
        if (ev == null) {
            System.out.println("Aucun event avec id=" + id);
            return;
        }

        System.out.print("Confirmer suppression de " + ev + " ? (y/n): ");
        String ok = sc.nextLine().trim().toLowerCase();
        if (!ok.equals("y")) {
            System.out.println("Annule.");
            return;
        }

        evenementCRUD.supprimer(id);
        System.out.println("Event supprime id=" + id);
    }

    private static void seedDemoData() throws SQLException {
        String unique = String.valueOf(System.currentTimeMillis());
        String adminEmail = "admin" + unique + "@mail.com";

        userCRUD.ajouter(new User("AdminSeed", adminEmail, "123", Role.ADMIN));

        User existingEntrepreneurUser = userCRUD.afficher().stream()
                .filter(u -> u.getRole() == Role.ENTREPRENEUR)
                .findFirst()
                .orElse(null);
        if (existingEntrepreneurUser == null) {
            System.out.println("Aucun entrepreneur existant avec profil lie. Seed projet/evenement ignore.");
            return;
        }

        Integer entrepreneurProfileId = projetCRUD.findEntrepreneurProfileIdByUserId(existingEntrepreneurUser.getId());
        if (entrepreneurProfileId == null) {
            System.out.println("Aucun profil_entrepreneur pour user id=" + existingEntrepreneurUser.getId());
            return;
        }

        Projet projet = new Projet("ProjetSeed", "desc courte", Statut.EN_ATTENTE, entrepreneurProfileId);
        projet.setSecteur("Technologie");
        projet.setDescriptionLongue("Description longue generee pour les tests CLI.");
        projet.setObjectifTnd(new BigDecimal("10000.00"));
        projet.setDureeCampagneJours(45);
        projet.setModeRemboursement("STANDARD");
        projetCRUD.ajouter(projet);

        Evenement event = new Evenement("EventSeed", "desc", Statut.EN_ATTENTE, existingEntrepreneurUser.getId(), LocalDate.now().plusDays(7));
        event.setProjectId(projet.getId());
        event.setDateDebut(LocalDateTime.now().plusDays(7));
        event.setDateFin(LocalDateTime.now().plusDays(7).plusHours(2));
        event.setMode("PRESENTIEL");
        event.setLieu("Tunis");
        evenementCRUD.ajouter(event);

        System.out.println("Demo data ajoute.");
    }
}
