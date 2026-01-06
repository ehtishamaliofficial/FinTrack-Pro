# Authentication System Guide

## Overview
FinTrack Pro uses a secure JWT-based authentication system with refresh tokens, account locking, and comprehensive security features.

## Features

### ✅ Implemented Features
- **JWT Access Tokens** - Short-lived tokens (1 hour) for API access
- **Refresh Tokens** - Long-lived tokens (7 days) for obtaining new access tokens
- **Account Locking** - Automatic lock after 5 failed login attempts (30 minutes)
- **Email Verification** - Required before login
- **Password Security** - BCrypt hashing with strength 12
- **Session Management** - Track and revoke individual or all sessions
- **IP & User Agent Tracking** - Monitor login locations and devices
- **Automatic Token Cleanup** - Daily cleanup of expired tokens

## API Endpoints

### 1. Register
**Endpoint:** `POST /api/v1/auth/register`

**Request:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Registration successful. Please check your email to verify your account.",
  "timestamp": "2025-11-05T09:00:00",
  "path": "/api/v1/auth/register"
}
```

**Notes:**
- Password must meet complexity requirements
- Verification email sent automatically
- User cannot login until email is verified

---

### 2. Verify Email
**Endpoint:** `GET /api/v1/auth/verify-email?token={token}`

**Response:**
```json
{
  "success": true,
  "message": "Email verified successfully. Welcome to FinTrack Pro!",
  "timestamp": "2025-11-05T09:05:00",
  "path": "/api/v1/auth/verify-email"
}
```

**Notes:**
- Token expires after 24 hours
- Welcome email sent after successful verification
- User can now login

---

### 3. Login
**Endpoint:** `POST /api/v1/auth/login`

**Request:**
```json
{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "user": {
      "id": 1,
      "username": "johndoe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "emailVerified": true
    }
  },
  "timestamp": "2025-11-05T09:10:00",
  "path": "/api/v1/auth/login"
}
```

**Security Features:**
- Tracks failed login attempts
- Locks account after 5 failed attempts for 30 minutes
- Records IP address and user agent
- Requires email verification
- Resets failed attempts on successful login

**Error Responses:**
```json
// Invalid credentials
{
  "success": false,
  "message": "Invalid email or password",
  "timestamp": "2025-11-05T09:10:00",
  "path": "/api/v1/auth/login"
}

// Account locked
{
  "success": false,
  "message": "Account is locked due to too many failed login attempts. Please try again in 25 minutes",
  "timestamp": "2025-11-05T09:10:00",
  "path": "/api/v1/auth/login"
}

// Email not verified
{
  "success": false,
  "message": "Please verify your email address before logging in",
  "timestamp": "2025-11-05T09:10:00",
  "path": "/api/v1/auth/login"
}
```

---

### 4. Refresh Token
**Endpoint:** `POST /api/v1/auth/refresh`

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "user": {
      "id": 1,
      "username": "johndoe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "emailVerified": true
    }
  },
  "timestamp": "2025-11-05T10:10:00",
  "path": "/api/v1/auth/refresh"
}
```

**Notes:**
- Refresh token remains the same
- New access token is generated
- Refresh token expires after 7 days

---

### 5. Logout
**Endpoint:** `POST /api/v1/auth/logout`

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "success": true,
  "message": "Logged out successfully",
  "timestamp": "2025-11-05T11:00:00",
  "path": "/api/v1/auth/logout"
}
```

**Notes:**
- Revokes the refresh token
- Access token remains valid until expiration
- User must login again to get new tokens

---

### 6. Logout All Sessions
**Endpoint:** `POST /api/v1/auth/logout-all`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response:**
```json
{
  "success": true,
  "message": "All sessions logged out successfully",
  "timestamp": "2025-11-05T11:00:00",
  "path": "/api/v1/auth/logout-all"
}
```

**Notes:**
- Requires authentication
- Revokes all refresh tokens for the user
- Useful when account is compromised
- All devices will need to login again

---

## Using JWT Tokens

### Access Token
Include in the `Authorization` header for protected endpoints:

```http
GET /api/v1/protected-endpoint
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Claims
Access tokens contain:
- `sub` - Username
- `email` - User email
- `userId` - User ID
- `type` - "access"
- `iss` - "fintrack-pro"
- `iat` - Issued at timestamp
- `exp` - Expiration timestamp

Refresh tokens contain:
- Same claims as access token
- `type` - "refresh"
- Longer expiration (7 days)

---

## Security Best Practices

### For Frontend Developers

1. **Store Tokens Securely**
   - Store access token in memory (state management)
   - Store refresh token in httpOnly cookie (if possible) or secure storage
   - Never store tokens in localStorage (XSS vulnerability)

2. **Handle Token Expiration**
   ```javascript
   // Pseudo-code
   async function makeApiCall(url, options) {
     try {
       const response = await fetch(url, {
         ...options,
         headers: {
           'Authorization': `Bearer ${accessToken}`,
           ...options.headers
         }
       });
       
       if (response.status === 401) {
         // Token expired, refresh it
         const newTokens = await refreshAccessToken();
         // Retry the request with new token
         return makeApiCall(url, options);
       }
       
       return response;
     } catch (error) {
       // Handle error
     }
   }
   ```

3. **Automatic Token Refresh**
   - Refresh token before it expires (e.g., 5 minutes before)
   - Use interceptors to handle 401 responses
   - Implement token refresh queue to avoid multiple refresh requests

4. **Logout Properly**
   - Call logout endpoint
   - Clear all tokens from storage
   - Redirect to login page

### For Backend Developers

1. **Token Validation**
   - Verify signature using public key
   - Check expiration
   - Validate token type (access vs refresh)

2. **Rate Limiting**
   - Implement rate limiting on login endpoint
   - Prevent brute force attacks

3. **Monitoring**
   - Log failed login attempts
   - Monitor for suspicious activity
   - Alert on multiple failed attempts

---

## Account Locking

### How It Works
1. User enters wrong password
2. Failed attempt counter increases
3. After 5 failed attempts, account is locked for 30 minutes
4. User receives error message with time remaining
5. Account automatically unlocks after 30 minutes
6. Counter resets on successful login

### Manual Unlock
Admins can manually unlock accounts (feature to be implemented):
```sql
UPDATE users 
SET failed_login_attempts = 0, 
    account_non_locked = true, 
    account_locked_until = NULL 
WHERE email = 'user@example.com';
```

---

## Token Cleanup

### Automatic Cleanup
- Runs daily at 2:00 AM
- Deletes expired refresh tokens
- Keeps database clean and performant

### Manual Cleanup
```sql
DELETE FROM refresh_tokens WHERE expires_at < NOW();
```

---

## Configuration

### application.properties
```properties
# JWT Configuration
app.jwt.public-key=classpath:app.pub
app.jwt.private-key=classpath:app.key
app.jwt.expiration=3600000              # 1 hour in milliseconds
app.jwt.refresh-expiration=604800000    # 7 days in milliseconds
```

### Customization
To change token expiration times, update the properties:
- Access token: `app.jwt.expiration`
- Refresh token: `app.jwt.refresh-expiration`

To change account locking settings, modify in `AuthService`:
```java
private static final int MAX_FAILED_ATTEMPTS = 5;
private static final long LOCK_DURATION_MINUTES = 30;
```

---

## Database Schema

### refresh_tokens Table
```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

## Testing

### Test Login Flow
```bash
# 1. Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@1234",
    "confirmPassword": "Test@1234",
    "firstName": "Test",
    "lastName": "User"
  }'

# 2. Verify Email (check email for token)
curl -X GET "http://localhost:8080/api/v1/auth/verify-email?token=YOUR_TOKEN"

# 3. Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@1234"
  }'

# 4. Access Protected Endpoint
curl -X GET http://localhost:8080/api/v1/protected \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# 5. Refresh Token
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'

# 6. Logout
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

---

## Troubleshooting

### Common Issues

**1. "Invalid email or password"**
- Check credentials are correct
- Ensure email is verified
- Check if account is locked

**2. "Please verify your email address before logging in"**
- Check email inbox for verification link
- Token expires after 24 hours
- Request new verification email (feature to be implemented)

**3. "Account is locked"**
- Wait for lock duration to expire (30 minutes)
- Contact admin for manual unlock

**4. "Invalid or expired refresh token"**
- Token has expired (7 days)
- Token was revoked (logout)
- Login again to get new tokens

**5. 401 Unauthorized on protected endpoints**
- Access token expired
- Refresh the token
- Check Authorization header format

---

## Security Considerations

### ⚠️ Important Security Notes

1. **Never expose private key**
   - Keep `app.key` secure
   - Don't commit to version control
   - Use environment variables in production

2. **HTTPS Only in Production**
   - Always use HTTPS
   - Tokens can be intercepted over HTTP

3. **Token Storage**
   - Don't store in localStorage (XSS risk)
   - Use httpOnly cookies or secure storage

4. **Password Requirements**
   - Minimum 8 characters
   - Must contain uppercase, lowercase, digit, and special character
   - No whitespace allowed

5. **Rate Limiting**
   - Implement rate limiting on login endpoint
   - Prevent brute force attacks

6. **Monitoring**
   - Monitor failed login attempts
   - Alert on suspicious activity
   - Regular security audits

---

## Future Enhancements

- [ ] Password reset functionality
- [ ] Two-factor authentication (2FA)
- [ ] Remember me functionality
- [ ] Social login (Google, Facebook, etc.)
- [ ] Email notification on new login
- [ ] Trusted devices
- [ ] Session management UI
- [ ] Admin panel for user management
- [ ] Rate limiting implementation
- [ ] Audit logging
