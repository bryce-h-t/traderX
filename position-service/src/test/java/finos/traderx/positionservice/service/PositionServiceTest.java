package finos.traderx.positionservice.service;

import finos.traderx.positionservice.model.Position;
import finos.traderx.positionservice.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private PositionService positionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPositions() {
        // Arrange
        Position position1 = new Position();
        position1.setAccountId(1);
        position1.setSecurity("AAPL");
        position1.setQuantity(100);
        position1.setUpdated(new Date());

        Position position2 = new Position();
        position2.setAccountId(2);
        position2.setSecurity("GOOGL");
        position2.setQuantity(50);
        position2.setUpdated(new Date());

        List<Position> expectedPositions = Arrays.asList(position1, position2);
        when(positionRepository.findAll()).thenReturn(expectedPositions);

        // Act
        List<Position> actualPositions = positionService.getAllPositions();

        // Assert
        assertEquals(expectedPositions.size(), actualPositions.size());
        assertEquals(expectedPositions, actualPositions);
        verify(positionRepository, times(1)).findAll();
    }

    @Test
    void testGetPositionsByAccountID() {
        // Arrange
        int accountId = 1;
        Position position = new Position();
        position.setAccountId(accountId);
        position.setSecurity("AAPL");
        position.setQuantity(100);
        position.setUpdated(new Date());

        List<Position> expectedPositions = Arrays.asList(position);
        when(positionRepository.findByAccountId(accountId)).thenReturn(expectedPositions);

        // Act
        List<Position> actualPositions = positionService.getPositionsByAccountID(accountId);

        // Assert
        assertEquals(expectedPositions.size(), actualPositions.size());
        assertEquals(expectedPositions, actualPositions);
        verify(positionRepository, times(1)).findByAccountId(accountId);
    }
}
