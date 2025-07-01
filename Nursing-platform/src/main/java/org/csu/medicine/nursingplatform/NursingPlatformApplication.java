package org.csu.medicine.nursingplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NursingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(NursingPlatformApplication.class, args);
    }

}
