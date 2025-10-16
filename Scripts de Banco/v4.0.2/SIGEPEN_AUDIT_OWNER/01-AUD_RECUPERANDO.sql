------------------------------------------------------------------
--- SCRIPT DE ALTERAÇÃO DA TABELA AUD_RECUPERANDO
------------------------------------------------------------------

ALTER TABLE SIGEPEN_AUDIT_OWNER.AUD_RECUPERANDO ADD PERICULOSIDADE_ADAPTATIVA NUMBER(10);

UPDATE SIGEPEN_AUDIT_OWNER.AUD_RECUPERANDO
SET PERICULOSIDADE_ADAPTATIVA = 
    DECODE(PERICULOSIDADE,
        'BAIXA', 0, 
        'MÉDIA', 1, 
        'ALTA', 2, 
        'ALTÍSSIMA', 3
    );