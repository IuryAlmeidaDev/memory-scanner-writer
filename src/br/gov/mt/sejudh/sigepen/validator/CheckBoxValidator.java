package br.gov.mt.sejudh.sigepen.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("checkBoxValidator")
public class CheckBoxValidator implements Validator {
	
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		
	        if (value.equals(Boolean.FALSE)) {
	        	
	        	FacesMessage msg = new FacesMessage("Confirmação Obrigatória!");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
	        }
	    }

}

