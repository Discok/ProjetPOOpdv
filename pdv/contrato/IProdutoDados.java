package pdv.contrato;

import pdv.modelo.Produto;
import java.util.Optional;

public interface IProdutoDados {
    Optional<Produto> buscarPorCodigo(String codigo);
    Optional<Produto> buscarPorId(int id);
    boolean atualizarEstoque(int produtoId, int quantidade);
}