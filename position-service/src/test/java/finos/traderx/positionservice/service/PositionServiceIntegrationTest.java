package finos.traderx.positionservice.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import finos.traderx.positionservice.model.Position;
import finos.traderx.positionservice.repository.PositionRepository;

@SpringBootTest
@TestPropertySource(locations = "/test-application.properties")
@Transactional
class PositionServiceIntegrationTest {

    @Autowired
    private PositionService positionService;

    @Autowired
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
    void getAllPositions_WithSavedPositions_ReturnsAllPositions() {
        // Arrange
        Position position1 = createTestPosition(1, "AAPL", 100);
        Position position2 = createTestPosition(1, "GOOGL", 50);
        positionRepository.save(position1);
        positionRepository.save(position2);

        // Act
        List<Position> positions = positionService.getAllPositions();

        // Assert
        assertEquals(2, positions.size());
        assertTrue(positions.stream().anyMatch(p -> 
            p.getAccountId().equals(1) && 
            p.getSecurity().equals("AAPL") && 
            p.getQuantity().equals(100)));
        assertTrue(positions.stream().anyMatch(p -> 
            p.getAccountId().equals(1) && 
            p.getSecurity().equals("GOOGL") && 
            p.getQuantity().equals(50)));
    }

    @Test
    void getPositionsByAccountID_WithSavedPositions_ReturnsAccountPositions() {
        // Arrange
        Position position1 = createTestPosition(1, "AAPL", 100);
        Position position2 = createTestPosition(1, "GOOGL", 50);
        Position position3 = createTestPosition(2, "MSFT", 75);
        positionRepository.save(position1);
        positionRepository.save(position2);
        positionRepository.save(position3);

        // Act
        List<Position> positions = positionService.getPositionsByAccountID(1);

        // Assert
        assertEquals(2, positions.size());
        assertTrue(positions.stream().allMatch(p -> p.getAccountId().equals(1)));
        assertTrue(positions.stream().anyMatch(p -> p.getSecurity().equals("AAPL")));
        assertTrue(positions.stream().anyMatch(p -> p.getSecurity().equals("GOOGL")));
    }

    @Test
    void getPositionsByAccountID_WithNonExistentAccount_ReturnsEmptyList() {
        // Arrange
        Position position = createTestPosition(1, "AAPL", 100);
        positionRepository.save(position);

        // Act
        List<Position> positions = positionService.getPositionsByAccountID(999);

        // Assert
        assertTrue(positions.isEmpty());
    }
}
