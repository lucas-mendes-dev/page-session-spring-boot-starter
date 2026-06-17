package dev.lucasmendes.pagesession;

import dev.lucasmendes.pagesession.config.PageSessionAutoConfiguration;
import dev.lucasmendes.pagesession.controller.BarController;
import dev.lucasmendes.pagesession.controller.FooController;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = { FooController.class, BarController.class },
        properties = "spring.main.banner-mode=off"
)
@Import(PageSessionAutoConfiguration.class)
class PageSessionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve isolar atributos de sessão com o mesmo nome em controllers diferentes sem conflitos")
    void shouldIsolateSessionAttributesBetweenDifferentControllers() throws Exception {
        // Criamos uma única sessão HTTP simulando o mesmo usuário navegando nas duas páginas
        final var session = new MockHttpSession();

        // 1. Acessa FooController e define o valor "value-from-foo" para a chave "items"
        mockMvc.perform(get("/foo/set").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("items", "value-from-foo"));

        // 2. VALIDAÇÃO DA SESSÃO REAL: O Spring deve ter salvo na sessão usando o prefixo da classe Foo
        final var fooSessionKey = "dev.lucasmendes.pagesession.controller.FooController.items";
        assertEquals("value-from-foo", session.getAttribute(fooSessionKey));

        // 3. Acessa BarController e define o valor "value-from-bar" para a chave "items"
        mockMvc.perform(get("/bar/set").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("items", "value-from-bar"));

        // 4. VALIDAÇÃO DA SESSÃO REAL: O Spring deve ter salvo na sessão usando o prefixo da classe Bar
        final var barSessionKey = "dev.lucasmendes.pagesession.controller.BarController.items";
        assertEquals("value-from-bar", session.getAttribute(barSessionKey));

        // 5. PROVA DE FOGO (Não houve sobrescrita): O valor de Foo na sessão continua intacto e isolado!
        assertNotEquals(session.getAttribute(fooSessionKey), session.getAttribute(barSessionKey));
        assertEquals("value-from-foo", session.getAttribute(fooSessionKey));

        // 6. VALIDAÇÃO DO MODEL: Ao voltar para o FooController, o @ModelAttribute deve recuperar o valor correto e
        // limpo
        mockMvc.perform(get("/foo/get").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("items", "value-from-foo"));

        // 7. VALIDAÇÃO DO MODEL: Ao voltar para o BarController, recupera o valor correspondente a ele
        mockMvc.perform(get("/bar/get").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("items", "value-from-bar"));
    }
}
