package br.com.crmHdmSamBackend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HdmsamApplication {
    @Autowired
    UsuarioRepository repo;


    public static void main(String[] args) {
        SpringApplication.run(HdmsamApplication.class, args);



    }

}
