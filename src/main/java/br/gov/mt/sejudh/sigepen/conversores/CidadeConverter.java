package br.gov.mt.sejudh.sigepen.conversores;

import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.dao.InfopenDAO; 
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="CidadeConverter")
public class CidadeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "Todos".equals(value)) {
            return null;
        }

        try {
            InfopenDAO dao = context.getApplication().evaluateExpressionGet(context, "#{infopenDAO}", InfopenDAO.class);
            
            if (dao == null) return null;

            Long id = Long.parseLong(value);
            return dao.buscarCidadePorId(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || !(value instanceof Cidade)) {
            return "";
        }
        return String.valueOf(((Cidade) value).getId());
    }
}