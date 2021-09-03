package com.sijodji.context.resolver;

import com.sijodji.model.BasicEvent;
import com.sijodji.transfer.BasicEventKafkaSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.core.ResolvableType;

import java.util.Objects;

/**
 * Not used anymore
 *
 * @see com.sijodji.context.bpp.EventSenderBeanFactoryPostProcessor
 */
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class EventSenderAutowireCandidateResolver implements AutowireCandidateResolver {

    AutowireCandidateResolver resolver;

    @Override
    public boolean isAutowireCandidate(final BeanDefinitionHolder bdHolder, final DependencyDescriptor descriptor) {
        Class<?> dependencyType = descriptor.getDependencyType();

        if (dependencyType.isAssignableFrom(BasicEventKafkaSender.class)) {
            ResolvableType resolvableType = descriptor.getResolvableType();
            ResolvableType[] generics = resolvableType.getGenerics();
            if (generics.length > 0
                    && Objects.nonNull(generics[0].resolve())
                    && BasicEvent.class.isAssignableFrom(Objects.requireNonNull(generics[0].resolve()))
                    && Objects.equals(generics[0].getSuperType().getRawClass(), BasicEvent.class)) {
                if (bdHolder.getBeanDefinition().getResolvableType().getGenerics()[0].resolve().getSimpleName().equals(generics[0].resolve().getSimpleName())) {
                    //TODO: refactor this mess, if condition was made for debug only
                    return true;
                }
                return false;
            }
            throw new IllegalStateException("Error creating bean " + descriptor.getMember().getDeclaringClass().getSimpleName()
                    + ". Field " + descriptor.getField().getName() + " of class "
                    + BasicEventKafkaSender.class.getSimpleName() + " must be declared with generic type");
        }

        return resolver.isAutowireCandidate(bdHolder, descriptor);
    }
}
