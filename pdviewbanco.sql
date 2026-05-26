
CREATE DATABASE IF NOT EXISTS pdviewBanco;
USE pdviewBanco;

CREATE TABLE IF NOT EXISTS produtos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo_barras VARCHAR(50) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    estoque INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vendas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS venda_itens (
    id INT PRIMARY KEY AUTO_INCREMENT,
    venda_id INT,
    produto_id INT,
    quantidade INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venda_id) REFERENCES vendas(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

INSERT IGNORE INTO produtos (codigo_barras, nome, preco, estoque) VALUES
('7891234560010', 'Coca-Cola 2L', 8.50, 100),
('7891234560027', 'Arroz 5kg', 22.90, 300),
('7891234560034', 'Feijão 1kg', 6.75, 400),
('7891234560041', 'Açúcar 1kg', 4.50, 205),
('7891234560058', 'Café 500g', 12.90, 200),
('7891234560065', 'Leite 1L', 4.99, 350),
('7891234560072', 'Pão de Forma', 8.90, 150),
('7891234560089', 'Manteiga 500g', 12.50, 180),
('7891234560096', 'Óleo de Soja 900ml', 6.75, 220),
('7891234560102', 'Farinha de Trigo 1kg', 4.29, 300);

-- 6. Verificar se os dados foram inseridos
SELECT * FROM produtos;

-- 7. Mostrar resumo
SELECT 
    'Banco de dados criado com sucesso!' AS Status,
    COUNT(*) AS Total_Produtos 
FROM produtos;