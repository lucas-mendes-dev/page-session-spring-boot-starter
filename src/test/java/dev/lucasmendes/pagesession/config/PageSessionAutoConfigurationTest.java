package dev.lucasmendes.pagesession.config;

import dev.lucasmendes.pagesession.processor.PageSessionBeanPostProcessor;
import dev.lucasmendes.pagesession.store.PageSessionAttributeStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import static org.assertj.core.api.Assertions.assertThat;

class PageSessionAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(PageSessionAutoConfiguration.class));

    @Test
    void testAutoConfigurationLoadsBeans() {
        this.contextRunner
                .withUserConfiguration(WebMvcConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(PageSessionAttributeStore.class);
                    assertThat(context).hasSingleBean(PageSessionBeanPostProcessor.class);
                    
                    // Verify RequestMappingHandlerAdapter session attribute store has been replaced
                    RequestMappingHandlerAdapter adapter = context.getBean(RequestMappingHandlerAdapter.class);
                    PageSessionAttributeStore store = context.getBean(PageSessionAttributeStore.class);
                    try {
                        java.lang.reflect.Field field = RequestMappingHandlerAdapter.class.getDeclaredField("sessionAttributeStore");
                        field.setAccessible(true);
                        Object actualStore = field.get(adapter);
                        assertThat(actualStore).isSameAs(store);
                    } catch (Exception e) {
                        throw new AssertionError("Could not access sessionAttributeStore field via reflection", e);
                    }
                });
    }

    @Test
    void testPropertiesAreBound() {
        this.contextRunner
                .withUserConfiguration(WebMvcConfig.class)
                .withPropertyValues("page-session.separator=-", "page-session.use-simple-name=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(PageSessionProperties.class);
                    PageSessionProperties properties = context.getBean(PageSessionProperties.class);
                    assertThat(properties.getSeparator()).isEqualTo("-");
                    assertThat(properties.isUseSimpleName()).isTrue();
                });
    }

    @Test
    void testAttributeStoreBacksOff() {
        this.contextRunner
                .withUserConfiguration(WebMvcConfig.class, CustomStoreConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(PageSessionAttributeStore.class);
                    PageSessionAttributeStore customStore = context.getBean("customAttributeStore", PageSessionAttributeStore.class);
                    PageSessionAttributeStore autoConfigStore = context.getBean(PageSessionAttributeStore.class);
                    assertThat(autoConfigStore).isSameAs(customStore);
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    static class WebMvcConfig {
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomStoreConfig {
        @Bean
        PageSessionAttributeStore customAttributeStore(PageSessionProperties properties) {
            return new PageSessionAttributeStore(properties);
        }
    }
}
