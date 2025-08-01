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

@Service
public class EmailServiceImpl implements EmailService {

        @Autowired
        private JavaMailSender sender;

        @Value("${origin.url}")
        private String originUrl;

        @Override
        public void sendConfirmationEmail(EmailConfirmationToken emailConfirmationToken) throws MessagingException {
                // MIME - HTML message
                MimeMessage message = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom("priviumcontacto@gmail.com");
                helper.setTo(emailConfirmationToken.getUser().getEmail().trim());

                helper.setSubject("Confirma tu correo electrónico – Privium Marketplace");

                helper.setText(
                                "<html>" +
                                                "<body>" +
                                                "<h2>¡Hola "
                                                + emailConfirmationToken.getUser().getName() + " "
                                                + emailConfirmationToken.getUser().getLastName() + "!</h2>" +
                                                "<p>¡Bienvenido a <strong>Privium Marketplace</strong>! " +
                                                "Nos alegra que te hayas registrado.</p>" +
                                                "<p>Para activar tu cuenta y empezar a compartir con tu comunidad privada, "
                                                +
                                                "haz clic en el siguiente enlace:</p>" +
                                                "<p>" + generateConfirmationLink(emailConfirmationToken.getToken())
                                                + "</p>" +
                                                "<p>Si no has solicitado esta verificación, ignora este correo.</p>" +
                                                "<br/>" +
                                                "<p>Saludos cordiales,<br/>" +
                                                "El equipo de Privium Marketplace</p>" +
                                                "</body>" +
                                                "</html>",
                                true);

                sender.send(message);
        }

        private String generateConfirmationLink(String token) {
                return "<a href=" + originUrl + "/auth/verify-email?token=" + token + ">Confirm Email</a>";
        }

        @Override
        public void sendPasswordResetEmail(PasswordResetToken resetToken) throws MessagingException {
                User user = resetToken.getUser();
                String cleanEmail = user.getEmail().trim();

                MimeMessage message = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setFrom("priviumcontacto@gmail.com");
                helper.setTo(cleanEmail);
                helper.setSubject("Restablece tu contraseña – Privium Marketplace");

                // Genera el enlace de reset (ajusta la URL a tu frontend)
                String link = originUrl + "/auth/reset-password?token=" + resetToken.getToken();

                helper.setText(
                                "<html>" +
                                                "<body>" +
                                                "<h2>¡Hola " + user.getName() + " " + user.getLastName() + "!</h2>" +
                                                "<p>Has solicitado restablecer tu contraseña en <strong>Privium Marketplace</strong>.</p>"
                                                +
                                                "<p>Haz clic en el siguiente enlace para elegir una nueva contraseña:</p>"
                                                +
                                                "<p><a href=\"" + link + "\">Restablecer contraseña</a></p>" +
                                                "<p>Este enlace expirará en 24 horas. Si no fuiste tú quien lo solicitó, ignora este correo.</p>"
                                                +
                                                "<br/>" +
                                                "<p>Saludos cordiales,<br/>" +
                                                "El equipo de Privium Marketplace</p>" +
                                                "</body>" +
                                                "</html>",
                                true);

                sender.send(message);
        }

        @Override
        public void sendRegistrationProof(
                        User user,
                        String proofMessage,
                        byte[] imageData,
                        String imageFilename) throws MessagingException, IOException {
                MimeMessage msg = sender.createMimeMessage();
                MimeMessageHelper h = new MimeMessageHelper(msg, true);
                h.setFrom("priviumcontacto@gmail.com");
                h.setTo("tucuenta@tudominio.com");
                h.setSubject("Prueba de residencia – Privium Marketplace");

                String html = "<html><body>"
                                + "<h2>Nuevo registro de “" + user.getName() + " " + user.getLastName() + "”</h2>"
                                + "<p><strong>Email:</strong> " + user.getEmail() + "</p>"
                                + "<p><strong>Mensaje:</strong><br/>"
                                + (proofMessage != null ? proofMessage : "(sin mensaje)") + "</p>"
                                + "</body></html>";
                h.setText(html, true);

                if (imageData != null && imageFilename != null) {
                        ByteArrayDataSource ds = new ByteArrayDataSource(imageData,
                                        Files.probeContentType(Paths.get(imageFilename)));
                        h.addAttachment(imageFilename, ds);
                }

                sender.send(msg);
        }

        @Override
    public void sendResidenceDecisionEmail(User user, boolean approved)
                        throws MessagingException {

                MimeMessage msg = sender.createMimeMessage();
                MimeMessageHelper h = new MimeMessageHelper(msg, true);

                h.setFrom("priviumcontacto@gmail.com");
                h.setTo(user.getEmail().trim());

                String subject = approved
                                ? "Residencia verificada – Privium Marketplace"
                                : "Residencia rechazada – Privium Marketplace";
                h.setSubject(subject);

                String html = """
                                <html><body>
                                    <h2>¡Hola %s %s!</h2>
                                    <p>Tu prueba de residencia ha sido <strong>%s</strong>.</p>
                                    %s
                                    <br/>
                                    <p>Saludos cordiales,<br/>
                                    El equipo de Privium Marketplace</p>
                                </body></html>
                                """.formatted(
                                user.getName(), user.getLastName(),
                                approved ? "aprobada" : "rechazada",
                                approved
                                                ? "<p>Ya podés operar dentro de tu comunidad privada.</p>"
                                                : "<p>Revisa la documentación y vuelve a intentarlo.</p>");

                h.setText(html, true);
                sender.send(msg);
        }

        @Override
        public void sendListingDeletionEmail(User owner, String listingTitle, String message)
                        throws MessagingException {

                MimeMessage msg = sender.createMimeMessage();
                MimeMessageHelper h = new MimeMessageHelper(msg, true);

                h.setFrom("priviumcontacto@gmail.com");
                h.setTo(owner.getEmail().trim());
                h.setSubject("Publicación eliminada – Privium Marketplace");

                String html = String.format(
                                "<html><body>" +
                                "<h2>¡Hola %s %s!</h2>" +
                                "<p>Tu publicación <strong>%s</strong> ha sido eliminada por un administrador.</p>" +
                                "%s" +
                                "<br/>" +
                                "<p>Saludos cordiales,<br/>" +
                                "El equipo de Privium Marketplace</p>" +
                                "</body></html>",
                                owner.getName(), owner.getLastName(), listingTitle,
                                message != null ? "<p>" + message + "</p>" : "");

                h.setText(html, true);
                sender.send(msg);
        }

        @Override
        public void sendContactMessage(User user, String header, String message, ICountryDto country) throws MessagingException {
                MimeMessage msg = sender.createMimeMessage();
                MimeMessageHelper h = new MimeMessageHelper(msg, true);

                h.setFrom("priviumcontacto@gmail.com");
                h.setTo("mpulitano1701@gmail.com");

                StringBuilder subject = new StringBuilder();
                if (header != null && !header.isBlank()) {
                        subject.append(header).append(" - ");
                }
                subject.append(user.getName()).append(" ").append(user.getLastName());

                subject.append(" (").append(country.getName()).append(")");

                h.setSubject(subject.toString());

                h.setText(message, false);
                sender.send(msg);
        }

}
