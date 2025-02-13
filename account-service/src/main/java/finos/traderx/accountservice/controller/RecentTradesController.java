package finos.traderx.accountservice.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import finos.traderx.accountservice.exceptions.ResourceNotFoundException;
import finos.traderx.accountservice.model.Account;
import finos.traderx.accountservice.model.Trade;

@RestController
@RequestMapping(value="/account", produces="application/json")
public class RecentTradesController {
    @Value("${position.service.url}")
    private String positionServiceUrl;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    @GetMapping("/{id}/trades")
    public ResponseEntity<?> getRecentTrades(
        @PathVariable int id,
        @RequestParam(defaultValue = "7") int days
    ) {
        if (days < 1 || days > 365) {
            return ResponseEntity.badRequest()
                .body(new Error("BAD_REQUEST", "Days parameter must be between 1 and 365"));
        }
        
        try {
            // First verify account exists
            String accountUrl = "/account/" + id;
            restTemplate.getForEntity(accountUrl, Account.class);
            
            // Get trades from position service
            String tradesUrl = positionServiceUrl + "/trades/" + id;
            ResponseEntity<List<Trade>> response = restTemplate.exchange(
                tradesUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Trade>>() {}
            );
            
            // Filter trades by date
            LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
            List<Trade> recentTrades = response.getBody().stream()
                .filter(t -> t.getCreated().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .isAfter(cutoff))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(recentTrades);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(404)
                .body(new Error("NOT_FOUND", "Account not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new Error("INTERNAL_ERROR", "Internal server error"));
        }
    }
}
