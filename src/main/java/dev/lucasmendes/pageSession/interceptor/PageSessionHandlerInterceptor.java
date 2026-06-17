package dev.lucasmendes.pageSession.interceptor;

import dev.lucasmendes.pageSession.store.PageSessionAttributeStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Handler interceptor that registers the active controller class in the current request,
 * allowing the {@link PageSessionAttributeStore} to apply the correct prefix.
 *
 * <p>Must be registered before any other interceptor that needs
 * the isolated session attribute.
 */
public class PageSessionHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        if(handler instanceof HandlerMethod handlerMethod) {
            // Make the controller type available to the store in this request scope
            request.setAttribute(
                    PageSessionAttributeStore.HANDLER_CLASS_ATTR,
                    handlerMethod.getBeanType()
            );
        }
        return true;
    }
}
