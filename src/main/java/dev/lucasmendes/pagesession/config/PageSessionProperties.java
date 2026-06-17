package dev.lucasmendes.pagesession.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Page Session starter.
 * <p>
 * Configurable via application.properties or application.yml.
 */
@ConfigurationProperties(prefix = "page-session")
public class PageSessionProperties {

    /**
     * Separator between controller class name and attribute name in the session key.
     */
    private String separator = ".";

    /**
     * Whether to use Class#getSimpleName instead of the fully qualified class name.
     * <p>
     * Using simple names may cause conflicts when controllers in different packages share the same class name.
     */
    private boolean useSimpleName = false;

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public boolean isUseSimpleName() {
        return useSimpleName;
    }

    public void setUseSimpleName(boolean useSimpleName) {
        this.useSimpleName = useSimpleName;
    }
}
