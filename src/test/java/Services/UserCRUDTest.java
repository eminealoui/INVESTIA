package Services;

import Entities.Role;
import Entities.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserCRUDTest {

    @Test
    void ajouterUsesUserTableAndMapsGeneratedId() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(42);

        UserCRUD crud = new UserCRUD(connection);
        User user = new User("Alice", "alice@test.com", "secret", Role.ADMIN);
        user.setStatutVerification("VERIFIE");

        crud.ajouter(user);

        verify(connection).prepareStatement(org.mockito.ArgumentMatchers.contains("INSERT INTO user"), anyInt());
        verify(preparedStatement).setString(1, "alice@test.com");
        verify(preparedStatement).setString(4, "VERIFIE");
        verify(preparedStatement).setBoolean(13, true);
        verify(preparedStatement).executeUpdate();
        assertEquals(42, user.getId());
    }

    @Test
    void findByEmailMapsExtendedUserFields() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(7);
        when(resultSet.getString("email")).thenReturn("admin@investia.test");
        when(resultSet.getString("password")).thenReturn("hash");
        when(resultSet.getString("role")).thenReturn("ADMIN");
        when(resultSet.getString("statut_verification")).thenReturn("VERIFIE");
        when(resultSet.getString("nom")).thenReturn("Investia");
        when(resultSet.getString("prenom")).thenReturn("Admin");
        when(resultSet.getString("telephone")).thenReturn("20000001");
        when(resultSet.getString("cin")).thenReturn("10000001");
        when(resultSet.getBoolean("active")).thenReturn(true);

        UserCRUD crud = new UserCRUD(connection);
        User user = crud.findByEmail("admin@investia.test");

        assertEquals(7, user.getId());
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals("Admin", user.getPrenom());
        assertTrue(user.isActive());
    }
}
