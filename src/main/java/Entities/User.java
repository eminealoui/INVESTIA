package Entities;

public class User {
    private int id;
    private String nom;
    private String email;
    private String password;
    private Role role;
    private boolean active = true;

    public User() {}

    public User(int id, String nom, String email, String password, Role role) {
        this.id = id; this.nom = nom; this.email = email; this.password = password; this.role = role;
    }

    public User(String nom, String email, String password, Role role) {
        this.nom = nom; this.email = email; this.password = password; this.role = role;
    }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + id + ", nom='" + nom + "', email='" + email + "', role=" + role + "}";
    }


}
