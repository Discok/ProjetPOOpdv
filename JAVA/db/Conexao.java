package projetopoopdv.java.db; 

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {

    private static final String URL    = "jdbc:mysql://localhost:3306/estoque_db"
                                        + "?useSSL=false&serverTimezone=America/Manaus";
    private static final String USUARIO = "root";
    private static final String SENHA   = "sua_senha_aqui";

    public static Connection obter() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao conectar: " + e.getMessage());
        }
    }
}