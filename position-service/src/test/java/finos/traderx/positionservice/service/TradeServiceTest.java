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

import finos.traderx.positionservice.model.Trade;
import finos.traderx.positionservice.repository.TradeRepository;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;
    
    @InjectMocks
    private TradeService tradeService;

    private Trade testTrade;

    @BeforeEach
    void setUp() {
        testTrade = new Trade();
        testTrade.setId("trade1");
        testTrade.setAccountId(1);
        testTrade.setSecurity("AAPL");
        testTrade.setQuantity(100);
        testTrade.setSide("BUY");
        testTrade.setState("NEW");
    }

    @Test
    void getAllTrades_ShouldReturnAllTrades() {
        List<Trade> trades = Arrays.asList(testTrade);
        when(tradeRepository.findAll()).thenReturn(trades);
        
        List<Trade> result = tradeService.getAllTrades();
        
        assertEquals(1, result.size());
        assertEquals(testTrade.getId(), result.get(0).getId());
        assertEquals(testTrade.getAccountId(), result.get(0).getAccountId());
    }

    @Test
    void getAllTrades_WhenEmpty_ShouldReturnEmptyList() {
        when(tradeRepository.findAll()).thenReturn(Arrays.asList());
        
        List<Trade> result = tradeService.getAllTrades();
        
        assertTrue(result.isEmpty());
    }

    @Test
    void getTradesByAccountID_ShouldReturnTradesForAccount() {
        List<Trade> trades = Arrays.asList(testTrade);
        when(tradeRepository.findByAccountId(1)).thenReturn(trades);
        
        List<Trade> result = tradeService.getTradesByAccountID(1);
        
        assertEquals(1, result.size());
        assertEquals(testTrade.getAccountId(), result.get(0).getAccountId());
    }

    @Test
    void getTradesByAccountID_WhenNoTrades_ShouldReturnEmptyList() {
        when(tradeRepository.findByAccountId(999)).thenReturn(Arrays.asList());
        
        List<Trade> result = tradeService.getTradesByAccountID(999);
        
        assertTrue(result.isEmpty());
    }
}
