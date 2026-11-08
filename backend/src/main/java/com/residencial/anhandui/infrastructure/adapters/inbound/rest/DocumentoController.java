package com.residencial.anhandui.infrastructure.adapters.inbound.rest;

import com.residencial.anhandui.domain.exception.EntidadeNaoEncontradaException;
import com.residencial.anhandui.domain.model.CategoriaDocumento;
import com.residencial.anhandui.domain.model.DocumentoCondominio;
import com.residencial.anhandui.domain.ports.output.IDocumentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IDocumentoStoragePort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = "*")
public class DocumentoController {

    private final IDocumentoRepositoryPort documentoRepository;
    private final IDocumentoStoragePort storagePort;

    public DocumentoController(IDocumentoRepositoryPort documentoRepository,
                               IDocumentoStoragePort storagePort) {
        this.documentoRepository = documentoRepository;
        this.storagePort = storagePort;
    }

    @GetMapping
    public ResponseEntity<List<DocumentoCondominio>> listarTodos() {
        return ResponseEntity.ok(documentoRepository.listarTodos());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoCondominio> upload(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("titulo") String titulo,
            @RequestParam("categoria") String categoriaStr,
            @RequestParam("localizacaoFisica") String localizacaoFisica) throws IOException {

        CategoriaDocumento cat;
        try {
            cat = CategoriaDocumento.valueOf(categoriaStr);
        } catch (Exception e) {
            cat = CategoriaDocumento.OUTRO;
        }

        String caminhoRelativo = storagePort.salvarArquivo(arquivo.getOriginalFilename(), arquivo.getBytes());

        DocumentoCondominio doc = new DocumentoCondominio(
                UUID.randomUUID(),
                titulo,
                cat,
                arquivo.getOriginalFilename(),
                caminhoRelativo,
                localizacaoFisica,
                LocalDateTime.now()
        );

        DocumentoCondominio salvo = documentoRepository.salvar(doc);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        DocumentoCondominio doc = documentoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Documento não encontrado com ID: " + id));

        byte[] conteudo = storagePort.lerArquivo(doc.getCaminhoArquivo());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNomeArquivo() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(conteudo);
    }
}
