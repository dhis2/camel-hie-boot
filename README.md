## Camel HIE Boot

Opinionated [Apache Camel](https://camel.apache.org/) tailored for HIE. Learn about Apache Camel from our Camel primer on the [DHIS2 Developer Portal](https://developers.dhis2.org/docs/integration/apache-camel/). 

### Getting started

Bootstrap your Java integration with Camel HIE by extending the app main class from `org.hisp.hieboot.CamelHieBootApp` like so:

```java
package org.example;

import org.hisp.hieboot.CamelHieBootApp;
import org.springframework.boot.SpringApplication;

public class Main extends CamelHieBootApp {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

### [Kamelet Catalog](kamelet-catalog.md)


