package Marketplace.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import Marketplace.models.EmailConfirmationToken;
import Marketplace.models.PasswordResetToken;
import Marketplace.models.User;
import Marketplace.projections.ICountryDto;
import Marketplace.services.EmailService;

/**
 * Centralised e‑mail utility for Privium Marketplace.
 * <p>
 * All messages share a friendlier tone, richer context
 * (why you received the e‑mail, what to do next and how to get help)
 * and every dynamic link is generated from a single {@code originUrl}
 * property so that changing the base host/domain requires no code change.
 * </p>
 */
@Service
public class EmailServiceImpl implements EmailService {

        private static final String FROM_EMAIL = "priviumcontacto@gmail.com";
        private static final String BRAND_NAME = "Privium Marketplace";

        @Autowired
        private JavaMailSender sender;

        /** Base URL for the front‑end (e.g. "https://app.privium.com/") */
        @Value("${origin.url}")
        private String originUrl;

        /* ----------------------------------------------------------- */
        /* Helper methods */
        /* ----------------------------------------------------------- */

        /**
         * Ensures that {@code originUrl} always ends with a single '/'.
         */
        private String baseUrl() {
                return originUrl.endsWith("/") ? originUrl : originUrl + "/";
        }

        private String makeButton(String href, String label) {
                return "<a href=\"" + href + "\" style=\"display:inline-block;padding:12px 24px;margin:16px 0;"
                                + "background-color:#1d4ed8;border-radius:6px;color:#ffffff;text-decoration:none;"
                                + "font-weight:600;\">" + label + "</a>";
        }

        private String fullWidthImg(String src, String alt) {
                return "<img src=\"" + src + "\" alt=\"" + alt + "\" style=\"max-width:100%;height:auto;border:0;\"/>";
        }

        private String wrapper(String title, String content) {
                return "<html><body style=\"font-family:Arial,Helvetica,sans-serif;line-height:1.5;color:#111827;\">"
                                + fullWidthImg(baseUrl() + "assets/email/header.png", BRAND_NAME)
                                + "<h2 style=\"color:#1d4ed8;\">" + title + "</h2>"
                                + content
                                + "<hr style=\"margin-top:32px;border:none;border-top:1px solid #e5e7eb;\"/>"
                                + "<small style=\"color:#6b7280;\">Este correo fue enviado por " + BRAND_NAME
                                + ". Si tienes alguna duda o "
                                + "recibiste este mensaje por error, por favor contáctanos en <a href=\"mailto:"
                                + FROM_EMAIL + "\">"
                                + FROM_EMAIL + "</a>.</small>"
                                + "</body></html>";
        }

        /* ----------------------------------------------------------- */
        /* Email implementations */
        /* ----------------------------------------------------------- */

        @Override
        public void sendConfirmationEmail(EmailConfirmationToken ect) throws MessagingException {
                User u = ect.getUser();
                String link = baseUrl() + "auth/verify-email?token=" + ect.getToken();

                String inner = "<p>¡Hola <strong>" + u.getName() + " " + u.getLastName()
                                + "</strong>! Gracias por registrarte en "
                                + BRAND_NAME + ". Solo queda un paso para activar tu cuenta.</p>"
                                + makeButton(link, "Confirmar correo electrónico")
                                + "<p>Si el botón no funciona copia y pega este enlace en tu navegador:<br/><a href=\""
                                + link + "\">" + link + "</a></p>";

                sendHtmlMail(u.getEmail(), "Confirma tu correo electrónico – " + BRAND_NAME,
                                wrapper("Activa tu cuenta", inner));
        }

        @Override
        public void sendPasswordResetEmail(PasswordResetToken prt) throws MessagingException {
                User u = prt.getUser();
                String link = baseUrl() + "auth/reset-password?token=" + prt.getToken();

                String inner = "<p>¡Hola <strong>" + u.getName() + " " + u.getLastName() + "</strong>! "
                                + "Recibimos una solicitud para restablecer tu contraseña.</p>"
                                + makeButton(link, "Elegir nueva contraseña")
                                + "<p>Este enlace vencerá en 24 horas. Si tú no solicitaste el cambio, simplemente ignora este mensaje.</p>";

                sendHtmlMail(u.getEmail(), "Restablece tu contraseña – " + BRAND_NAME,
                                wrapper("Restablecer contraseña", inner));
        }

        @Override
        public void sendRegistrationProof(User user, String proofMessage, byte[] imageData, String imageFilename)
                        throws MessagingException, IOException {

                MimeMessage msg = sender.createMimeMessage();
                MimeMessageHelper h = new MimeMessageHelper(msg, true);
                h.setFrom(FROM_EMAIL);
                h.setTo("mpulitano1701@gmail.com");
                h.setSubject("Nuevo registro pendiente de verificación – " + BRAND_NAME);

                String inner = "<p><strong>Email:</strong> " + user.getEmail() + "</p>"
                                + (proofMessage != null && !proofMessage.isBlank()
                                                ? "<p><strong>Mensaje del usuario:</strong><br/>" + proofMessage
                                                                + "</p>"
                                                : "");

                h.setText(wrapper("Prueba de residencia recibida", inner), true);

                if (imageData != null && imageFilename != null) {
                        ByteArrayDataSource ds = new ByteArrayDataSource(imageData,
                                        Files.probeContentType(Paths.get(imageFilename)));
                        h.addAttachment(imageFilename, ds);
                }

                sender.send(msg);
        }

        @Override
        public void sendResidenceDecisionEmail(User user, boolean approved) throws MessagingException {
                String title = approved ? "Residencia verificada" : "Residencia rechazada";
                String decisionText = approved
                                ? "<p>¡Felicidades! Ya puedes disfrutar de todas las funcionalidades dentro de tu comunidad privada.</p>"
                                : "<p>Lamentamos informarte que la documentación enviada no fue suficiente. Revise los documentos y mande el seguimiento de su caso a priviumcontacto@gmail.com. Si fue un error, sepa disculparnos. Nuestro deber es brindarle la mayor seguridad a nuestros usuarios</p>";

                String inner = "<p>¡Hola <strong>" + user.getName() + " " + user.getLastName() + "</strong>!</p>"
                                + decisionText;

                sendHtmlMail(user.getEmail(), title + " – " + BRAND_NAME, wrapper(title, inner));
        }

        @Override
        public void sendListingDeletionEmail(User owner, String listingTitle, String adminMessage)
                        throws MessagingException {
                String inner = "<p>¡Hola <strong>" + owner.getName() + " " + owner.getLastName() + "</strong>!</p>"
                                + "<p>Tu publicación <strong>" + listingTitle
                                + "</strong> fue eliminada por un administrador por no cumplir con nuestras normas de convivencia.</p>"
                                + (adminMessage != null && !adminMessage.isBlank()
                                                ? "<p>Motivo: " + adminMessage + "</p>"
                                                : "")
                                + "<p>Si tienes dudas, responde a este correo para obtener más información.</p>";

                sendHtmlMail(owner.getEmail(), "Publicación eliminada – " + BRAND_NAME,
                                wrapper("Publicación eliminada", inner));
        }

        @Override
        public void sendContactMessage(User user, String header, String message, ICountryDto country)
                        throws MessagingException {
                String subject = (header != null && !header.isBlank() ? header + " – " : "")
                                + user.getName() + " " + user.getLastName() + " (" + country.getName() + ")";

                sendHtmlMail("mpulitano1701@gmail.com", subject, wrapper(subject, "<p>" + message + "</p>"));
        }

        /* ----------------------------------------------------------- */
        /* Private send helpers */
        /* ----------------------------------------------------------- */

        private void sendHtmlMail(String to, String subject, String htmlBody) throws MessagingException {
                MimeMessage msg = sender.createMimeMessage();
                MimeMessageHelper h = new MimeMessageHelper(msg, true);
                h.setFrom(FROM_EMAIL);
                h.setTo(to.trim());
                h.setSubject(subject);
                h.setText(htmlBody, true);
                sender.send(msg);
        }
}
