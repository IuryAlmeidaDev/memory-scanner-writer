package br.gov.mt.sejudh.sigepen.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * Classe utilitária para operações relacionadas ao Cardápio
 */
public class CardapioUtil {

    /**
     * Obtém o número da semana do ano para uma determinada data
     *
     * @param dataString Data no formato dd/MM/yyyy ou dd/MM/yyyy HH:mm:ss
     * @return Número da semana do ano
     */
    public static int obterNumeroSemanaAno(String dataString) {
        String dataFormatada = formataData(dataString);
        LocalDate data = LocalDate.parse(dataFormatada);
        return data.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
    }

    /**
     * Formata uma string de data de dd/MM/yyyy para yyyy-MM-dd
     *
     * @param dataString Data no formato dd/MM/yyyy ou dd/MM/yyyy HH:mm:ss
     * @return Data formatada no padrão yyyy-MM-dd
     */
    public static String formataData(String dataString) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
            Date dataConvert = formatoEntrada.parse(dataString);
            SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy-MM-dd");
            return formatoSaida.format(dataConvert);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * Calcula o número da semana no ciclo (1-6) baseado no número da semana do ano
     *
     * @param numeroSemana Número da semana do ano
     * @return Número da semana no ciclo (1-6)
     */
    public static int obterSemana(int numeroSemana) {
        int resultado = numeroSemana % 6;
        if (resultado == 0 && numeroSemana != 0) {
            return 6;
        }
        return resultado;
    }
}