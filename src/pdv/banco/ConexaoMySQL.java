package pdv.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoMySQL implements Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USUARIO = "root";
    private static final String SENHA = "123456"; // ← Coloque sua senha do MySQL aqui!
    
    private Connection conexao;
    
    @Override
    public Connection conectar() throws SQLException {
        try {
            // Registrar o driver (opcional mas recomendado)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            if (conexao == null || conexao.isClosed()) {
                // Primeiro conecta sem banco específico
                conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                criarBancoSeNaoExistir();
                // Depois reconecta no banco específico
                conexao = DriverManager.getConnection(URL + "pdv_simples", USUARIO, SENHA);
                System.out.println("✅ Conectado ao MySQL com sucesso!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL não encontrado!");
            throw new SQLException("Driver não encontrado", e);
        }
        
        return conexao;
    }  
    
    private void criarBancoSeNaoExistir() throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS pdv_simples";
        try (Statement stmt = conexao.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Banco de dados verificado/criado!");
        }
    }
    
    @Override
    public void desconectar() {
        if (conexao != null) {
            try {
                conexao.close();
                System.out.println("✅ Conexão fechada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}