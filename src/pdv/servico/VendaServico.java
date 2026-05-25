package pdv.servico;

import pdv.modelo.Venda;
import pdv.modelo.VendaItem;
import pdv.contrato.IVendaDados;

public class VendaServico {
    private final IVendaDados dados;
    private final EstoqueServico estoque;
    
    public VendaServico(IVendaDados dados, EstoqueServico estoque) {
        this.dados = dados;
        this.estoque = estoque;
    }
    
    public boolean finalizar(Venda venda) {
        if (venda == null || venda.estaVazia()) {
            return false;
        }
        
        // Verificar estoque
        for (VendaItem item : venda.getItens()) {
            if (!estoque.verificarDisponibilidade(item.getProdutoId(), item.getQuantidade())) {
                return false;
            }
        }
        
        // Salvar venda
        int vendaId = dados.salvarVenda(venda);
        if (vendaId <= 0) return false;
        
        // Salvar itens e baixar estoque
        dados.salvarItens(vendaId, venda.getItens());
        for (VendaItem item : venda.getItens()) {
            estoque.baixarEstoque(item.getProdutoId(), item.getQuantidade());
        }
        
        return true;
    }
}