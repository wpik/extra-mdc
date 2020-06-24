# extra-mdc

- [About MDC](#about-mdc)
- [About the library](#about-the-library)
- [Modules](#modules)
- [Usage](#usage)
  - [Spring boot starter](#spring-boot-starter)
  - [Annotate methods and its parameters](#annotate-methods-and-its-parameters)
  - [Expressions](#expressions)
- [Configuration](#configuration)
  - [Disabling library](#disabling-library)
  - [Adding MDC to log messages](#adding-mdc-to-log-messages)
- [Troubleshooting](#troubleshooting)

# About MDC

A MDC (_Mapped Diagnostic Context_) is a Java Map where you put any diagnostic information you consider useful.
Then this information can be added to each log line. Each thread has its own MDC map.

You can use MDC to correlate log messages for the same request. Just insert a correlationId or requestId to it. You can
also add other information like operation type, customerId, and others.

For more information about MDC please check [SLF4J documentation](http://www.slf4j.org/manual.html) and
[Logback manual](http://logback.qos.ch/manual/mdc.html).

# About the library

`extra-mdc` is a simple library for filling MDC in a declarative way. Instead of manually putting data into MDC you
use Java Annotations to mark method parameters which values are put into MDC. After method execution, the parameters are 
removed from MDC so their values don't show in the logs when a thread starts processing another request. 

# Modules

Project consist of the following modules:

- `aspect` - Core of the library. Contains Java annotations and aspect. You can manually include it in your project.
- `spring-boot-starter` - A Spring Boot Starter that can be used to seamlessly integrate `extra-mdc` with your Spring 
                          application.
- `example` - A sample project showing how to use the library.

# Usage

Please check the [example](./example) to see how the library can be integrated into an existing Spring application.

## Spring boot starter

To integrate `extra-mdc` with your Spring application add the `spring-boot-starter` dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.wpik.extra-mdc</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Annotate methods and its parameters

Decide which methods have parameters that you want to put into MDC. Each such method must be annotated with `@ExtraMdc`.
Each method parameter that you want to put into MDC should be annotated with `@MdcField`.

In the following example, for each invocation of the `echo` method, a value of `msg` parameter will be put into MDC 
under the key `message`.

```java
class SomeClass{
    @ExtraMdc
    String echo(@MdcField("message") String msg) {
        return "ECHO: " + msg;
    }
}
``` 

You can also use `@MdcField`'s `name` parameter to define the key name in the MDC. It is an alias for `value` just for
convenience. The `@MdcField` must have `value` or `name` defined. Otherwise, the warning is generated during the 
runtime.

## Expressions

By default, the library uses the `String.valueOf` to calculate the value put into MDC entry. But you can modify this 
behavior by using `@MdcField`'s `expression` parameter. You can define the 
[SpEL](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#expressions) expression that will
be used to calculate the value put into MDC entry. This feature is useful when the method parameter is a POJO and
you want to put into MDC just one of its fields.

The `@MdcField` is repeatable which means you can define multiple `@MdcField` annotation on a single method parameter. 
This feature is useful when the method parameter is a POJO and you want to add a few of its fields into MDC.

In the following example, only the book's author and title is added into MDC:

```java   
class Book {
    private final String author;
    private final String title;
    private final String isbn;
    // other fields
    
    // constructor / getters / setters omitted for simplicity
}

class LibraryService {
    
    @ExtraMdc
    void book(
            @MdcField(name = "author", expression = "author")
            @MdcField(name = "title", expression = "title")
            Book book)
    {
        // logic omitted
    }
}
```

# Configuration

## Disabling library

When using `spring-boot-starter` you can disable library by defining property: `extra-mdc.enabled=false`

## Adding MDC to log messages

To display MDC in log messages you need to configure the logging framework logging pattern. When using Spring Boot you 
can add the following property: `logging.pattern.level='%5p [%X]'`

# Troubleshooting

When using `spring-boot-starter`, the autoconfiguration puts the following log line when the library gets activated: 
`Auto configuring com.github.wpik.extramdc.aspect.ExtraMdcAspect`.

If you don't see such message it means the library was not activated.
