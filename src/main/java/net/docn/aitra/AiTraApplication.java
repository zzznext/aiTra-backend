package net.docn.aitra;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("net.docn.aitra.web.generator.mapper")
public class AiTraApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiTraApplication.class, args);
    }

}
