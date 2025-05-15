package br.gov.mt.sejudh.sigepen.conversores;

import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


//@FacesConverter(forClass = TipoPlantao.class)
@FacesConverter("tipoPlantaoConverter")
public class TipoPlantaoConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.isEmpty()) {
            return TipoPlantao.valueOf(value);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if (object instanceof TipoPlantao) {
            return ((TipoPlantao) object).name();
        }
        return null;
    }
}
