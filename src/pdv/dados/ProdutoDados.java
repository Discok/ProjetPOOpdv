package pdv.dados;

import pdv.modelo.Produto;
import pdv.contrato.IProdutoDados;
import pdv.banco.Conexao;
import java.sql.*;
import java.util.Optional;

public class ProdutoDados implements IProdutoDados {
    private final Conexao conexao;
    
    public ProdutoDados(Conexao conexao) {
        this.conexao = conexao;
        criarTabela();
        inserirProdutosExemplo();
    }
    
    private void criarTabela() {
        String sql = """
            CREATE TABLE IF NOT EXISTS produtos (
                id INT PRIMARY KEY AUTO_INCREMENT,
                codigo_barras VARCHAR(50) UNIQUE NOT NULL,
                nome VARCHAR(100) NOT NULL,
                preco DECIMAL(10,2) NOT NULL,
                estoque INT DEFAULT 0
            )
        """;
        
        try (Connection conn = conexao.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void inserirProdutosExemplo() {
        String sql = """
            INSERT IGNORE INTO produtos (codigo_barras, nome, preco, estoque) VALUES
            ('7891234560010', 'Coca-Cola 2L', 8.50, 50),
            ('7891234560027', 'Arroz 5kg', 22.90, 30),
            ('7891234560034', 'Feijão 1kg', 6.75, 40),
            ('7891234560041', 'Açúcar 1kg', 4.50, 25),
            ('7891234560058', 'Café 500g', 12.90, 20)
        """;
        
        try (Connection conn = conexao.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Optional<Produto> buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM produtos WHERE codigo_barras = ?";
        
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(new Produto(
                    rs.getInt("id"),
                    rs.getString("codigo_barras"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getInt("estoque")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Produto> buscarPorId(int id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(new Produto(
                    rs.getInt("id"),
                    rs.getString("codigo_barras"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getInt("estoque")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @Override
    public boolean atualizarEstoque(int produtoId, int quantidade) {
        String sql = "UPDATE produtos SET estoque = estoque - ? WHERE id = ? AND estoque >= ?";
        
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantidade);
            stmt.setInt(2, produtoId);
            stmt.setInt(3, quantidade);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}