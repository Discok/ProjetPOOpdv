package pdv.servico;

import pdv.contrato.IFormaPagamento;

public class PagamentoCartaoCredito implements IFormaPagamento {
    private int parcelas;
    
    public PagamentoCartaoCredito() {
        this.parcelas = 1;
    }
    
    public PagamentoCartaoCredito(int parcelas) {
        this.parcelas = parcelas;
    }
    
    @Override
    public String getNome() {
        return parcelas > 1 ? String.format("Cartão Crédito (%dx)", parcelas) : "Cartão Crédito";
    }
    
    @Override
    public String getIcone() {
        return "💳";
    }
    
    @Override
    public boolean processarPagamento(double valorTotal, double valorPago) {
        return true; // Cartão sempre aprova
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
    
    public double getValorParcela(double valorTotal) {
        return valorTotal / parcelas;
    }
}