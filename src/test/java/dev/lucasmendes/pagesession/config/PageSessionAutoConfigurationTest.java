package dev.lucasmendes.pagesession.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner; // Import importante
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.lang.reflect.Field;

import dev.lucasmendes.pagesession.processor.PageSessionBeanPostProcessor;
import dev.lucasmendes.pagesession.store.PageSessionAttributeStore;

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
                    final var adapter = context.getBean(RequestMappingHandlerAdapter.class);
                    final var store = context.getBean(PageSessionAttributeStore.class);
                    try {
                        final Field field
                                = RequestMappingHandlerAdapter.class.getDeclaredField("sessionAttributeStore");
                        field.setAccessible(true);
                        final Object actualStore = field.get(adapter);
                        assertThat(actualStore).isSameAs(store);
                    } catch(Exception e) {
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
                    final PageSessionProperties properties = context.getBean(PageSessionProperties.class);
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
                    final PageSessionAttributeStore customStore = context.getBean("customAttributeStore",
                                                                                  PageSessionAttributeStore.class);
                    final PageSessionAttributeStore autoConfigStore = context.getBean(PageSessionAttributeStore.class);
                    assertThat(autoConfigStore).isSameAs(customStore);
                });
    }

    // --- NOVOS TESTES ESSENCIAIS ABAIXO ---

    @Test
    void testAutoConfigurationBacksOffInNonWebApplication() {
        // Usamos ApplicationContextRunner para simular um ambiente que NÃO é Web (ex: Console/Batch)
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(PageSessionAutoConfiguration.class))
                .run(context -> {
                    // Os beans NÃO devem ser carregados para não quebrar a aplicação do usuário
                    assertThat(context).doesNotHaveBean(PageSessionAttributeStore.class);
                    assertThat(context).doesNotHaveBean(PageSessionBeanPostProcessor.class);
                });
    }

    @Test
    void testAutoConfigurationBacksOffWhenRequiredClassIsMissing() {
        this.contextRunner
                .withUserConfiguration(WebMvcConfig.class)
                // Simula que a classe RequestMappingHandlerAdapter não está no classpath (ex: projeto WebFlux puro)
                .withClassLoader(new FilteredClassLoader(RequestMappingHandlerAdapter.class))
                .run(context -> {
                    assertThat(context).doesNotHaveBean(PageSessionAttributeStore.class);
                    assertThat(context).doesNotHaveBean(PageSessionBeanPostProcessor.class);
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
