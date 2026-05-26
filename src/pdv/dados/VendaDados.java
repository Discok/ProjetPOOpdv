package pdv.dados;

import java.sql.*;
import java.util.List;
import pdv.banco.Conexao;
import pdv.contrato.IVendaDados;
import pdv.modelo.Venda;
import pdv.modelo.VendaItem;

public class VendaDados implements IVendaDados {
    private final Conexao conexao;
    
    public VendaDados(Conexao conexao) {
        this.conexao = conexao;
        criarTabelas();
    }
    
    private void criarTabelas() {
        String sqlVendas = "CREATE TABLE IF NOT EXISTS vendas (" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "data_hora DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "total DECIMAL(10,2) NOT NULL)";
        String sqlItens = "CREATE TABLE IF NOT EXISTS venda_itens (" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "venda_id INT, produto_id INT, quantidade INT, subtotal DECIMAL(10,2)," +
            "FOREIGN KEY (venda_id) REFERENCES vendas(id))";
        try (Connection conn = conexao.conectar(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlVendas);
            stmt.executeUpdate(sqlItens);
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    @Override
    public int salvarVenda(Venda venda) {
        String sql = "INSERT INTO vendas (data_hora, total) VALUES (?, ?)";
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(venda.getDataHora()));
            stmt.setDouble(2, venda.getTotal());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    
    @Override
    public void salvarItens(int vendaId, List<VendaItem> itens) {
        String sql = "INSERT INTO venda_itens (venda_id, produto_id, quantidade, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (VendaItem item : itens) {
                stmt.setInt(1, vendaId);
                stmt.setInt(2, item.getProdutoId());
                stmt.setInt(3, item.getQuantidade());
                stmt.setDouble(4, item.getSubtotal());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}