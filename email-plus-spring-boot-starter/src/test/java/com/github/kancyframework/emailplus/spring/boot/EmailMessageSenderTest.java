package com.github.kancyframework.emailplus.spring.boot;

import com.github.kancyframework.emailplus.core.EmailSender;
import com.github.kancyframework.emailplus.spring.boot.config.EmailplusAutoConfiguration;
import com.github.kancyframework.emailplus.spring.boot.message.EmailMessage;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SenderTest
 *
 * @author kancy
 * @date 2020/2/21 1:45
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EmailplusAutoConfiguration.class, RedisAutoConfiguration.class})
public class EmailMessageSenderTest {
    @Autowired
    private EmailSender emailSender;
    @Ignore
    @Test
    public void testRender() {
        emailSender.send(EmailMessage.create("test-a"));
    }
}
