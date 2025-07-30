package Marketplace.services;

import java.io.IOException;

import Marketplace.models.EmailConfirmationToken;
import Marketplace.models.PasswordResetToken;
import Marketplace.models.User;
import Marketplace.projections.ICountryDto;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendConfirmationEmail(EmailConfirmationToken emailConfirmationToken) throws MessagingException;
    void sendPasswordResetEmail(PasswordResetToken resetToken) throws MessagingException;

    void sendRegistrationProof(
        User user,
        String proofMessage,
        byte[] imageData,
        String imageFilename
    ) throws MessagingException, IOException;

    public void sendResidenceDecisionEmail(User user, boolean approved)
                        throws MessagingException;

    void sendListingDeletionEmail(User owner, String listingTitle, String message)
                        throws MessagingException;

    public void sendContactMessage(User user, String header, String message, ICountryDto country) throws MessagingException;
}
