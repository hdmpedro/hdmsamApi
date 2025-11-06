package br.com.crmHdmSamBackend.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TentativaLoginService {

    private static final Logger log = LoggerFactory.getLogger(TentativaLoginService.class);

    private static final int MAX_TENTATIVAS = 5;
    private static final int TEMPO_BLOQUEIO_MINUTOS = 15;
    private static final int JANELA_LIMPEZA_MINUTOS = 60;

    private final Map<String, TentativaLogin> tentativasPorIp = new ConcurrentHashMap<>();

    public void registrarTentativaFalha(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            log.warn("IP nulo ou vazio recebido para registro de tentativa");
            return;
        }

        limparTentativasAntigas();

        TentativaLogin tentativa = tentativasPorIp.computeIfAbsent(ip, k -> new TentativaLogin());
        tentativa.incrementar();

        log.info("Tentativa falha registrada para IP: {} - Total: {}/{}", ip, tentativa.getContador(), MAX_TENTATIVAS);

        if (tentativa.getContador() >= MAX_TENTATIVAS) {
            tentativa.bloquear(TEMPO_BLOQUEIO_MINUTOS);
            log.warn("IP BLOQUEADO por {} minutos: {} - {} tentativas", TEMPO_BLOQUEIO_MINUTOS, ip, tentativa.getContador());
        }
    }

    public void registrarTentativaSucesso(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return;
        }

        tentativasPorIp.remove(ip);
        log.info("Tentativas resetadas para IP após sucesso: {}", ip);
    }

    public boolean isIpBloqueado(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }

        limparTentativasAntigas();

        TentativaLogin tentativa = tentativasPorIp.get(ip);
        if (tentativa == null) {
            return false;
        }

        boolean bloqueado = tentativa.estaBloqueado();
        if (bloqueado) {
            log.warn("Acesso negado para IP bloqueado: {} - Bloqueado até: {}", ip, tentativa.getBloqueadoAte());
        }

        return bloqueado;
    }

    public int getTentativasRestantes(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return MAX_TENTATIVAS;
        }

        TentativaLogin tentativa = tentativasPorIp.get(ip);
        if (tentativa == null) {
            return MAX_TENTATIVAS;
        }

        return Math.max(0, MAX_TENTATIVAS - tentativa.getContador());
    }

    public LocalDateTime getTempoDesbloqueio(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return null;
        }

        TentativaLogin tentativa = tentativasPorIp.get(ip);
        if (tentativa == null || !tentativa.estaBloqueado()) {
            return null;
        }

        return tentativa.getBloqueadoAte();
    }

    private void limparTentativasAntigas() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(JANELA_LIMPEZA_MINUTOS);
        tentativasPorIp.entrySet().removeIf(entry -> {
            TentativaLogin tentativa = entry.getValue();
            boolean remover = tentativa.getPrimeiraTentativa().isBefore(limite) && !tentativa.estaBloqueado();
            if (remover) {
                log.debug("Limpando tentativas antigas do IP: {}", entry.getKey());
            }
            return remover;
        });
    }

    public void limparBloqueio(String ip) {
        if (ip != null && !ip.trim().isEmpty()) {
            tentativasPorIp.remove(ip);
            log.info("Bloqueio removido manualmente para IP: {}", ip);
        }
    }

    public Map<String, TentativaLogin> obterTodasTentativas() {
        limparTentativasAntigas();
        return new ConcurrentHashMap<>(tentativasPorIp);
    }

    public static class TentativaLogin {
        private int contador = 0;
        private LocalDateTime primeiraTentativa = LocalDateTime.now();
        private LocalDateTime ultimaTentativa = LocalDateTime.now();
        private LocalDateTime bloqueadoAte = null;

        public void incrementar() {
            contador++;
            ultimaTentativa = LocalDateTime.now();
        }

        public void bloquear(int minutos) {
            bloqueadoAte = LocalDateTime.now().plusMinutes(minutos);
        }

        public boolean estaBloqueado() {
            if (bloqueadoAte == null) {
                return false;
            }
            if (LocalDateTime.now().isAfter(bloqueadoAte)) {
                bloqueadoAte = null;
                contador = 0;
                return false;
            }
            return true;
        }

        public int getContador() {
            return contador;
        }

        public LocalDateTime getPrimeiraTentativa() {
            return primeiraTentativa;
        }

        public LocalDateTime getUltimaTentativa() {
            return ultimaTentativa;
        }

        public LocalDateTime getBloqueadoAte() {
            return bloqueadoAte;
        }
    }
}