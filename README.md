# Spring Cloud Ribbon Discovery Server filter

> A Spring Cloud Ribbon extension for filtering the server list.

[![Build Status](https://travis-ci.org/jmnarloch/ribbon-discovery-filter-spring-cloud-starter.svg?branch=master)](https://travis-ci.org/jmnarloch/ribbon-discovery-filter-spring-cloud-starter)
[![Coverage Status](https://coveralls.io/repos/jmnarloch/ribbon-discovery-filter-spring-cloud-starter/badge.svg?branch=master&service=github)](https://coveralls.io/github/jmnarloch/ribbon-discovery-filter-spring-cloud-starter?branch=master)

## Features

Allows to specify the criteria based on which the Ribbon load balanced servers lists will be chosen during runtime.
It will affect the list of servers provided for instance through Eureka and allow to filter them based on presence of
specific settings.

## Setup

Add the Spring Cloud starter to your project:

```xml
<dependency>
  <groupId>io.jmnarloch</groupId>
  <artifactId>ribbon-discovery-filter-spring-cloud-starter</artifactId>
  <version>2.0.1</version>
</dependency>
```

## Usage

The extension specifies a custom Ribbon rule - `DiscoveryEnabledRule` an abstract class through which you can provide
your own filtering logic, currently it's only implementation is `MetadataAwareRule` that match the specified attribute
against the registered service instance metadata map. The API allows to specify the expected attributes through
RibbonFilterContextHolder at runtime.

The extension defines the glue code to perform the server filtering, but it's up to specific use case how exactly this
is going to be used.

Example:

Let's consider situation when you have deployed multiple versions of the same application (by branching your codebase)
overtime and you deploy and run them in your system simultaneously. You need to route your versioned requests towards
correct services. A simple approach would be to prefix the name of your service like for instance `recommendations-v1.0`
or `recommendations-v1.1` etc. This is going to work, but does not provide a very flexible solution. A more general
approach would be to facilitate the metadata associated with your discovery service and add logic for filtering the services.

```
eureka:
  instance:
    metadataMap:
      version: 1.1
      variant: A
```

To route the Ribbon request towards services with specific `metadataMap` entries you need to populate the thread bound
`RibbonFilterContext`:

```java

RibbonFilterContextHolder.getCurrentContext()
                .add("version", "1.1")
                .add("variant", "A");

```

You can place such code in your application logic or in some more convenient place like for instance RestTemplate's
ClientHttpRequestInterceptor for more generic approaches.

You may also provide your own custom logic, the only requirement is to implement and register instance of
`DiscoveryEnabledRule` in your Spring application context.

## Limitations

Due to lack of proper abstraction in Spring Cloud, this extension is targeting only Netflix Eureka, it's not going to
work if you will use Consul or Zookeeper as your Spring Cloud enabled discovery services.

## Properties

You can disable the extension through `ribbon.filter.metadata.enabled` property.

```

ribbon.filter.metadata.enabled=true # true by default

```

## License

Apache 2.0