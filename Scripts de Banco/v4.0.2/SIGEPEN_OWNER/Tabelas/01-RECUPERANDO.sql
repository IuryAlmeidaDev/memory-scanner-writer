------------------------------------------------------------------
--- SCRIPT DE ALTERAÇÃO DA TABELA RECUPERANDO
------------------------------------------------------------------

ALTER TABLE RECUPERANDO ADD PERICULOSIDADE_ADAPTATIVA NUMBER(10);


UPDATE RECUPERANDO
SET PERICULOSIDADE_ADAPTATIVA = 
    DECODE(PERICULOSIDADE, 
        'BAIXA', 0, 
        'MÉDIA', 1, 
        'ALTA', 2, 
        'ALTÍSSIMA', 3
    );