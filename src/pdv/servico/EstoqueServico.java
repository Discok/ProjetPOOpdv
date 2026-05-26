package pdv.servico;

import pdv.contrato.IProdutoDados;

public class EstoqueServico {
    private final IProdutoDados dados;
    
    public EstoqueServico(IProdutoDados dados) {
        this.dados = dados;
    }
    
    // ✅ Método mantido mas sempre retorna true (permite venda)
    public boolean verificarDisponibilidade(int produtoId, int quantidade) {
        // SEMPRE retorna true - permite venda mesmo sem estoque
        System.out.println("✅ Verificação ignorada - venda permitida (estoque pode ficar negativo)");
        return true;
    }
    
    public boolean baixarEstoque(int produtoId, int quantidade) {
        if (quantidade <= 0) return false;
        
        boolean atualizado = dados.atualizarEstoque(produtoId, quantidade);
        if (atualizado) {
            System.out.println("✅ Estoque do produto " + produtoId + " baixado em " + quantidade);
        } else {
            System.out.println("❌ Falha ao baixar estoque do produto " + produtoId);
        }
        return atualizado;
    }
}