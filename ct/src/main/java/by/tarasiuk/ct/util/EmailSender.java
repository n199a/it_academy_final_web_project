package by.tarasiuk.ct.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static by.tarasiuk.ct.util.MessageKey.EMAIL_SUBJECT_COMPLETION_REGISTRATION;
import static by.tarasiuk.ct.util.MessageKey.EMAIL_TEXT_COMPLETION_REGISTRATION;

/**
 * Util class designed to send email.
 */
public class EmailSender {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PROPERTIES_EMAIL = "email.properties";
    private static final String USER_NAME = "mail.user.name";
    private static final String USER_PASSWORD = "mail.user.password";
    private static final String PERSONAL = "mail.user.personal";
    private static final String LINK_START = "http://localhost:8081/controller?account_email=";
    private static final String LINK_COMMAND = "&command=activate_account";
    private static final String LINK_TOKEN = "&token_number=";
    private static final String LINK_LOCALE = "&locale=";
    private static final String CONTENT = "text/html";
    private static final String user;
    private static final String password;
    private static final String personalName;
    private static final Session session;

    /**
     * Static initialization/
     */
    static  {
        Properties properties = PropertiesLoader.getProperties(PROPERTIES_EMAIL);

        user = properties.getProperty(USER_NAME);
        password = properties.getProperty(USER_PASSWORD);
        personalName = properties.getProperty(PERSONAL);

        // Authentication
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        };

        session = Session.getDefaultInstance(properties, authenticator);
        session.setDebug(true);
    }

    private EmailSender() {
    }

    /**
     * Method for sending email to confirm email for account activation.
     *
     * @param locale            Locale message.
     * @param firstName         First name.
     * @param emailTo           The email address to which the email should be sent.
     * @param token             Token number for activation account.
     * @see                     by.tarasiuk.ct.model.entity.impl.Token
     */
    public static void sendActivationEmail(String locale, String firstName, String emailTo, String token) {
        String emailSubject = MessageManager.findMassage(EMAIL_SUBJECT_COMPLETION_REGISTRATION, locale);
        String formatMessage = MessageManager.findMassage(EMAIL_TEXT_COMPLETION_REGISTRATION, locale);
        StringBuilder link = new StringBuilder(LINK_START)
                .append(emailTo)
                .append(LINK_COMMAND)
                .append(LINK_TOKEN)
                .append(token)
                .append(LINK_LOCALE)
                .append(locale);
        String emailMessage = String.format(formatMessage, firstName, link);

        try {
            InternetAddress addressTo = new InternetAddress(emailTo);
            InternetAddress addressFrom = new InternetAddress(user, personalName);

            final Message message = new MimeMessage(session);
            message.setFrom(addressFrom);
            message.setRecipient(Message.RecipientType.TO, addressTo);
            message.setSubject(emailSubject);
            message.setContent(emailMessage, CONTENT);

            Transport.send(message);
            LOGGER.info("Message to '{}' with subject '{}' was successfully send!", emailTo, emailSubject);
        } catch (AddressException e) {
            LOGGER.fatal("Can't created Internet address for address '{}'.", user, e);
            throw new RuntimeException("Can't created Internet address for address '" + user + "'.", e);
        } catch (MessagingException e) {
            LOGGER.fatal("Can't created message.", e);
            throw new RuntimeException("Can't created message.", e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
