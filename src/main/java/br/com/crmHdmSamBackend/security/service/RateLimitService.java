package br.com.crmHdmSamBackend.security.service;
import br.com.crmHdmSamBackend.exception.RateLimitExceededException;
import br.com.crmHdmSamBackend.model.ApiRateLimit;
import br.com.crmHdmSamBackend.repository.ApiRateLimitRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;


@Service
public class RateLimitService {

    private final ApiRateLimitRepository rateLimitRepository;

    @Autowired
    public RateLimitService(ApiRateLimitRepository rateLimitRepository) {
        this.rateLimitRepository = rateLimitRepository;
    }

    public void validarRateLimit(String identificador, String endpoint, int limiteRequisicoes, long janelaMinutos) {
        Optional<ApiRateLimit> rateLimitOpt = rateLimitRepository
                .findByIdentificadorAndEndpoint(identificador, endpoint);

        if (rateLimitOpt.isPresent()) {
            ApiRateLimit rateLimit = rateLimitOpt.get();

            if (rateLimit.janelaExpirou(janelaMinutos)) {
                rateLimit.resetar();
                rateLimitRepository.save(rateLimit);
                return;
            }

            if (rateLimit.getRequisicoes() >= limiteRequisicoes) {
                long minutosRestantes = janelaMinutos -
                        java.time.Duration.between(rateLimit.getJanelaInicio(), OffsetDateTime.now()).toMinutes();
                throw new RateLimitExceededException(
                        "Limite de requisições excedido. Tente novamente em " + minutosRestantes + " minutos."
                );
            }

            rateLimit.incrementar();
            rateLimitRepository.save(rateLimit);
        } else {
            ApiRateLimit novoRateLimit = new ApiRateLimit(identificador, endpoint);
            rateLimitRepository.save(novoRateLimit);
        }
    }

    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void limparRateLimitsAntigos() {
        OffsetDateTime limite = OffsetDateTime.now().minusHours(2);
        rateLimitRepository.limparJanelasAntigas(limite);
    }
}