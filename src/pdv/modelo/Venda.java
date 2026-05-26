package pdv.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {
    private List<VendaItem> itens;
    private LocalDateTime dataHora;
    private double total;
    
    // Construtor padrão
    public Venda() {
        this.itens = new ArrayList<>();
        this.dataHora = LocalDateTime.now();
        this.total = 0;
    }
    
    // Construtor com parâmetros
    public Venda(List<VendaItem> itens, double total) {
        this.itens = itens != null ? itens : new ArrayList<>();
        this.dataHora = LocalDateTime.now();
        this.total = total;
    }
    
    public void adicionarItem(VendaItem item) {
        for (VendaItem existente : itens) {
            if (existente.getProdutoId() == item.getProdutoId()) {
                existente.setQuantidade(existente.getQuantidade() + item.getQuantidade());
                recalcularTotal();
                return;
            }
        }
        itens.add(item);
        recalcularTotal();
    }
    
    public void removerItem(int indice) {
        if (indice >= 0 && indice < itens.size()) {
            itens.remove(indice);
            recalcularTotal();
        }
    }
    
    public void recalcularTotal() {
        this.total = 0;
        for (VendaItem item : itens) {
            this.total += item.getPreco() * item.getQuantidade();
        }
        System.out.println("💰 Total recalculado: R$ " + this.total);
    }
    
    public List<VendaItem> getItens() { 
        return itens; 
    }
    
    public LocalDateTime getDataHora() { 
        return dataHora; 
    }
    
    public double getTotal() { 
        return total; 
    }
    
    public boolean estaVazia() { 
        return itens.isEmpty(); 
    }
    
    public int getQuantidadeItens() {
        int qtd = 0;
        for (VendaItem item : itens) {
            qtd += item.getQuantidade();
        }
        return qtd;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public void setItens(List<VendaItem> itens) {
        this.itens = itens;
    }
}