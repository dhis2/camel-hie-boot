## Camel HIE Boot

[Apache Camel](https://camel.apache.org/) tailored for HIE.

Bootstrap your Java integration with Camel HIE by extending the app main class from `org.hisp.hieboot.camel.CamelHieBootApp` like so:

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
