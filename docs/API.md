# TraderX API Documentation

## Overview
TraderX exposes several microservices that work together to provide trading functionality. This document describes the REST APIs and WebSocket interfaces available in each service.

## Services

### Account Service
Base URL: `/account`

#### Endpoints

##### Get Account by ID
- **GET** `/account/{id}`
- **Response**: Account object
- **Status Codes**:
  - 200: Success
  - 404: Account not found
  - 500: Server error

##### Create Account
- **POST** `/account/`
- **Body**: Account object
- **Response**: Created account
- **Status Codes**:
  - 200: Success
  - 500: Server error

##### Update Account
- **PUT** `/account/`
- **Body**: Account object
- **Response**: Updated account
- **Status Codes**:
  - 200: Success
  - 404: Account not found
  - 500: Server error

##### Get All Accounts
- **GET** `/account/`
- **Response**: Array of Account objects
- **Status Codes**:
  - 200: Success
  - 500: Server error

### Position Service
Base URL: `/positions`

#### Endpoints

##### Get Positions by Account ID
- **GET** `/positions/{accountId}`
- **Response**: Array of Position objects
- **Status Codes**:
  - 200: Success
  - 500: Server error

##### Get All Positions
- **GET** `/positions/`
- **Response**: Array of Position objects
- **Status Codes**:
  - 200: Success
  - 500: Server error

### Trade Service
Base URL: `/trade`

#### Endpoints

##### Submit Trade Order
- **POST** `/trade/`
- **Body**: TradeOrder object
- **Response**: TradeOrder object
- **Status Codes**:
  - 200: Success
  - 404: Invalid security or account
  - 500: Server error

### Reference Data Service
Base URL: `/stocks`

#### Endpoints

##### Get All Stocks
- **GET** `/stocks`
- **Response**: Array of Stock objects
- **Status Codes**:
  - 200: Success
  - 500: Server error

##### Get Stock by Ticker
- **GET** `/stocks/{ticker}`
- **Response**: Stock object
- **Status Codes**:
  - 200: Success
  - 404: Stock not found
  - 500: Server error

### People Service
Base URL: `/people`

#### Endpoints

##### Get Person
- **GET** `/people/GetPerson`
- **Query Parameters**:
  - logonId (optional)
  - employeeId (optional)
- **Response**: Person object
- **Status Codes**:
  - 200: Person found
  - 404: Person not found
  - 400: Invalid request

##### Get Matching People
- **GET** `/people/GetMatchingPeople`
- **Query Parameters**:
  - searchText
- **Response**: Array of Person objects
- **Status Codes**:
  - 200: People found
  - 404: No matches found
  - 400: Invalid request

##### Validate Person
- **GET** `/people/ValidatePerson`
- **Query Parameters**:
  - logonId (optional)
  - employeeId (optional)
- **Response**: No content
- **Status Codes**:
  - 200: Person is valid
  - 404: Person not found
  - 400: Invalid request

### Trade Feed Service
WebSocket endpoint: `ws://localhost:18086`

#### Events

##### Subscribe
```javascript
socket.emit('subscribe', topic);
```
Topics:
- `/*`: All messages
- `/trades`: Trade-related messages
- `/chat`: Chat messages
- `/accounts/{accountId}/positions`: Position updates for specific account

##### Publish
```javascript
socket.emit('publish', {
  topic: string,
  payload: any
});
```

##### Message Format
```javascript
{
  type: string,
  from: string,
  topic: string,
  date: number,
  payload: any
}
```

## Data Models

### Account
```json
{
  "id": "integer",
  "displayName": "string"
}
```

### Position
```json
{
  "accountId": "integer",
  "security": "string",
  "quantity": "integer",
  "updated": "date"
}
```

### TradeOrder
```json
{
  "id": "string",
  "state": "string",
  "security": "string",
  "quantity": "integer",
  "accountId": "integer",
  "side": "string"
}
```

### Stock
```typescript
{
  ticker: string;
  companyName: string;
}
```

### Person
```json
{
  "logonId": "string",
  "employeeId": "string",
  "fullName": "string"
}
```
