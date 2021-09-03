package com.sijodji.context.bpp;

import com.sijodji.context.resolver.EventSenderAutowireCandidateResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * Not used anymore, cause of spring bean factory resolves bean dependencies perfectly
 *
 * @see EventSenderAutowireCandidateResolver
 */
public class EventSenderBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ((DefaultListableBeanFactory) beanFactory).setAutowireCandidateResolver(
                new EventSenderAutowireCandidateResolver(((DefaultListableBeanFactory) beanFactory).getAutowireCandidateResolver())
        );
    }
}
