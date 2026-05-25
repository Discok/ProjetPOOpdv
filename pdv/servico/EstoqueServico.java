package pdv.servico;

import pdv.contrato.IProdutoDados;

public class EstoqueServico {
    private final IProdutoDados dados;
    
    public EstoqueServico(IProdutoDados dados) {
        this.dados = dados;
    }
    
    public boolean verificarDisponibilidade(int produtoId, int quantidade) {
        var produto = dados.buscarPorId(produtoId);
        return produto.isPresent() && produto.get().getEstoque() >= quantidade;
    }
    
    public boolean baixarEstoque(int produtoId, int quantidade) {
        if (quantidade <= 0) return false;
        return dados.atualizarEstoque(produtoId, quantidade);
    }
}