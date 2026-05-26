package pdv.contrato;

import pdv.modelo.Produto;
import java.util.List;

public interface IBuscaStrategy {
    List<Produto> buscar(String termo);
    String getTipo();
}