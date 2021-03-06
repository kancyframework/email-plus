package com.github.kancyframework.emailplus.core;

/**
 * 谷歌
 * GmailSimpleEmailSender
 *
 * @author kancy
 * @date 2020/2/20 1:59
 */
public class GmailSimpleEmailSender extends SimpleEmailSender {
    public GmailSimpleEmailSender() {
        setHost("smtp.gmail.com");
    }

    public GmailSimpleEmailSender(String username, String password) {
        this();
        setUsername(username);
        setPassword(password);
    }
}
