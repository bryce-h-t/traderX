# Trade Service API Documentation

## Overview
The Trade Service is responsible for submitting trade orders for processing in the TraderX system. It validates the trade details against reference data and account services before publishing the trade for further processing.

## Base URL
All endpoints are relative to the base trade service URL.

## Endpoints

### Submit Trade Order
Submit a new trade order for processing.

**Endpoint:** `POST /trade/`

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "id": "ABC-123-XYZ",
  "security": "ADBE",
  "quantity": 100,
  "accountId": 22214,
  "side": "Buy"
}
```

**Request Fields:**
- `id` (string): Unique identifier for the trade order
- `security` (string): The ticker symbol of the security to trade
- `quantity` (integer): Number of shares to trade
- `accountId` (integer): The account identifier for the trade
- `side` (string, enum): Trade direction
  - Allowed values: `Buy`, `Sell`

**Response:**
```json
{
  "id": "ABC-123-XYZ",
  "state": "New",
  "security": "ADBE",
  "quantity": 100,
  "accountId": 22214,
  "side": "Buy"
}
```

**Response Fields:**
- All request fields plus:
- `state` (string): Current state of the trade order

**Status Codes:**
- `200 OK`: Trade order successfully submitted
- `404 Not Found`: Invalid security symbol or account ID
- `500 Internal Server Error`: Error publishing trade order

**Validation:**
The service performs two key validations before accepting a trade:
1. Validates the security symbol against the reference data service
2. Validates the account ID against the account service

If either validation fails, a 404 error is returned with an appropriate error message.

## Error Handling

### Resource Not Found Error
```json
{
  "timestamp": "2024-02-12T02:32:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "INVALID_SYMBOL not found in Reference data service.",
  "path": "/trade/"
}
```

### Publishing Error
```json
{
  "timestamp": "2024-02-12T02:32:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Failed to publish trade order",
  "path": "/trade/"
}
```

## Dependencies
The Trade Service depends on:
- Reference Data Service: For validating security symbols
- Account Service: For validating account IDs
- Trade Feed: For publishing validated trades for further processing
