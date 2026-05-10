package Services;

import Entities.Decision;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModerationActionCRUDTest {

    @Test
    void moderateProjetWritesAuditAndUpdatesStatusInTransaction() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement insertStatement = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        ProjetCRUD projetCRUD = mock(ProjetCRUD.class);
        EvenementCRUD evenementCRUD = mock(EvenementCRUD.class);
        when(connection.prepareStatement(contains("INSERT INTO moderation_actions"), anyInt())).thenReturn(insertStatement);
        when(insertStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(11);
        ModerationActionCRUD crud = new ModerationActionCRUD(connection, projetCRUD, evenementCRUD);

        crud.moderateProjet(1, 5, Decision.VALIDER, "ok");

        verify(connection).setAutoCommit(false);
        verify(insertStatement).executeUpdate();
        verify(projetCRUD).updateStatut(5, Entities.Statut.VALIDE);
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }
}
