package pdv.servico;

import pdv.contrato.IVendaDados;
import pdv.modelo.Venda;
import pdv.modelo.VendaItem;

public class VendaServico {
    private final IVendaDados dados;
    private final EstoqueServico estoque;
    
    public VendaServico(IVendaDados dados, EstoqueServico estoque) {
        this.dados = dados;
        this.estoque = estoque;
    }
    
    public boolean finalizar(Venda venda) {
        if (venda == null || venda.estaVazia()) {
            System.out.println("❌ Venda vazia!");
            return false;
        }
        
        System.out.println("💰 Finalizando venda...");
        System.out.println("📊 Total: R$ " + venda.getTotal());
        
        // 1. Verificar estoque de todos os itens
        for (VendaItem item : venda.getItens()) {
            System.out.println("Verificando estoque: " + item.getNomeProduto() + " - Qtd: " + item.getQuantidade());
            
            if (!estoque.verificarDisponibilidade(item.getProdutoId(), item.getQuantidade())) {
                System.out.println("❌ Estoque insuficiente para: " + item.getNomeProduto());
                return false;
            }
        }
        
        // 2. Salvar venda no banco
        int vendaId = dados.salvarVenda(venda);
        if (vendaId <= 0) {
            System.out.println("❌ Erro ao salvar venda!");
            return false;
        }
        System.out.println("✅ Venda salva com ID: " + vendaId);
        
        // 3. Salvar itens e BAIXAR ESTOQUE
        dados.salvarItens(vendaId, venda.getItens());
        
        for (VendaItem item : venda.getItens()) {
            boolean baixou = estoque.baixarEstoque(item.getProdutoId(), item.getQuantidade());
            if (baixou) {
                System.out.println("✅ Estoque baixado: " + item.getNomeProduto() + " - " + item.getQuantidade());
            } else {
                System.out.println("❌ Erro ao baixar estoque: " + item.getNomeProduto());
            }
        }
        
        System.out.println("✅ Venda finalizada com sucesso!");
        return true;
    }
}