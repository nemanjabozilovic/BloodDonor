package com.example.blooddonor.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private static final String TAG = "EmailSender";

    private final String senderEmail;
    private final Executor executor;
    private final Handler handler;

    public EmailSender(String senderEmail) {
        this.senderEmail = senderEmail;
        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void sendEmail(String recipientEmail, String subject, String body, EmailSendCallback callback) {
        sendEmail(List.of(recipientEmail), subject, body, callback);
    }

    public void sendEmail(List<String> recipientEmails, String subject, String body, EmailSendCallback callback) {
        executor.execute(() -> {
            boolean success = false;
            String errorMessage = null;

            try {
                Session session = getSession();
                Message message = createMessage(session, recipientEmails, subject, body);

                Transport.send(message);
                success = true;
                Log.d(TAG, "Email successfully sent to: " + recipientEmails);

            } catch (Exception e) {
                errorMessage = e.getMessage();
                logEmailError(e, recipientEmails, subject);
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

    private Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "10.0.2.2");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");

        return Session.getInstance(props);
    }

    private Message createMessage(Session session, List<String> recipientEmails, String subject, String body) throws Exception {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));

        InternetAddress[] recipientAddresses = recipientEmails.stream()
                .map(email -> {
                    try {
                        return new InternetAddress(email);
                    } catch (Exception e) {
                        Log.e(TAG, "Invalid email address: " + email, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(InternetAddress[]::new);

        message.setRecipients(Message.RecipientType.TO, recipientAddresses);
        message.setSubject(subject);
        message.setText(body);

        return message;
    }

    private void logEmailError(Exception e, List<String> recipientEmails, String subject) {
        Log.e(TAG, "Failed to send email.", e);
        Log.e(TAG, "Recipients: " + recipientEmails);
        Log.e(TAG, "Subject: " + subject);
    }

    public interface EmailSendCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }
}