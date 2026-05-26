package pdv.contrato;

import pdv.modelo.Venda;

public interface INotaFiscal {
    void emitir(Venda venda);
    String getTipo();
}