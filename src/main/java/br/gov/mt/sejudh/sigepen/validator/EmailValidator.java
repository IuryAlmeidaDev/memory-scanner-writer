package br.gov.mt.sejudh.sigepen.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

	private final static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private final static Pattern EMAIL_COMPILED_PATTERN = Pattern.compile(EMAIL_PATTERN);

	public void validate(FacesContext context, UIComponent c, Object value)
			throws ValidatorException {

		String email = (String) value;
		email = email.trim();

		/*
		if (email == null || "".equals(email)) {
			FacesMessage msg = new FacesMessage("E-mail inválido.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}*/
		
		if(!email.equals("")){
			Matcher matcher = EMAIL_COMPILED_PATTERN.matcher((String) email);
			
			if (!matcher.matches()) {
				FacesMessage msg = new FacesMessage("E-mail não é válido. Exemplo: nome@teste.com");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}
	}

}

