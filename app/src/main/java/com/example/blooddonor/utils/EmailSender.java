package com.example.blooddonor.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private final String senderEmail;
    private final Executor executor;
    private final Handler handler;

    public EmailSender(String senderEmail) {
        this.senderEmail = senderEmail;
        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public interface EmailSendCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void sendEmail(String recipientEmail, String subject, String body, EmailSendCallback callback) {
        executor.execute(() -> {
            boolean success = false;
            String errorMessage = null;

            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", "10.0.2.2");
                props.put("mail.smtp.port", "25");
                props.put("mail.smtp.auth", "false");
                props.put("mail.smtp.starttls.enable", "false");

                Session session = Session.getInstance(props);

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                success = true;

            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
            }

            final boolean finalSuccess = success;
            final String finalErrorMessage = errorMessage;

            handler.post(() -> {
                if (finalSuccess) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(finalErrorMessage != null ? finalErrorMessage : "Unknown error occurred.");
                }
            });
        });
    }
}