package finos.traderx.positionservice.service;

import finos.traderx.positionservice.model.Position;
import finos.traderx.positionservice.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {
    @InjectMocks
    private PositionService positionService;

    @Mock
    private PositionRepository positionRepository;

    private Position createTestPosition(Integer accountId, String security, Integer quantity) {
        Position position = new Position();
        position.setAccountId(accountId);
        position.setSecurity(security);
        position.setQuantity(quantity);
        position.setUpdated(new Date());
        return position;
    }

    @Test
    void getAllPositions_ShouldReturnAllPositions() {
        // Arrange
        Position pos1 = createTestPosition(1, "AAPL", 100);
        Position pos2 = createTestPosition(2, "GOOGL", 50);
        when(positionRepository.findAll()).thenReturn(Arrays.asList(pos1, pos2));

        // Act
        List<Position> positions = positionService.getAllPositions();

        // Assert
        assertNotNull(positions);
        assertEquals(2, positions.size());
        verify(positionRepository).findAll();
    }

    @Test
    void getPositionsByAccountID_ShouldReturnPositionsForAccount() {
        // Arrange
        Integer accountId = 1;
        Position pos1 = createTestPosition(accountId, "AAPL", 100);
        Position pos2 = createTestPosition(accountId, "GOOGL", 50);
        when(positionRepository.findByAccountId(accountId)).thenReturn(Arrays.asList(pos1, pos2));

        // Act
        List<Position> positions = positionService.getPositionsByAccountID(accountId);

        // Assert
        assertNotNull(positions);
        assertEquals(2, positions.size());
        positions.forEach(position -> assertEquals(accountId, position.getAccountId()));
        verify(positionRepository).findByAccountId(accountId);
    }

    @Test
    void getPositionsByAccountID_ShouldReturnEmptyListForNonExistentAccount() {
        // Arrange
        Integer accountId = 999;
        when(positionRepository.findByAccountId(accountId)).thenReturn(Arrays.asList());

        // Act
        List<Position> positions = positionService.getPositionsByAccountID(accountId);

        // Assert
        assertNotNull(positions);
        assertTrue(positions.isEmpty());
        verify(positionRepository).findByAccountId(accountId);
    }
}
