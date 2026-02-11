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

    /**
     * Retorna a próxima data em que cairá a semana do ciclo e o dia da semana informados,
     * a partir de uma data de referência.
     *
     * @param semanaCiclo Semana do ciclo (1 a 6)
     * @param diaSemana   Dia da semana (0 a 6, onde 0 é Domingo)
     * @param dataReferencia Data de início para a busca
     * @return Próxima data correspondente
     */
    public static LocalDate calcularProximaData(int semanaCiclo, int diaSemana, LocalDate dataReferencia) {
        LocalDate data = dataReferencia;
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // Procura nos próximos 12 meses (espaço de tempo suficiente para encontrar no ciclo de 6 semanas)
        for (int i = 0; i < 366; i++) {
            int numSemanaAno = data.get(weekFields.weekOfWeekBasedYear());
            int cicloAtual = obterSemana(numSemanaAno);
            int diaSemanaAtual = data.getDayOfWeek().getValue() % 7; // Ajusta para 0=Domingo se necessário, mas Java DayOfWeek é 1=Segunda, 7=Domingo

            // DiasSemana enum: DOM(0), SEG(1), ... SAB(6)
            // Java DayOfWeek: MONDAY(1) ... SUNDAY(7)
            // Para alinhar:
            int diaSemanaJavaToEnum = data.getDayOfWeek().getValue() % 7; 

            if (cicloAtual == semanaCiclo && diaSemanaJavaToEnum == diaSemana) {
                return data;
            }
            data = data.plusDays(1);
        }
        return null;
    }

    /**
     * Obtém o dia da semana no formato do enum DiasSemana (0-6, onde 0 é Domingo)
     * @param data Data
     * @return id do dia da semana
     */
    public static int obterDiaSemana(LocalDate data) {
        return data.getDayOfWeek().getValue() % 7;
    }
}