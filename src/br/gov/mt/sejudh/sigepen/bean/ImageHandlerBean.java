package br.gov.mt.sejudh.sigepen.bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ApplicationScoped
public class ImageHandlerBean {
	
	@Inject
	private FacesUtil facesUtil;

	public StreamedContent getImagem(Imagem img) {
		FacesContext context = FacesContext.getCurrentInstance();
		String hashId = context.getExternalContext().getRequestParameterMap().get("hashId");
		
		if (img != null) {
			facesUtil.setObjetoSessao(String.valueOf(img.getId()), img);
		}
		
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			if (hashId == null || hashId.isEmpty()) {
				InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/images/fotoplaceholder.png");
				return new DefaultStreamedContent(stream, "image/png");
			}
	
			Imagem imgSessao = (Imagem) facesUtil.getObjetoSessao(hashId, false);
			return new DefaultStreamedContent(new ByteArrayInputStream(imgSessao.getArquivo()), imgSessao.getContentType());
		}
	}
}
