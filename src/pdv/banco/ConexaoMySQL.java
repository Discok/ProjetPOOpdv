package pdv.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoMySQL implements Conexao {
    private static final String URL = "127.0.0.1:3306/";
    private static final String USUARIO = "root";
    private static final String SENHA = "123456"; 
    
    private Connection conexao;
    
    @Override
    public Connection conectar() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            criarBancoSeNaoExistir();
            conexao = DriverManager.getConnection(URL + "pdv_simples", USUARIO, SENHA);
        }
        return conexao;
    }  
    private void criarBancoSeNaoExistir() throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS pdv_simples";
        try (Statement stmt = conexao.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
    
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void desconectar() {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}