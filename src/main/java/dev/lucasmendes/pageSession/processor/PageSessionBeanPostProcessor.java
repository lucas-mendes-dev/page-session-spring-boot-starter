package dev.lucasmendes.pageSession.processor;

import dev.lucasmendes.pageSession.store.PageSessionAttributeStore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * Injects the {@link PageSessionAttributeStore} into the {@link RequestMappingHandlerAdapter}
 * of Spring MVC after its initialization.
 *
 * <p>Must be declared as {@code static @Bean} to be instantiated before
 * other beans, ensuring that the {@link RequestMappingHandlerAdapter}
 * is already configured when controllers are registered.
 */
public class PageSessionBeanPostProcessor implements BeanPostProcessor {

    private final PageSessionAttributeStore store;

    public PageSessionBeanPostProcessor(final PageSessionAttributeStore store) {
        this.store = store;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if(bean instanceof final RequestMappingHandlerAdapter adapter) {
            adapter.setSessionAttributeStore(store);
        }
        return bean;
    }
}
