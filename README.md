# page-session-spring-boot-starter

[![CI](https://github.com/lucas-mendes-dev/page-session-spring-boot-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/lucas-mendes-dev/page-session-spring-boot-starter/actions/workflows/ci.yml)

A replacement for `@SessionAttributes` that isolates session attributes by controller, eliminating naming conflicts when the user navigates between different pages.

## Problem

Two controllers that declare the same attribute name in `@SessionAttributes` share the same key in the `HttpSession`:

```java
// Both declare "items" → conflict!
@SessionAttributes("items")
public class FooController { ... }

@SessionAttributes("items")
public class BarController { ... }
```

## Solution

Replace `@SessionAttributes` with `@PageSessionAttributes`. The prefix is applied automatically and transparently — your Thymeleaf templates and `Model` continue to use `"items"`:

```java
@PageSessionAttributes("items")
public class FooController { ... }

@PageSessionAttributes("items")  // no conflict
public class BarController { ... }
```

## Installation

Add the repository and dependency to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/lucas-mendes-dev/page-session-spring-boot-starter")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("io.github.lucas-mendes-dev:page-session-spring-boot-starter:1.0.0")
}
```

## Configuration

No configuration is required. Optionally, in `application.properties`:

```properties
# Separator between class name and attribute name (default: ".")
page-session.separator=.

# true = uses getSimpleName() instead of fully qualified name (default: false)
# Warning: may cause conflicts if two controllers from different packages
# have the same simpleName
page-session.use-simple-name=false
```

## How it works

In the `HttpSession`, keys are prefixed by the fully qualified class name:

| Attribute in controller | Real key in session |
|---|---|
| `"items"` in `FooController` | `com.example.FooController.items` |
| `"items"` in `BarController` | `com.example.BarController.items` |

`SessionStatus.setComplete()` continues to work normally.

## Requirements

- Java 25+
- Spring Boot 4.x
- Spring MVC (servlet, not reactive)
