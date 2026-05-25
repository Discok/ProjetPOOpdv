package pdv.servico;

import pdv.modelo.Produto;
import pdv.contrato.IProdutoDados;
import java.util.Optional;

public class ProdutoServico {
    private final IProdutoDados dados;
    
    public ProdutoServico(IProdutoDados dados) {
        this.dados = dados;
    }
    
    public Optional<Produto> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }
        return dados.buscarPorCodigo(codigo);
    }
    
    public boolean temEstoque(Produto produto, int quantidade) {
        return produto != null && produto.getEstoque() >= quantidade;
    }
}