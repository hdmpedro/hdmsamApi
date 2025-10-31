package br.com.crmHdmSamBackend;

import br.com.crmHdmSamBackend.model.Usuario;
import br.com.crmHdmSamBackend.repository.UsuarioRepository;
import br.com.crmHdmSamBackend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@SpringBootApplication
public class HdmsamApplication {
    @Autowired
    UsuarioRepository repo;


    public static void main(String[] args) {
        SpringApplication.run(HdmsamApplication.class, args);



    }

}
