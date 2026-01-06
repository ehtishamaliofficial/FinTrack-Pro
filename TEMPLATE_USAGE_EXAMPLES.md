# Email Template Usage Examples

## Overview
The email system now uses **Thymeleaf templates** for flexible and maintainable email generation. All templates are located in `src/main/resources/templates/email/`.

## Template Structure

### Base Template
- **File:** `email-base.html`
- **Purpose:** Main wrapper with header, footer, and styling
- **Variables:**
  - `subject` - Email subject
  - `headerTitle` - Title shown in the header
  - `headerTheme` - Color theme (primary, success, danger, warning)
  - `footerText` - Footer text
  - `baseUrl` - Frontend base URL
  - `contentFragment` - Path to content template

### Available Templates

1. **verification-email.html** - Email verification
2. **welcome-email.html** - Welcome message after verification
3. **password-reset-email.html** - Password reset
4. **generic-email.html** - Generic template for any purpose

## Usage Examples

### 1. Using Pre-built Templates

#### Verification Email
```java
// Already implemented in AuthService
emailServicePort.sendVerificationEmail(
    "user@example.com",
    "johndoe",
    "verification-token-uuid"
);
```

#### Welcome Email
```java
// Already implemented in AuthService
emailServicePort.sendWelcomeEmail(
    "user@example.com",
    "johndoe"
);
```

#### Password Reset Email
```java
// To be implemented
emailServicePort.sendPasswordResetEmail(
    "user@example.com",
    "johndoe",
    "reset-token-uuid"
);
```

### 2. Using Generic Email Template

The `sendGenericEmail` method allows you to send custom emails without creating new templates.

#### Example 1: Simple Notification
```java
@Autowired
private EmailService emailService;

public void sendAccountLockedNotification(String email, String username) {
    String content = """
        <p>Your account has been temporarily locked due to multiple failed login attempts.</p>
        <p>For security reasons, your account will be automatically unlocked in 30 minutes.</p>
        <p>If this wasn't you, please contact our support team immediately.</p>
        """;
    
    emailService.sendGenericEmail(
        email,                          // to
        "Account Security Alert",       // subject
        "Account Locked",               // title
        content,                        // HTML content
        "Contact Support",              // button text
        "https://fintrackpro.com/support", // button link
        "danger",                       // button style
        "<strong>⚠️ Security Alert:</strong> If you didn't attempt to log in, your account may be compromised.", // info message
        "danger"                        // header theme
    );
}
```

#### Example 2: Budget Alert
```java
public void sendBudgetExceededAlert(String email, String username, String category, double amount) {
    String content = String.format("""
        <p>Hello <strong>%s</strong>,</p>
        <p>Your spending in the <strong>%s</strong> category has exceeded your budget limit.</p>
        <p>Current spending: <span class="highlight">$%.2f</span></p>
        <p>Consider reviewing your expenses to stay on track with your financial goals.</p>
        """, username, category, amount);
    
    emailService.sendGenericEmail(
        email,
        "Budget Alert - " + category,
        "Budget Exceeded",
        content,
        "View Budget Details",
        "https://fintrackpro.com/budgets/" + category,
        "warning",
        "<strong>💡 Tip:</strong> Review your recent transactions and adjust your budget if needed.",
        "warning"
    );
}
```

#### Example 3: Monthly Report
```java
public void sendMonthlyReport(String email, String username, String reportData) {
    String content = String.format("""
        <p>Hello <strong>%s</strong>,</p>
        <p>Your monthly financial report is ready!</p>
        <h3 style="color: #1f2937; margin-top: 20px;">This Month's Summary:</h3>
        %s
        <p>Keep up the great work managing your finances!</p>
        """, username, reportData);
    
    emailService.sendGenericEmail(
        email,
        "Your Monthly Financial Report",
        "Monthly Report Ready",
        content,
        "View Full Report",
        "https://fintrackpro.com/reports/monthly",
        "success",
        null, // no info message
        "primary"
    );
}
```

#### Example 4: Email Without Button
```java
public void sendSimpleNotification(String email, String username) {
    String content = """
        <p>This is a simple notification without any action button.</p>
        <p>Just wanted to let you know that your profile has been updated successfully.</p>
        """;
    
    emailService.sendGenericEmail(
        email,
        "Profile Updated",
        "Profile Update Confirmation",
        content,
        null,  // no button
        null,  // no link
        null,  // no button style
        null,  // no info message
        "success"
    );
}
```

### 3. Creating Custom Templates

If you need a completely custom template:

1. Create a new HTML file in `src/main/resources/templates/email/`
2. Use Thymeleaf syntax for dynamic content
3. Create a new method in `EmailService` similar to existing ones

#### Example: Custom Template
**File:** `src/main/resources/templates/email/invoice-email.html`
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <h2 th:text="'Hello, ' + ${username} + '!'">Hello, User!</h2>
    
    <p>Your invoice for <strong th:text="${invoiceMonth}">January</strong> is ready.</p>
    
    <div style="background-color: #f3f4f6; padding: 20px; border-radius: 8px; margin: 20px 0;">
        <h3 style="margin: 0 0 10px 0;">Invoice Details</h3>
        <p style="margin: 5px 0;">Invoice Number: <strong th:text="${invoiceNumber}">INV-001</strong></p>
        <p style="margin: 5px 0;">Amount: <strong th:text="${amount}">$99.99</strong></p>
        <p style="margin: 5px 0;">Due Date: <strong th:text="${dueDate}">2025-12-01</strong></p>
    </div>
    
    <div class="button-container">
        <a th:href="${invoiceLink}" class="button">View Invoice</a>
    </div>
</body>
</html>
```

**Service Method:**
```java
public void sendInvoiceEmail(String to, String username, String invoiceNumber, 
                             String invoiceMonth, String amount, String dueDate) {
    try {
        String subject = "Your Invoice is Ready - " + invoiceMonth;
        String invoiceLink = frontendUrl + "/invoices/" + invoiceNumber;
        
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("invoiceNumber", invoiceNumber);
        context.setVariable("invoiceMonth", invoiceMonth);
        context.setVariable("amount", amount);
        context.setVariable("dueDate", dueDate);
        context.setVariable("invoiceLink", invoiceLink);
        context.setVariable("subject", subject);
        context.setVariable("headerTitle", "Invoice Ready");
        context.setVariable("headerTheme", "primary");
        context.setVariable("footerText", messageUtil.getMessage("email.footer"));
        context.setVariable("baseUrl", frontendUrl);
        context.setVariable("contentFragment", "email/invoice-email :: body");
        
        String htmlContent = templateEngine.process("email/email-base", context);
        
        sendHtmlEmail(to, subject, htmlContent);
        log.info("Invoice email sent successfully to: {}", to);
    } catch (MessagingException e) {
        log.error("Failed to send invoice email to: {}", to, e);
        throw new RuntimeException("Failed to send invoice email", e);
    }
}
```

## Template Variables Reference

### Common Variables (Available in all templates)
- `${username}` - User's username
- `${subject}` - Email subject
- `${headerTitle}` - Header title
- `${headerTheme}` - Header color theme
- `${footerText}` - Footer text
- `${baseUrl}` - Frontend base URL

### Verification Email Variables
- `${verificationLink}` - Full verification URL

### Welcome Email Variables
- `${loginLink}` - Login page URL

### Password Reset Email Variables
- `${resetLink}` - Password reset URL

### Generic Email Variables
- `${title}` - Email title
- `${content}` - HTML content (can include any HTML)
- `${buttonText}` - Button text (optional)
- `${buttonLink}` - Button URL (optional)
- `${buttonStyle}` - Button style class (optional)
- `${infoMessage}` - Info box message (optional)

## Header Themes

Available theme colors:
- **primary** - Purple/Indigo gradient (default)
- **success** - Green gradient
- **warning** - Orange/Yellow gradient
- **danger** - Red gradient

## Button Styles

Available button styles:
- **button** - Default primary blue
- **button success** - Green button
- **button danger** - Red button

## CSS Classes Available in Templates

### Layout
- `.email-wrapper` - Main container
- `.email-header` - Header section
- `.email-body` - Body section
- `.email-footer` - Footer section

### Components
- `.button` - Action button
- `.button-container` - Button wrapper (centered)
- `.info-box` - Information box with left border
- `.link-text` - Styled text for URLs
- `.divider` - Horizontal line separator
- `.highlight` - Highlighted text with yellow background

### Typography
- `h1` - Main header title
- `h2` - Section heading
- `h3` - Subsection heading
- `p` - Paragraph text

## Best Practices

1. **Keep content concise** - Users scan emails quickly
2. **Use clear CTAs** - Make action buttons obvious
3. **Test on multiple clients** - Gmail, Outlook, Apple Mail, etc.
4. **Mobile-first** - Templates are responsive by default
5. **Personalize** - Use username and relevant data
6. **Add value** - Every email should have a purpose
7. **Include unsubscribe** - For marketing emails (future feature)
8. **Monitor delivery** - Check logs for failed sends

## Troubleshooting

### Template Not Found
- Ensure template file is in `src/main/resources/templates/email/`
- Check file name matches the path in `contentFragment`

### Variables Not Rendering
- Verify variable names match between Java and HTML
- Check Thymeleaf syntax: `th:text="${variableName}"`

### Styling Issues
- Inline styles work best for email clients
- Test with different email clients
- Use tables for complex layouts (if needed)

### Images Not Loading
- Use absolute URLs for images
- Host images on a CDN or web server
- Include alt text for accessibility

## Future Enhancements

- [ ] Email templates in database for easy customization
- [ ] Email queue for better performance
- [ ] Email analytics and tracking
- [ ] Unsubscribe functionality
- [ ] Email preferences management
- [ ] Attachment support
- [ ] Bulk email sending
- [ ] A/B testing for email templates
