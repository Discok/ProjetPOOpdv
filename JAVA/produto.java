public class Produto {

    private int    id;
    private String nome;
    private String codigo;
    private double preco;
    private int    quantidade;

    // Construtor vazio
    public Produto() { }

    // Construtor completo
    public Produto(int id, String nome, String codigo,
                   double preco, int quantidade) {
        this.id         = id;
        this.nome       = nome;
        this.codigo     = codigo;
        this.preco      = preco;
        this.quantidade = quantidade;
    }

    // Getters
    public int    getId()        { return id; }
    public String getNome()      { return nome; }
    public String getCodigo()    { return codigo; }
    public double getPreco()     { return preco; }
    public int    getQuantidade(){ return quantidade; }

    // Setters
    public void setId(int id)              { this.id         = id; }
    public void setNome(String nome)        { this.nome       = nome; }
    public void setCodigo(String codigo)    { this.codigo     = codigo; }
    public void setPreco(double preco)      { this.preco      = preco; }
    public void setQuantidade(int qtd)      { this.quantidade = qtd; }

    // Exibe o produto como texto (útil para debug)
    public String toString() {
        return nome + " | R$ " + preco + " | Estoque: " + quantidade;
    }

}