package pdv.servico;

import java.util.*;
import pdv.contrato.IBuscaStrategy;
import pdv.contrato.IProdutoDados;
import pdv.modelo.Produto;

public class ProdutoServico {
    private final IProdutoDados dados;
    private IBuscaStrategy estrategiaBusca;
    
    public ProdutoServico(IProdutoDados dados) {
        this.dados = dados;
    }
    
    public void setEstrategiaBusca(IBuscaStrategy estrategia) {
        this.estrategiaBusca = estrategia;
    }
    
    public List<Produto> buscar(String termo) {
        if (estrategiaBusca == null) return new ArrayList<>();
        return estrategiaBusca.buscar(termo);
    }
    
    public Optional<Produto> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return Optional.empty();
        return dados.buscarPorCodigo(codigo);
    }
    
    public Optional<Produto> buscarPorId(int id) {
        return dados.buscarPorId(id);
    }
    
    public List<Produto> buscarPorNome(String nome) {
        return dados.buscarPorNome(nome);
    }
    
    public boolean temEstoque(Produto produto, int quantidade) {
        return produto != null && produto.getEstoque() >= quantidade;
    }
}