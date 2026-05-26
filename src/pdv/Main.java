package pdv;

import java.sql.SQLException;
import pdv.tela.TelaPDV;
import pdv.banco.Conexao;
import pdv.banco.ConexaoMySQL;
import pdv.dados.ProdutoDados;
import pdv.dados.VendaDados;
import pdv.servico.ProdutoServico;
import pdv.servico.VendaServico;
import pdv.servico.EstoqueServico;
import javax.swing.SwingUtilities;

public class Main {
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        try {
            Conexao conexao = new ConexaoMySQL();
            var conn = conexao.conectar();
            
            ProdutoDados produtoDados = new ProdutoDados(conexao);
            VendaDados vendaDados = new VendaDados(conexao);
            
            ProdutoServico produtoServico = new ProdutoServico(produtoDados);
            EstoqueServico estoqueServico = new EstoqueServico(produtoDados);
            VendaServico vendaServico = new VendaServico(vendaDados, estoqueServico);
            
            SwingUtilities.invokeLater(() -> {
                new TelaPDV(produtoServico, vendaServico, conn).setVisible(true);
            });
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}