package finos.traderx.positionservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import finos.traderx.positionservice.model.Position;
import finos.traderx.positionservice.repository.PositionRepository;

@SpringBootTest
class PositionServiceTest {

    @MockBean
    private PositionRepository positionRepository;

    private PositionService positionService;

    @BeforeEach
    void setUp() {
        positionService = new PositionService();
        positionService.positionRepository = positionRepository;
    }

    private Position createTestPosition(Integer accountId, String security, Integer quantity) {
        Position position = new Position();
        position.setAccountId(accountId);
        position.setSecurity(security);
        position.setQuantity(quantity);
        position.setUpdated(new Date());
        return position;
    }

    @Test
    void getAllPositions_EmptyRepository_ReturnsEmptyList() {
        when(positionRepository.findAll()).thenReturn(Collections.emptyList());
        List<Position> positions = positionService.getAllPositions();
        assertTrue(positions.isEmpty());
    }

    @Test
    void getAllPositions_MultiplePositions_ReturnsAllPositions() {
        List<Position> expectedPositions = Arrays.asList(
            createTestPosition(1, "AAPL", 100),
            createTestPosition(1, "GOOGL", 50),
            createTestPosition(2, "AAPL", 200)
        );
        when(positionRepository.findAll()).thenReturn(expectedPositions);
        
        List<Position> positions = positionService.getAllPositions();
        
        assertEquals(expectedPositions.size(), positions.size());
        assertEquals(expectedPositions, positions);
    }

    @Test
    void getPositionsByAccountID_ValidAccount_ReturnsPositions() {
        Integer accountId = 1;
        List<Position> expectedPositions = Arrays.asList(
            createTestPosition(accountId, "AAPL", 100),
            createTestPosition(accountId, "GOOGL", 50)
        );
        when(positionRepository.findByAccountId(accountId)).thenReturn(expectedPositions);
        
        List<Position> positions = positionService.getPositionsByAccountID(accountId);
        
        assertEquals(expectedPositions.size(), positions.size());
        assertEquals(expectedPositions, positions);
    }

    @Test
    void getPositionsByAccountID_NonExistentAccount_ReturnsEmptyList() {
        Integer accountId = 999;
        when(positionRepository.findByAccountId(accountId)).thenReturn(Collections.emptyList());
        
        List<Position> positions = positionService.getPositionsByAccountID(accountId);
        
        assertTrue(positions.isEmpty());
    }

    @Test
    void getPositionsByAccountID_InvalidAccountId_ReturnsEmptyList() {
        Integer accountId = -1;
        when(positionRepository.findByAccountId(accountId)).thenReturn(Collections.emptyList());
        
        List<Position> positions = positionService.getPositionsByAccountID(accountId);
        
        assertTrue(positions.isEmpty());
    }

    @Test
    void getAllPositions_RepositoryException_ReturnsEmptyList() {
        when(positionRepository.findAll()).thenThrow(new RuntimeException("Database error"));
        
        List<Position> positions = positionService.getAllPositions();
        
        assertTrue(positions.isEmpty());
    }

    @Test
    void getPositionsByAccountID_RepositoryException_ReturnsEmptyList() {
        Integer accountId = 1;
        when(positionRepository.findByAccountId(accountId)).thenThrow(new RuntimeException("Database error"));
        
        List<Position> positions = positionService.getPositionsByAccountID(accountId);
        
        assertTrue(positions.isEmpty());
    }
}
