package br.gov.mt.sejudh.sigepen.conversores;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BooleanToNumericConverter implements AttributeConverter<Boolean, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Boolean value) {
        return (value != null && value) ? 1 : 0; // Aqui garante que true vira 1 e false vira 0
    }

    @Override
    public Boolean convertToEntityAttribute(Integer value) {
        return (value != null && value == 1);
    }
}

