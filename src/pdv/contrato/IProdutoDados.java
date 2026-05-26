package pdv.contrato;

import java.util.List;
import java.util.Optional;
import pdv.modelo.Produto;

public interface IProdutoDados {
    Optional<Produto> buscarPorCodigo(String codigo);
    Optional<Produto> buscarPorId(int id);
    boolean atualizarEstoque(int produtoId, int quantidade);
    List<Produto> buscarPorNome(String nome);
}