package com.residencial.anhandui.application.factory;

import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IDeclaracaoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPagamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;
import com.residencial.anhandui.domain.usecase.EmitirDndUseCase;
import com.residencial.anhandui.domain.usecase.GerarBalanceteMensalUseCase;
import com.residencial.anhandui.domain.usecase.InicializarCondominioPadraoUseCase;
import com.residencial.anhandui.domain.usecase.RegistrarPagamentoDinheiroUseCase;
import com.residencial.anhandui.domain.usecase.RegistrarPagamentoPeriodoUseCase;
import com.residencial.anhandui.domain.usecase.SimularPlanoMuroUseCase;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public InicializarCondominioPadraoUseCase inicializarCondominioPadraoUseCase(
            IApartamentoRepositoryPort apartamentoRepository,
            ICotaRepositoryPort cotaRepository) {
        return new InicializarCondominioPadraoUseCase(apartamentoRepository, cotaRepository);
    }

    @Bean
    public RegistrarPagamentoDinheiroUseCase registrarPagamentoDinheiroUseCase(
            ICotaRepositoryPort cotaRepository,
            IApartamentoRepositoryPort apartamentoRepository,
            IPagamentoRepositoryPort pagamentoRepository,
            IPdfGeneratorPort pdfGenerator) {
        return new RegistrarPagamentoDinheiroUseCase(cotaRepository, apartamentoRepository, pagamentoRepository, pdfGenerator);
    }

    @Bean
    public RegistrarPagamentoPeriodoUseCase registrarPagamentoPeriodoUseCase(
            IApartamentoRepositoryPort apartamentoRepository,
            ICotaRepositoryPort cotaRepository,
            IPagamentoRepositoryPort pagamentoRepository,
            IPdfGeneratorPort pdfGenerator) {
        return new RegistrarPagamentoPeriodoUseCase(apartamentoRepository, cotaRepository, pagamentoRepository, pdfGenerator);
    }

    @Bean
    public EmitirDndUseCase emitirDndUseCase(
            IApartamentoRepositoryPort apartamentoRepository,
            ICotaRepositoryPort cotaRepository,
            IDeclaracaoRepositoryPort declaracaoRepository,
            IPdfGeneratorPort pdfGenerator) {
        return new EmitirDndUseCase(apartamentoRepository, cotaRepository, declaracaoRepository, pdfGenerator);
    }

    @Bean
    public SimularPlanoMuroUseCase simularPlanoMuroUseCase(
            IApartamentoRepositoryPort apartamentoRepository) {
        return new SimularPlanoMuroUseCase(apartamentoRepository);
    }

    @Bean
    public GerarBalanceteMensalUseCase gerarBalanceteMensalUseCase(
            IApartamentoRepositoryPort apartamentoRepository,
            ICotaRepositoryPort cotaRepository,
            IPdfGeneratorPort pdfGenerator) {
        return new GerarBalanceteMensalUseCase(apartamentoRepository, cotaRepository, pdfGenerator);
    }

    @Bean
    public ApplicationRunner autoInicializadorCondominio(InicializarCondominioPadraoUseCase inicializador) {
        return args -> {
            inicializador.executar();
        };
    }
}
