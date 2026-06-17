package dev.lucasmendes.pageSession.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.lang.annotation.*;

/**
 * A {@link SessionAttributes} replacement that isolates session attributes
 * by controller using the class name as an automatic prefix.
 *
 * <p>Two controllers can declare the same attribute name without conflict:
 * <pre>{@code
 * @PageSessionAttributes("items")
 * public class FooController { ... }
 *
 * @PageSessionAttributes("items")
 * public class BarController { ... }
 * }</pre>
 * <p>
 * In the HTTP session, the keys will be:
 * <ul>
 *   <li>{@code com.example.FooController.items}</li>
 *   <li>{@code com.example.BarController.items}</li>
 * </ul>
 * <p>
 * In Thymeleaf templates and {@link org.springframework.ui.Model},
 * the name remains simply {@code "items"}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SessionAttributes
public @interface PageSessionAttributes {

    /**
     * Session attribute names — equivalent to {@link SessionAttributes#names()}.
     */
    @AliasFor(
            annotation = SessionAttributes.class,
            attribute = "names"
    )
    String[] value() default {};

    /**
     * Session attribute types — equivalent to {@link SessionAttributes#types()}.
     */
    @AliasFor(
            annotation = SessionAttributes.class,
            attribute = "types"
    )
    Class<?>[] types() default {};
}
