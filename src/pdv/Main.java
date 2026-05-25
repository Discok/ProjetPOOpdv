package pdv;

import javax.swing.SwingUtilities;
import pdv.banco.Conexao;
import pdv.banco.ConexaoMySQL;
import pdv.dados.ProdutoDados;
import pdv.dados.VendaDados;
import pdv.servico.EstoqueServico;
import pdv.servico.ProdutoServico;
import pdv.servico.VendaServico;
import pdv.tela.TelaPDV;

public class Main {
    public static void main(String[] args) {
        // Conectar ao banco
        var conexao = new ConexaoMySQL();
        ConexaoMySQL conexao1 = conexao;
        
        // Repositórios
        var produtoDados = new ProdutoDados((Conexao) conexao1);
        var vendaDados = new VendaDados((Conexao) conexao1);
        
        // Serviços
        var produtoServico = new ProdutoServico(produtoDados);
        var estoqueServico = new EstoqueServico(produtoDados);
        var vendaServico = new VendaServico(vendaDados, estoqueServico);
        
        // Abrir tela
        SwingUtilities.invokeLater(() -> {
            new TelaPDV(produtoServico, vendaServico).setVisible(true);
        });
        
        // Fechar conexão ao sair
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            conexao1.desconectar();
        }));
    }
}

