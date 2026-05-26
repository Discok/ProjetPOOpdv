package pdv.servico;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pdv.contrato.IBuscaStrategy;
import pdv.contrato.IProdutoDados;
import pdv.modelo.Produto;

public class BuscaPorCodigo implements IBuscaStrategy {
    private final IProdutoDados dados;
    
    public BuscaPorCodigo(IProdutoDados dados) {
        this.dados = dados;
    }
    
    @Override
    public List<Produto> buscar(String termo) {
        List<Produto> resultados = new ArrayList<>();
        Optional<Produto> produto = dados.buscarPorCodigo(termo);
        produto.ifPresent(resultados::add);
        return resultados;
    }
    
    @Override
    public String getTipo() {
        return "Código de Barras";
    }
}