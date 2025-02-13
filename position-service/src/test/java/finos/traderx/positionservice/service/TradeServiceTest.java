package finos.traderx.positionservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import finos.traderx.positionservice.model.Trade;
import finos.traderx.positionservice.repository.TradeRepository;

@SpringBootTest
@TestPropertySource(locations = "/test-application.properties")
class TradeServiceTest {

    @Autowired
    private TradeService tradeService;

    @MockBean
    private TradeRepository tradeRepository;

    @Test
    void getAllTrades_ReturnsTrades() {
        // Arrange
        Trade trade = new Trade();
        trade.setId("trade1");
        trade.setAccountId(1);
        trade.setSecurity("AAPL");
        trade.setSide("BUY");
        trade.setState("NEW");
        trade.setQuantity(100);
        trade.setCreated(new Date());
        trade.setUpdated(new Date());

        when(tradeRepository.findAll()).thenReturn(Arrays.asList(trade));

        // Act
        List<Trade> trades = tradeService.getAllTrades();

        // Assert
        assertEquals(1, trades.size());
        assertEquals("trade1", trades.get(0).getId());
        assertEquals(1, trades.get(0).getAccountId());
        assertEquals("AAPL", trades.get(0).getSecurity());
        assertEquals("BUY", trades.get(0).getSide());
        assertEquals("NEW", trades.get(0).getState());
        assertEquals(100, trades.get(0).getQuantity());
    }

    @Test
    void getAllTrades_ReturnsEmptyList_WhenNoTrades() {
        // Arrange
        when(tradeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Trade> trades = tradeService.getAllTrades();

        // Assert
        assertEquals(0, trades.size());
    }

    @Test
    void getTradesByAccountID_ReturnsTrades() {
        // Arrange
        Trade trade = new Trade();
        trade.setId("trade1");
        trade.setAccountId(1);
        trade.setSecurity("AAPL");
        trade.setSide("BUY");
        trade.setState("NEW");
        trade.setQuantity(100);
        trade.setCreated(new Date());
        trade.setUpdated(new Date());

        when(tradeRepository.findByAccountId(1)).thenReturn(Arrays.asList(trade));

        // Act
        List<Trade> trades = tradeService.getTradesByAccountID(1);

        // Assert
        assertEquals(1, trades.size());
        assertEquals("trade1", trades.get(0).getId());
        assertEquals(1, trades.get(0).getAccountId());
        assertEquals("AAPL", trades.get(0).getSecurity());
        assertEquals("BUY", trades.get(0).getSide());
        assertEquals("NEW", trades.get(0).getState());
        assertEquals(100, trades.get(0).getQuantity());
    }

    @Test
    void getTradesByAccountID_ReturnsEmptyList_WhenNoTradesForAccount() {
        // Arrange
        when(tradeRepository.findByAccountId(999)).thenReturn(Collections.emptyList());

        // Act
        List<Trade> trades = tradeService.getTradesByAccountID(999);

        // Assert
        assertEquals(0, trades.size());
    }
}
