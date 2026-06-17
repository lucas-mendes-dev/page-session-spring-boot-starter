package dev.lucasmendes.pagesession.config;

import dev.lucasmendes.pagesession.interceptor.PageSessionHandlerInterceptor;
import dev.lucasmendes.pagesession.processor.PageSessionBeanPostProcessor;
import dev.lucasmendes.pagesession.store.PageSessionAttributeStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * Auto-configuration for the Page Session Starter.
 *
 * <p>Automatically activates in Spring Boot projects with Spring MVC on the classpath.
 * No manual configuration is necessary.
 *
 * <p>To override the default {@link PageSessionAttributeStore}, declare
 * your own bean — the {@link ConditionalOnMissingBean} will ensure that the
 * starter does not replace yours.
 */
@AutoConfiguration(after = org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ RequestMappingHandlerAdapter.class, WebMvcConfigurer.class })
@EnableConfigurationProperties(PageSessionProperties.class)
public class PageSessionAutoConfiguration implements WebMvcConfigurer {

    private final PageSessionProperties properties;

    public PageSessionAutoConfiguration(final PageSessionProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public PageSessionAttributeStore pageSessionAttributeStore() {
        return new PageSessionAttributeStore(properties);
    }

    /**
     * Must be {@code static} to be instantiated before other beans —
     * a requirement of Spring for every {@link org.springframework.beans.factory.config.BeanPostProcessor}.
     */
    @Bean
    public static PageSessionBeanPostProcessor pageSessionBeanPostProcessor(
            final ObjectProvider<PageSessionAttributeStore> storeProvider) {
        return new PageSessionBeanPostProcessor(storeProvider);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PageSessionHandlerInterceptor());
    }
}
