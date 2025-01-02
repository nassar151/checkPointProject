package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "org.example",
        "com.amdocs.queryexec.utilities.repository",
        "com.amdocs.queryexec.utilities.model",
        "com.amdocs.queryexec.utilities",
        "com.amdocs.queryexec.utilities.config"
        // Add this line to include the security config package

})
@EnableJpaRepositories(basePackages = "com.amdocs.queryexec.utilities.repository")
@EntityScan(basePackages = "com.amdocs.queryexec.utilities.model")

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}