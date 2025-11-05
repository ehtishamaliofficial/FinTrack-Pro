# Email Templates - Quick Reference

## 📁 Template Files Location
`src/main/resources/templates/email/`

## 📧 Available Templates

### 1. email-base.html
**Base template** with header, body, and footer structure. All other templates extend this.

**Features:**
- Responsive design
- Multiple color themes (primary, success, warning, danger)
- Professional gradient headers
- Consistent footer with links
- Mobile-optimized

### 2. verification-email.html
**Purpose:** Email verification after registration

**Variables:**
- `username` - User's name
- `verificationLink` - Full verification URL

**Theme:** Primary (Purple/Indigo)

### 3. welcome-email.html
**Purpose:** Welcome message after successful verification

**Variables:**
- `username` - User's name
- `loginLink` - Login page URL

**Theme:** Success (Green)

**Features:**
- Feature highlights
- Getting started tips
- Call-to-action button

### 4. password-reset-email.html
**Purpose:** Password reset request

**Variables:**
- `username` - User's name
- `resetLink` - Password reset URL

**Theme:** Danger (Red)

**Features:**
- Security warnings
- Expiration notice
- "Didn't request this?" message

### 5. generic-email.html
**Purpose:** Flexible template for any custom email

**Variables:**
- `title` - Email heading
- `content` - HTML content (flexible)
- `buttonText` - Optional button text
- `buttonLink` - Optional button URL
- `buttonStyle` - Button color (primary, success, danger)
- `infoMessage` - Optional info box message

**Theme:** Configurable

## 🎨 Color Themes

| Theme | Colors | Use Case |
|-------|--------|----------|
| `primary` | Purple/Indigo | General notifications, verification |
| `success` | Green | Success messages, welcome emails |
| `warning` | Orange/Yellow | Warnings, alerts, budget notifications |
| `danger` | Red | Security alerts, password reset, errors |

## 🔧 Quick Usage

### Send Verification Email
```java
emailServicePort.sendVerificationEmail(email, username, token);
```

### Send Welcome Email
```java
emailServicePort.sendWelcomeEmail(email, username);
```

### Send Password Reset Email
```java
emailServicePort.sendPasswordResetEmail(email, username, token);
```

### Send Generic Email
```java
emailService.sendGenericEmail(
    email,           // to
    "Subject",       // subject
    "Title",         // title
    "<p>Content</p>", // HTML content
    "Click Here",    // button text (optional)
    "https://...",   // button link (optional)
    "success",       // button style (optional)
    "Info message",  // info box (optional)
    "primary"        // header theme
);
```

## 📝 CSS Classes

### Layout
- `.email-wrapper` - Main container (600px max-width)
- `.email-header` - Header with gradient background
- `.email-body` - Content area with padding
- `.email-footer` - Footer with links

### Components
- `.button` - Primary action button
- `.button.success` - Green button
- `.button.danger` - Red button
- `.button-container` - Centered button wrapper
- `.info-box` - Highlighted information box
- `.link-text` - Styled URL display
- `.divider` - Horizontal separator
- `.highlight` - Yellow highlighted text

## 📱 Responsive Design

All templates are mobile-responsive with:
- Fluid layouts
- Readable font sizes
- Touch-friendly buttons
- Optimized spacing

Breakpoint: 600px

## 🔗 Related Documentation

- **EMAIL_SETUP.md** - Configuration and setup guide
- **TEMPLATE_USAGE_EXAMPLES.md** - Detailed usage examples and code samples

## 🚀 Adding New Templates

1. Create HTML file in `templates/email/`
2. Use Thymeleaf syntax: `th:text="${variable}"`
3. Add method in `EmailService.java`
4. Set up Context variables
5. Process with `templateEngine.process()`

## ⚙️ Configuration

**Required in application.properties:**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL}
spring.mail.password=${MAIL_PASSWORD}
app.frontend.url=${FRONTEND_URL:http://localhost:3000}
```

**Required environment variables:**
- `MAIL` - Email address
- `MAIL_PASSWORD` - Email password/app password
- `FRONTEND_URL` - Frontend application URL

## 📦 Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

## ✅ Best Practices

1. **Keep it simple** - Avoid complex layouts
2. **Test thoroughly** - Check on Gmail, Outlook, Apple Mail
3. **Use inline styles** - Better email client support
4. **Optimize images** - Use absolute URLs
5. **Add alt text** - For accessibility
6. **Include plain text** - Fallback for text-only clients
7. **Monitor delivery** - Check logs regularly
8. **Personalize** - Use user's name and relevant data

## 🐛 Troubleshooting

**Template not found:**
- Check file path and name
- Ensure file is in `src/main/resources/templates/email/`

**Variables not rendering:**
- Verify Context variables match template
- Check Thymeleaf syntax

**Styling broken:**
- Test in multiple email clients
- Use inline styles when possible
- Avoid advanced CSS features

**Email not sending:**
- Check SMTP configuration
- Verify credentials
- Review application logs
- Check firewall settings
