package com.residencial.anhandui.infrastructure.adapters.inbound.rest;

import com.residencial.anhandui.domain.exception.EntidadeNaoEncontradaException;
import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.Pessoa;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.infrastructure.adapters.inbound.rest.dto.AtualizarMoradorRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apartamentos")
@CrossOrigin(origins = "*")
public class ApartamentoController {

    private final IApartamentoRepositoryPort apartamentoRepository;

    public ApartamentoController(IApartamentoRepositoryPort apartamentoRepository) {
        this.apartamentoRepository = apartamentoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Apartamento>> listarTodos() {
        List<Apartamento> lista = apartamentoRepository.listarTodos().stream()
                .sorted(Comparator.comparing(Apartamento::getBloco).thenComparing(Apartamento::getNumero))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Apartamento> buscarPorId(@PathVariable UUID id) {
        return apartamentoRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/morador")
    public ResponseEntity<Apartamento> atualizarMorador(@PathVariable UUID id, @RequestBody AtualizarMoradorRequest request) {
        Apartamento apto = apartamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Apartamento não encontrado com ID: " + id));

        Pessoa prop = new Pessoa(
                request.proprietarioNome(),
                request.proprietarioCpf(),
                request.proprietarioTelefone(),
                request.proprietarioEmail(),
                request.proprietarioObservacao()
        );

        Pessoa morador = new Pessoa(
                request.moradorNome(),
                request.moradorCpf(),
                request.moradorTelefone(),
                request.moradorEmail(),
                request.moradorObservacao()
        );

        apto.setProprietario(prop);
        apto.setMorador(morador);

        Apartamento salvo = apartamentoRepository.salvar(apto);
        return ResponseEntity.ok(salvo);
    }
}
