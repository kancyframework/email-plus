package com.github.kancyframework.emailplus.core;

import java.util.List;

/**
 * EmailSenderSelector
 *
 * @author kancy
 * @date 2020/2/20 13:17
 */
public interface EmailSenderSelector {
    /**
     * 找到一个可用的EmailSender
     *
     * @param emailSenders
     * @return
     */
    EmailSender findEmailSender(List<EmailSender> emailSenders);
}
