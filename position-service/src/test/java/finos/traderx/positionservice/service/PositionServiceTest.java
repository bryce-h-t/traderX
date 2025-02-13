package finos.traderx.positionservice.service;

import finos.traderx.positionservice.model.Position;
import finos.traderx.positionservice.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.config.location=classpath:application-test.properties")
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    private PositionService positionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        positionService = new PositionService();
        positionService.positionRepository = positionRepository;
    }

    @Test
    void getAllPositions_ShouldReturnAllPositions() {
        // Arrange
        List<Position> expectedPositions = new ArrayList<>();
        Position position1 = new Position();
        position1.setAccountId(1);
        Position position2 = new Position();
        position2.setAccountId(2);
        expectedPositions.add(position1);
        expectedPositions.add(position2);
        
        when(positionRepository.findAll()).thenReturn(expectedPositions);

        // Act
        List<Position> actualPositions = positionService.getAllPositions();

        // Assert
        assertNotNull(actualPositions);
        assertEquals(2, actualPositions.size());
        assertEquals(expectedPositions, actualPositions);
    }

    @Test
    void getPositionsByAccountID_ShouldReturnPositionsForSpecificAccount() {
        // Arrange
        int accountId = 1;
        List<Position> expectedPositions = new ArrayList<>();
        Position position = new Position();
        position.setAccountId(accountId);
        expectedPositions.add(position);
        
        when(positionRepository.findByAccountId(accountId)).thenReturn(expectedPositions);

        // Act
        List<Position> actualPositions = positionService.getPositionsByAccountID(accountId);

        // Assert
        assertNotNull(actualPositions);
        assertEquals(1, actualPositions.size());
        assertEquals(accountId, actualPositions.get(0).getAccountId());
    }
}
