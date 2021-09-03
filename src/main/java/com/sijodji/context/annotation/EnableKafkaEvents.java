package com.sijodji.context.annotation;

import com.sijodji.context.config.KafkaEventsEnvironmentConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Import(KafkaEventsEnvironmentConfiguration.class)
public @interface EnableKafkaEvents {
}
