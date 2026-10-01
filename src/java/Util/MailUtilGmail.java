package murach.email.util;

import sibApi.TransactionalEmailsApi;
import sibModel.*;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import java.util.Collections;

public class MailUtilGmail {

    // API Key Brevo
    private static final String BREVO_API_KEY = "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";

    public static void sendMail(String to, String from,
            String subject, String body, boolean bodyIsHTML) {

        if (from == null || from.trim().isEmpty()) {
            from = "thanh17022006@gmail.com";
        }
        if (to == null || to.trim().isEmpty()) {
            to = "thanh17022006@gmail.com";
        }

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(BREVO_API_KEY);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();

        // Nguoi gui: Email da verify tren Brevo
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(from);
        sender.setName("Ch\u00ed Th\u00e0nh Music Store");

        // Nguoi nhan
        SendSmtpEmailTo recipient = new SendSmtpEmailTo();
        recipient.setEmail(to);

        // Reply To
        SendSmtpEmailReplyTo replyTo = new SendSmtpEmailReplyTo();
        replyTo.setEmail(from);
        replyTo.setName("Ch\u00ed Th\u00e0nh Music Store");

        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        sendSmtpEmail.setSender(sender);
        sendSmtpEmail.setReplyTo(replyTo);
        sendSmtpEmail.setTo(Collections.singletonList(recipient));
        sendSmtpEmail.setSubject(subject);

        if (bodyIsHTML) {
            sendSmtpEmail.setHtmlContent(body);
        } else {
            sendSmtpEmail.setTextContent(body);
        }

        try {
            CreateSmtpEmail result = apiInstance.sendTransacEmail(sendSmtpEmail);
            System.out.println("[Brevo API] Gui mail thanh cong! Message ID: " + result.getMessageId());
        } catch (Throwable e) {
            System.err.println("[Brevo API] Gui mail that bai: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Loi gui mail qua Brevo: " + e.getMessage(), e);
        }
    }
}
