package br.gov.mt.sejudh.sigepen.util;

import org.apache.commons.lang3.StringUtils;

public final class TextUtil {

    private TextUtil() {
    }

    public static String toTitleCase(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        String[] palavras = StringUtils.split(str, ' ');
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < palavras.length; i++) {
            String palavraLowerCase = StringUtils.lowerCase(palavras[i]);
            String palavraCapitalizada = StringUtils.capitalize(palavraLowerCase);
            resultado.append(palavraCapitalizada);
            if (i < palavras.length - 1) {
                resultado.append(" ");
            }
        }
        return resultado.toString();
    }
}
