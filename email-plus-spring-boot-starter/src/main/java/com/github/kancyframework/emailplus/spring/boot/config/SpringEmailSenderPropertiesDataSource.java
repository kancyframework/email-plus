package com.github.kancyframework.emailplus.spring.boot.config;

import com.github.kancyframework.emailplus.core.EmailSenderProperties;
import com.github.kancyframework.emailplus.core.EmailSenderPropertiesDataSource;
import com.github.kancyframework.emailplus.core.annotation.Order;
import com.github.kancyframework.emailplus.spring.boot.properties.EmailplusProperties;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * SpringMailPropertiesDataSource
 *
 * @author kancy
 * @date 2020/2/21 1:16
 */
@Order(Integer.MIN_VALUE + 10)
public class SpringEmailSenderPropertiesDataSource implements EmailSenderPropertiesDataSource {
    /**
     * 加载属性
     *
     * @return
     */
    @Override
    public List<EmailSenderProperties> load() {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        if (Objects.nonNull(applicationContext)){
            EmailplusProperties emailplusProperties = applicationContext.getBean(EmailplusProperties.class);
            Map<String, EmailSenderProperties> senderProperties = emailplusProperties.getSender();
            if (Objects.nonNull(senderProperties)){
                senderProperties.forEach((k,v) ->{
                    if (Objects.isNull(v.getName())){
                        v.setName(k);
                    }
                });
                return new ArrayList<>(senderProperties.values());
            }
        }
        return Collections.emptyList();
    }
}
