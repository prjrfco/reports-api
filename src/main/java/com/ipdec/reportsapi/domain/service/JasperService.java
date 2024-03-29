package com.ipdec.reportsapi.domain.service;

import com.ipdec.reportsapi.api.dto.RelatorioDto;
import com.ipdec.reportsapi.api.dto.RelatorioInputDto;
import com.ipdec.reportsapi.api.dto.RelatorioListaDto;
import com.ipdec.reportsapi.api.exceptionhandler.exception.EntidadeNaoEncontradaException;
import com.ipdec.reportsapi.domain.model.Relatorio;
import com.ipdec.reportsapi.domain.repository.RelatorioRepository;
import lombok.Synchronized;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JasperService {

    @Autowired
    private RelatorioRepository repository;

    private static final String JASPER_DIRETORIO = "temp";
    private static final String JASPER_PREFIXO = "relatorio_";
    private static final String JASPER_SUFIXO = ".jasper";

    @Synchronized
    public byte[] exportarPDF(String backend, RelatorioInputDto dto) throws IOException {
        byte[] bytes = null;
        Relatorio relatorio = repository.findByNomeAndBackend_Nome(dto.getNome()+JASPER_SUFIXO, backend)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Relatorio não encontrado"));

        Path tempDirectory = Files.createTempDirectory(Paths.get("target"), JASPER_DIRETORIO);

        File tempFile = File.createTempFile(JASPER_PREFIXO, JASPER_SUFIXO, tempDirectory.toFile());
        OutputStream os = new FileOutputStream(tempFile);
        os.write(relatorio.getArquivo());
        os.close();

        try (InputStream jasperFile = new FileInputStream(tempFile)) {
            JasperPrint print;

            if (dto.getParameterList().size() > 0) {
                print = JasperFillManager.fillReport(jasperFile, dto.getParams(), new JRBeanCollectionDataSource(dto.getParameterList()));
            } else {
                print = JasperFillManager.fillReport(jasperFile, dto.getParams(), new JREmptyDataSource());
            }

            bytes = JasperExportManager.exportReportToPdf(print);
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            FileUtils.forceDelete(tempFile);
            FileUtils.deleteDirectory(tempDirectory.toFile());
        }
        return bytes;
    }

    public List<RelatorioListaDto> consultarPdfs(String user) {
        return repository.findAllByBackend_Nome(user)
                .stream()
                .map(RelatorioListaDto::new)
                .collect(Collectors.toList());
    }
}
