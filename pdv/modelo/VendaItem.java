package pdv.modelo;

public class VendaItem {
    private final int produtoId;
    private final String nomeProduto;
    private final double preco;
    private int quantidade;
    
    public VendaItem(int produtoId, String nomeProduto, double preco, int quantidade) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.preco = preco;
        this.quantidade = quantidade;
    }
    
    public int getProdutoId() { return produtoId; }
    public String getNomeProduto() { return nomeProduto; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
    public double getSubtotal() { return preco * quantidade; }
    
    public void setQuantidade(int quantidade) {
        if (quantidade > 0) this.quantidade = quantidade;
    }
}