//package br.com.crmHdmSamBackend;
//
//import br.com.crmHdmSamBackend.model.Usuario;
//import br.com.crmHdmSamBackend.service.UsuarioService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//@Component
//public class Outra implements CommandLineRunner {
//
//    @Autowired
//    private UsuarioService us;
//
//    @Override
//    public void run(String... args) throws Exception {
//        Usuario usuario = new Usuario();
//        usuario.setNome("predoteste");
//        usuario.setEmail("emailteste@email.com");
//        usuario.setTelefone("3432432423");
//
//        us.cirar(usuario);
//        System.out.println("Usuário criado com sucesso!");
//    }
//}