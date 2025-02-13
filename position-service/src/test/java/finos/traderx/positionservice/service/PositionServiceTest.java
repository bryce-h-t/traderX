package finos.traderx.positionservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import finos.traderx.positionservice.model.Position;
import finos.traderx.positionservice.repository.PositionRepository;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;
    
    @InjectMocks
    private PositionService positionService;

    private Position testPosition;

    @BeforeEach
    void setUp() {
        testPosition = new Position();
        testPosition.setAccountId(1);
        testPosition.setSecurity("AAPL");
        testPosition.setQuantity(100);
    }

    @Test
    void getAllPositions_ShouldReturnAllPositions() {
        List<Position> positions = Arrays.asList(testPosition);
        when(positionRepository.findAll()).thenReturn(positions);
        
        List<Position> result = positionService.getAllPositions();
        
        assertEquals(1, result.size());
        assertEquals(testPosition.getAccountId(), result.get(0).getAccountId());
        assertEquals(testPosition.getSecurity(), result.get(0).getSecurity());
        assertEquals(testPosition.getQuantity(), result.get(0).getQuantity());
    }

    @Test
    void getAllPositions_WhenEmpty_ShouldReturnEmptyList() {
        when(positionRepository.findAll()).thenReturn(Arrays.asList());
        
        List<Position> result = positionService.getAllPositions();
        
        assertTrue(result.isEmpty());
    }

    @Test
    void getPositionsByAccountID_ShouldReturnPositionsForAccount() {
        List<Position> positions = Arrays.asList(testPosition);
        when(positionRepository.findByAccountId(1)).thenReturn(positions);
        
        List<Position> result = positionService.getPositionsByAccountID(1);
        
        assertEquals(1, result.size());
        assertEquals(testPosition.getAccountId(), result.get(0).getAccountId());
    }

    @Test
    void getPositionsByAccountID_WhenNoPositions_ShouldReturnEmptyList() {
        when(positionRepository.findByAccountId(999)).thenReturn(Arrays.asList());
        
        List<Position> result = positionService.getPositionsByAccountID(999);
        
        assertTrue(result.isEmpty());
    }
}
