package dev.lucasmendes.pageSession.store;

import dev.lucasmendes.pageSession.annotation.PageSessionAttributes;
import dev.lucasmendes.pageSession.config.PageSessionProperties;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.context.request.WebRequest;

/**
 * Implementation of {@link SessionAttributeStore} that prefixes session keys
 * with the fully qualified name of the controller annotated with {@link PageSessionAttributes}.
 *
 * <p>The prefix is transparent: the controller and templates continue to use
 * short names (ex: {@code "items"}), while the physical key in the
 * {@link jakarta.servlet.http.HttpSession} is isolated by controller
 * (ex: {@code "com.example.FooController.items"}).
 *
 * <p>Controllers without {@link PageSessionAttributes} continue with behavior
 * identical to Spring's standard.
 */
public class PageSessionAttributeStore implements SessionAttributeStore {

    /**
     * Key of the request attribute where the interceptor registers the active controller class.
     */
    public static final String HANDLER_CLASS_ATTR = PageSessionAttributeStore.class.getName() + ".HANDLER_CLASS";

    private final PageSessionProperties properties;

    public PageSessionAttributeStore(final PageSessionProperties properties) {
        this.properties = properties;
    }

    @Override
    public void storeAttribute(WebRequest request, @NonNull String attributeName, @NonNull Object attributeValue) {
        request.setAttribute(
                buildKey(request, attributeName),
                attributeValue,
                WebRequest.SCOPE_SESSION
        );
    }

    @Override
    @Nullable
    public Object retrieveAttribute(WebRequest request, @NonNull String attributeName) {
        return request.getAttribute(
                buildKey(request, attributeName),
                WebRequest.SCOPE_SESSION
        );
    }

    @Override
    public void cleanupAttribute(WebRequest request, @NonNull String attributeName) {
        request.removeAttribute(
                buildKey(request, attributeName),
                WebRequest.SCOPE_SESSION
        );
    }

    /**
     * Builds the session key. For controllers with {@link PageSessionAttributes},
     * prefixes with the class name. Otherwise, returns the original name.
     */
    private String buildKey(final WebRequest request, final String attributeName) {
        final var handlerClass = request.getAttribute(HANDLER_CLASS_ATTR, WebRequest.SCOPE_REQUEST);

        if(handlerClass instanceof Class<?> cls && cls.isAnnotationPresent(PageSessionAttributes.class)) {
            final var prefix = properties.isUseSimpleName()
                               ? cls.getSimpleName()
                               : cls.getName();

            return prefix + properties.getSeparator() + attributeName;
        }

        return attributeName;
    }
}
