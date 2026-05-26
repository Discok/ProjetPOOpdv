package pdv.servico;

import pdv.contrato.IFormaPagamento;

public class PagamentoCartaoDebito implements IFormaPagamento {
    
    @Override
    public String getNome() {
        return "Cartão Débito";
    }
    
    @Override
    public String getIcone() {
        return "💳";
    }
    
    @Override
    public boolean processarPagamento(double valorTotal, double valorPago) {
        return true;
    }
    
    @Override
    public double calcularTroco(double valorTotal, double valorPago) {
        return 0;
    }
    
    @Override
    public boolean permiteTroco() {
        return false;
    }
    
    @Override
    public String getInstrucoes() {
        return "Aproxime ou insira o cartão na maquininha";
    }
}