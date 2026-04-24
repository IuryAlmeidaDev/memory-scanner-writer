package br.gov.mt.sejudh.sigepen.util;

/**
 * Constantes de nomes de permissões Shiro usadas no código Java.
 * Centraliza os literais que aparecem em @URLMapping(id=...),
 * SecurityUtils.getSubject().isPermitted(...), etc., para evitar
 * divergência entre o valor cadastrado no banco e o consumido no código.
 */
public final class Permissoes {

    public static final String RELATORIO_JUMBO = "GerarRelatorioJumbo";

    private Permissoes() {
    }
}
