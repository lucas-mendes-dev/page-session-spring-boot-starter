package dev.lucasmendes.pagesession.processor;

import dev.lucasmendes.pagesession.store.PageSessionAttributeStore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
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

    private final ObjectProvider<PageSessionAttributeStore> storeProvider;

    public PageSessionBeanPostProcessor(final ObjectProvider<PageSessionAttributeStore> storeProvider) {
        this.storeProvider = storeProvider;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if(bean instanceof final RequestMappingHandlerAdapter adapter) {
            adapter.setSessionAttributeStore(storeProvider.getIfAvailable());
        }
        return bean;
    }
}
