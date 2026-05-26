package pdv.contrato;

public interface IFormaPagamento {
    String getNome();
    String getIcone();
    boolean processarPagamento(double valorTotal, double valorPago);
    double calcularTroco(double valorTotal, double valorPago);
    boolean permiteTroco();
    String getInstrucoes();
}