package finos.traderx.positionservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import finos.traderx.positionservice.model.Position;
import finos.traderx.positionservice.service.PositionService;

@SpringBootTest
@TestPropertySource(locations = "/test-application.properties")
class PositionServiceTest {

    @Autowired
    PositionService positionService;

    @Test
    void contextLoads() {
    }

    @Test
    void getAllPositionsReturnsEmptyList() {
        List<Position> positions = positionService.getAllPositions();
        assertNotNull(positions);
    }

    @Test
    void getPositionsByAccountIDReturnsEmptyList() {
        List<Position> positions = positionService.getPositionsByAccountID(1);
        assertNotNull(positions);
    }

    @Test
    void getPositionsByAccountIDWithValidData() {
        // Create test position
        Position position = new Position();
        position.setAccountId(1);
        position.setSecurity("AAPL");
        position.setQuantity(100);
        position.setUpdated(new Date());
        
        // Save position using repository
        positionService.positionRepository.save(position);

        // Test retrieval
        List<Position> positions = positionService.getPositionsByAccountID(1);
        assertNotNull(positions);
        assertEquals(1, positions.size());
        assertEquals("AAPL", positions.get(0).getSecurity());
        assertEquals(100, positions.get(0).getQuantity());
    }
}
