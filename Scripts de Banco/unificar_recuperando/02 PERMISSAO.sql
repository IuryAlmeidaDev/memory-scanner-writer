insert into permissao(id, nome, descricao, funcionalidade_id) values((select max(id)+1 from PERMISSAO), 'UnificarRecuperando', 'Permite executar unificação de cadastro de recuperandos', 8);


-- conceder permissões para os casos de uso
INSERT INTO PERFIL_PERMISSOES(PERMISSAO_ID, PERFIL_ID)
SELECT P.ID, PERF.PERFIL_ID AS PERFIL
FROM PERMISSAO P,
     (
         SELECT 0 AS PERFIL_ID FROM DUAL  UNION
         SELECT PF.ID AS PERFIL_ID  FROM PERFIL PF WHERE PF.NOME ='PENAL' UNION
         SELECT PF.ID AS PERFIL_ID  FROM PERFIL PF WHERE PF.NOME ='SAAP' UNION
         SELECT PF.ID AS PERFIL_ID  FROM PERFIL PF WHERE PF.NOME ='GESTOR'
     ) PERF WHERE NOME IN
                  ( 'UnificarRecuperando')
              AND NOT EXISTS (SELECT 1 FROM PERFIL_PERMISSOES PF WHERE PF.PERFIL_ID = PERF.PERFIL_ID AND PF.PERMISSAO_ID = P.ID)

;