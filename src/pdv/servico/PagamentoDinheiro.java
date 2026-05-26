package pdv.servico;

import pdv.contrato.IFormaPagamento;

public class PagamentoDinheiro implements IFormaPagamento {
    
    @Override
    public String getNome() {
        return "Dinheiro";
    }
    
    @Override
    public String getIcone() {
        return "💰";
    }
    
    @Override
    public boolean processarPagamento(double valorTotal, double valorPago) {
        return valorPago >= valorTotal;
    }
    
    @Override
    public double calcularTroco(double valorTotal, double valorPago) {
        return valorPago - valorTotal;
    }
    
    @Override
    public boolean permiteTroco() {
        return true;
    }
    
    @Override
    public String getInstrucoes() {
        return "Informe o valor recebido em dinheiro";
    }
}