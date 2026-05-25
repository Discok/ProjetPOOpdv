package pdv.contrato;

import pdv.modelo.Venda;
import pdv.modelo.VendaItem;
import java.util.List;

public interface IVendaDados {
    int salvarVenda(Venda venda);
    void salvarItens(int vendaId, List<VendaItem> itens);
}