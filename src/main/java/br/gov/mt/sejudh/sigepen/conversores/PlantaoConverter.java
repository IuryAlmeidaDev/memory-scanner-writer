package br.gov.mt.sejudh.sigepen.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.service.PlantaoService;
import javax.faces.convert.Converter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@FacesConverter(forClass = Plantao.class)
public class PlantaoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // Mapeia pelo ID ou tipoPlantao (dependendo da necessidade)
        return new Plantao(); // Adapte conforme necessário
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if (object == null || !(object instanceof Plantao)) {
            return "";
        }
        return ((Plantao) object).getTipoPlantao().toString(); // Retorna o tipoPlantao como String
    }
}
