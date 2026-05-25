package pdv.modelo;

public class Produto {
    private final int id;
    private final String codigoBarras;
    private final String nome;
    private final double preco;
    private final int estoque;
    
    public Produto(int id, String codigoBarras, String nome, double preco, int estoque) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }
    
    public int getId() { return id; }
    public String getCodigoBarras() { return codigoBarras; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }
}