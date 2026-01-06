# Email Configuration Setup Guide

## Overview
This guide will help you configure Spring Mail for sending verification emails during user registration.

## Prerequisites
- Gmail account (or any other SMTP email provider)
- Application password for Gmail (if using 2FA)

## Gmail Setup (Recommended)

### Step 1: Enable 2-Step Verification
1. Go to your Google Account settings: https://myaccount.google.com/
2. Navigate to **Security**
3. Enable **2-Step Verification** if not already enabled

### Step 2: Generate App Password
1. Go to: https://myaccount.google.com/apppasswords
2. Select **Mail** as the app
3. Select **Other (Custom name)** as the device
4. Enter "FinTrack Pro" as the name
5. Click **Generate**
6. Copy the 16-character password (remove spaces)

### Step 3: Set Environment Variables
Add these environment variables to your system or IDE:

**Windows (PowerShell):**
```powershell
$env:MAIL="your-email@gmail.com"
$env:MAIL_PASSWORD="your-16-char-app-password"
$env:FRONTEND_URL="http://localhost:3000"
```

**Windows (Command Prompt):**
```cmd
set MAIL=your-email@gmail.com
set MAIL_PASSWORD=your-16-char-app-password
set FRONTEND_URL=http://localhost:3000
```

**Linux/Mac:**
```bash
export MAIL="your-email@gmail.com"
export MAIL_PASSWORD="your-16-char-app-password"
export FRONTEND_URL="http://localhost:3000"
```

**IntelliJ IDEA:**
1. Go to **Run > Edit Configurations**
2. Select your Spring Boot application
3. Add environment variables in the **Environment variables** field:
   ```
   MAIL=your-email@gmail.com;MAIL_PASSWORD=your-16-char-app-password;FRONTEND_URL=http://localhost:3000
   ```

## Alternative Email Providers

### Outlook/Hotmail
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=${MAIL}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Yahoo Mail
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=${MAIL}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Custom SMTP Server
```properties
spring.mail.host=your-smtp-server.com
spring.mail.port=587
spring.mail.username=${MAIL}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Testing the Configuration

### 1. Start the Application
```bash
mvn spring-boot:run
```

### 2. Register a New User
Send a POST request to `/api/v1/auth/register`:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Test@1234",
  "confirmPassword": "Test@1234",
  "firstName": "Test",
  "lastName": "User"
}
```

### 3. Check Your Email
You should receive a verification email with a link like:
```
http://localhost:3000/verify-email?token=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

### 4. Verify Email
Click the link or send a GET request to:
```
GET /api/v1/auth/verify-email?token=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

## API Endpoints

### Register User
- **Endpoint:** `POST /api/v1/auth/register`
- **Description:** Registers a new user and sends verification email
- **Response:** Success message with instruction to check email

### Verify Email
- **Endpoint:** `GET /api/v1/auth/verify-email?token={token}`
- **Description:** Verifies user's email address using the token
- **Response:** Success message and sends welcome email

## Email Templates

The system uses **Thymeleaf templates** for flexible email generation. Templates are located in `src/main/resources/templates/email/`.

### Pre-built Templates

1. **Verification Email** (`verification-email.html`) - Sent after registration
2. **Welcome Email** (`welcome-email.html`) - Sent after successful email verification
3. **Password Reset Email** (`password-reset-email.html`) - For password recovery (future feature)
4. **Generic Email** (`generic-email.html`) - For custom notifications

### Template Features

All email templates include:
- **Responsive design** - Works on all devices
- **Professional styling** - Modern gradient headers with multiple themes
- **Clear CTAs** - Prominent action buttons
- **Info boxes** - Highlighted important information
- **Branded footer** - Consistent branding across all emails
- **Customizable themes** - Primary, Success, Warning, Danger color schemes

### Using Templates

See `TEMPLATE_USAGE_EXAMPLES.md` for detailed examples of:
- Sending pre-built emails
- Using the generic template
- Creating custom templates
- Available variables and styling options

## Troubleshooting

### Email Not Sending
1. **Check credentials:** Verify MAIL and MAIL_PASSWORD environment variables
2. **Check SMTP settings:** Ensure host and port are correct
3. **Check logs:** Look for error messages in application logs
4. **Firewall:** Ensure port 587 is not blocked
5. **Gmail security:** Make sure "Less secure app access" is enabled or use App Password

### Token Expired
- Verification tokens expire after 24 hours
- User needs to request a new verification email (feature to be implemented)

### Email Goes to Spam
- Add your domain to SPF records
- Use a verified email address
- Consider using a dedicated email service like SendGrid or AWS SES for production

## Production Recommendations

For production environments, consider:

1. **Use a dedicated email service:**
   - SendGrid
   - AWS SES
   - Mailgun
   - Postmark

2. **Add email templates to database** for easy customization

3. **Implement email queue** for better performance

4. **Add retry mechanism** for failed emails

5. **Monitor email delivery** rates and bounces

6. **Use environment-specific configurations**

## Security Notes

⚠️ **Important Security Practices:**
- Never commit email credentials to version control
- Always use environment variables for sensitive data
- Use App Passwords instead of actual passwords
- Rotate credentials regularly
- Monitor for suspicious email activity
- Implement rate limiting on registration endpoint

## Support

If you encounter issues:
1. Check application logs for detailed error messages
2. Verify all environment variables are set correctly
3. Test SMTP connection using a mail client
4. Review Spring Mail documentation: https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#mail
