--------------------------------------------------------------
--- SCRIPT DE ALTERAÇÃO DA TABELA DE AUDITORIA AUD_SIGEPEN_OCORRENCIA
--------------------------------------------------------------
ALTER TABLE SIGEPEN_OWNER.AUD_SIGEPEN_OCORRENCIA ADD (
    RESUMO             VARCHAR2(255),
    DETALHAMENTO       VARCHAR2(4000),
    STATUS_OCORRENCIA  VARCHAR2(50),
    ID_PLANTAO         NUMBER(19),
    ATIVO              NUMBER(1)
    );