package br.gov.mt.sejudh.sigepen.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.gov.mt.sejudh.sigepen.model.Plantao;

import java.text.SimpleDateFormat;
import java.util.Date;

@FacesConverter(value = "PlantaoConverter2")
public class PlantaoConverter2 implements Converter {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Plantao) {
            Date dataPlantao = ((Plantao) value).getDataPlantao();
            return dataPlantao != null ? formatter.format(dataPlantao) : "";
        }
        return null;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return null; // Implementação opcional para conversão reversa, se necessário.
    }
}
