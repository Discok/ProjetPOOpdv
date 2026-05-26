package pdv.contrato;

import java.util.List;
import pdv.modelo.Venda;
import pdv.modelo.VendaItem;

public interface IVendaDados {
    int salvarVenda(Venda venda);
    void salvarItens(int vendaId, List<VendaItem> itens);
}