package br.gov.mt.sejudh.sigepen.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("CnpjValidator")
public class CnpjValidator implements Validator {

    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object valor) throws ValidatorException {
        if (valor == null || valor.toString().isEmpty()) {
            return;
        }

        String cnpj = valor.toString().replaceAll("[^a-zA-Z0-9]", "");

        if (!validaCNPJ(cnpj)) {
            FacesMessage message = new FacesMessage("CNPJ é inválido.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public static boolean validaCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) {
            return false;
        }

        try {
            Integer.parseInt(cnpj.substring(12));

            String base = cnpj.substring(0, 12);
            String digitosEsperados = calcularDigitos(base);

            return cnpj.endsWith(digitosEsperados);
        } catch (Exception e) {
            return false;
        }
    }

    private static String calcularDigitos(String base) {
        int peso1 = 5;
        int soma1 = 0;
        for (int i = 0; i < 12; i++) {
            soma1 += valorCaractere(base.charAt(i)) * peso1;
            peso1 = (peso1 == 2) ? 9 : peso1 - 1;
        }
        int d1 = 11 - (soma1 % 11);
        d1 = (d1 >= 10) ? 0 : d1;


        String base2 = base + d1;
        int peso2 = 6;
        int soma2 = 0;
        for (int i = 0; i < 13; i++) {
            soma2 += valorCaractere(base2.charAt(i)) * peso2;
            peso2 = (peso2 == 2) ? 9 : peso2 - 1;
        }
        int d2 = 11 - (soma2 % 11);
        d2 = (d2 >= 10) ? 0 : d2;

        return "" + d1 + d2;
    }

    private static int valorCaractere(char c) {
        if (Character.isDigit(c)) {
            return Character.getNumericValue(c);
        } else {
            return (int) c - 48;
        }
    }
}