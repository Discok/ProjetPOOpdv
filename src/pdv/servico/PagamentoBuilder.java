package pdv.servico;

import java.util.*;
import pdv.contrato.IFormaPagamento;

public class PagamentoBuilder {
    private static final Map<String, IFormaPagamento> formasPagamento = new LinkedHashMap<>();
    
    static {
        formasPagamento.put("Dinheiro", new PagamentoDinheiro());
        formasPagamento.put("Cartão Débito", new PagamentoCartaoDebito());
        formasPagamento.put("Cartão Crédito (1x)", new PagamentoCartaoCredito(1));
        formasPagamento.put("Cartão Crédito (2x)", new PagamentoCartaoCredito(2));
        formasPagamento.put("Cartão Crédito (3x)", new PagamentoCartaoCredito(3));
        // PIX e VALE removidos
    }
    
    public static List<String> getFormasDisponiveis() {
        return new ArrayList<>(formasPagamento.keySet());
    }
    
    public static IFormaPagamento getForma(String nome) {
        return formasPagamento.getOrDefault(nome, new PagamentoDinheiro());
    }
    
    public static IFormaPagamento getFormaPadrao() {
        return new PagamentoDinheiro();
    }
}