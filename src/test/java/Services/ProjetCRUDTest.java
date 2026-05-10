package Services;

import Entities.Projet;
import Entities.Statut;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjetCRUDTest {

    @Test
    void ajouterResolvesSchemaAndStoresRequiredFields() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement insertStatement = mock(PreparedStatement.class);
        PreparedStatement lookupStatement = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        ResultSet profileResult = mock(ResultSet.class);

        when(connection.prepareStatement(org.mockito.ArgumentMatchers.contains("INSERT INTO projet"), anyInt())).thenReturn(insertStatement);
        when(connection.prepareStatement(org.mockito.ArgumentMatchers.contains("SELECT id_entrepreneur FROM profil_entrepreneur WHERE id_entrepreneur"))).thenReturn(lookupStatement);
        when(lookupStatement.executeQuery()).thenReturn(profileResult);
        when(profileResult.next()).thenReturn(true);
        when(profileResult.getInt("id_entrepreneur")).thenReturn(3);
        when(insertStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(9);

        ProjetCRUD crud = new ProjetCRUD(connection);
        Projet projet = new Projet("Projet", "Desc", Statut.EN_ATTENTE, 3);
        projet.setSecteur("Tech");
        projet.setDescriptionLongue("Description longue");
        projet.setObjectifTnd(new BigDecimal("25000.00"));
        projet.setDureeCampagneJours(60);
        projet.setModeRemboursement("STANDARD");

        crud.ajouter(projet);

        verify(insertStatement).setString(1, "EN_ATTENTE");
        verify(insertStatement).setString(3, "Tech");
        verify(insertStatement).setBigDecimal(7, new BigDecimal("25000.00"));
        verify(insertStatement).setInt(16, 3);
        assertEquals(9, projet.getId());
    }
}
