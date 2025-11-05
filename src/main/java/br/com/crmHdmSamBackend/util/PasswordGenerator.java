package br.com.crmHdmSamBackend.util;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        String senha = "admin123";
        String hashDoBanco = "$2a$12$4twRzUPimw.K4a8maPaw7.Uq4rdUYn0JXnrs7RwxtfDDRBrlZrToq";

        System.out.println("========================================");
        System.out.println("TESTE DE SENHA BCrypt");
        System.out.println("========================================");
        System.out.println("Senha de teste: " + senha);
        System.out.println("Hash do banco: " + hashDoBanco);
        System.out.println();

        boolean matches = encoder.matches(senha, hashDoBanco);
        System.out.println("✓ RESULTADO DO MATCHES: " + matches);
        System.out.println();

        if (matches) {
            System.out.println("✓✓✓ SUCESSO! A senha 'admin123' bate com o hash do banco!");
            System.out.println("✓✓✓ O problema está em outro lugar!");
        } else {
            System.out.println("✗✗✗ FALHA! A senha 'admin123' NÃO bate com o hash do banco!");
            System.out.println("✗✗✗ O hash no banco pode estar corrompido!");
            System.out.println();
            System.out.println("Gerando novo hash para 'admin123':");
            String novoHash = encoder.encode(senha);
            System.out.println(novoHash);
            System.out.println();
            System.out.println("Use este SQL para atualizar:");
            System.out.println("UPDATE usuarios SET senha = '" + novoHash + "' WHERE login = 'admin';");
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("TESTE DE CARACTERES ESPECIAIS");
        System.out.println("========================================");

        String[] testesSenhas = {
                "admin123",
                " admin123",
                "admin123 ",
                " admin123 "
        };

        for (String teste : testesSenhas) {
            boolean match = encoder.matches(teste, hashDoBanco);
            System.out.println("Senha: '" + teste + "' (length: " + teste.length() + ") → matches: " + match);
        }
    }
}