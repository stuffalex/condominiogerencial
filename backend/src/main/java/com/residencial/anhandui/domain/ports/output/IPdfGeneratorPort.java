package com.residencial.anhandui.domain.ports.output;

import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BalanceteMensal;
import com.residencial.anhandui.domain.model.CotaCondominial;
import com.residencial.anhandui.domain.model.DeclaracaoNegativaDebito;
import com.residencial.anhandui.domain.model.PagamentoDinheiro;

import java.util.List;

public interface IPdfGeneratorPort {
    byte[] gerarReciboDinheiroPdf(PagamentoDinheiro pagamento, Apartamento apartamento, CotaCondominial cota);
    byte[] gerarReciboConsolidadoPeriodoPdf(PagamentoDinheiro pagamento, Apartamento apartamento, List<CotaCondominial> cotas);
    byte[] gerarDndPdf(DeclaracaoNegativaDebito dnd, Apartamento apartamento);
    byte[] gerarBalanceteMensalPdf(BalanceteMensal balancete);
}
