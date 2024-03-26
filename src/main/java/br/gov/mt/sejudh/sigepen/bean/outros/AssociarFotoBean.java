package br.gov.mt.sejudh.sigepen.bean.outros;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.primefaces.event.CaptureEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarFoto", pattern = "/associar/foto", viewId = "/WEB-INF/pages/_dialogs/AssociarFoto.xhtml")
public class AssociarFotoBean extends MetodosDialogBean {
	
	private CroppedImage croppedImage;
	
	private String caminhoDaImagem;
	
	private String tipoFoto;
	
	private boolean possuiFotoEmDisco;
	
	private final String IMAGE_PATH = "//resources//images//";
	
	private final String IMAGE_RELATIVE_PATH = "/resources/images/";
	
	private final String ROOT_PATH = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("");
	
	private final String IMAGE_ABSOLUTE_PATH = ROOT_PATH  + IMAGE_PATH;
	
	private final String EXTENSAO_FOTO = "jpg";
	
	private final String EXTENSAO_FOTO_COM_PONTO = "." + EXTENSAO_FOTO;
	
	private final String CONTENT_TYPE = "image/" + EXTENSAO_FOTO;
	
	@Inject
	private FacesUtil facesUtil;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}

		return null;
	}

	public void receberImagemPorUpload(FileUploadEvent event) {
		try {
	        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(event.getFile().getContents()));
	        bi = reduzirEscalaDaImagem(bi);
	        escreverImagemEmDisco(bi);
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public void receberImagemPelaWebcam(CaptureEvent captureEvent) {
		try {		
	        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(captureEvent.getData()));
	        bi = reduzirEscalaDaImagem(bi);
	        escreverImagemEmDisco(bi);
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public void recortarImagemEAssociar() {
		try {
			if (croppedImage == null) {
	            throw new Exception("Crop null");
	        }

	        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(croppedImage.getBytes()));
	        
	        byte[] imagemComTamanhoReduzido = reduzirTamanhoDaImagem(512000, bi);

	        Imagem imagem = new Imagem();
	        imagem.setContentType(CONTENT_TYPE);
	        imagem.setArquivo(imagemComTamanhoReduzido);
	        
			excluirImagemDoDisco();
			
			facesUtil.fecharPaginaDialog(imagem);
		} catch(Exception e) {
			 e.printStackTrace();
		}
    }
	
	public void fecharDialog(ActionEvent event) throws Exception {
		excluirImagemDoDisco();
		facesUtil.fecharPaginaDialog(null);
	}
	
	public void configurarVariaveisParaReceberNovaFoto() {
		this.possuiFotoEmDisco = false;
		this.setCaminhoDaImagem(null);
	
		excluirImagemDoDisco();
	}
	
	private byte[] reduzirTamanhoDaImagem(long tamanhoMaximoEmBytes, BufferedImage bi) throws IOException {
		ByteArrayOutputStream imgTemp = new ByteArrayOutputStream();
		ImageIO.write(bi, EXTENSAO_FOTO, imgTemp);
		imgTemp.close();
        
        while(imgTemp.size() > tamanhoMaximoEmBytes){
        	imgTemp = new ByteArrayOutputStream();
	        BufferedImage bi2 = new BufferedImage((int) (0.95 * bi.getWidth()),(int) (0.95 * bi.getHeight()), BufferedImage.TYPE_INT_RGB);
	        Graphics2D grph = (Graphics2D) bi2.getGraphics();
	        grph.scale(0.95, 0.95);
	        grph.drawImage(bi, 0, 0, null);
	        grph.dispose();
	        bi = bi2;
	        ImageIO.write(bi, EXTENSAO_FOTO, imgTemp);
        }
        
		return imgTemp.toByteArray();
	}
	
	private BufferedImage reduzirEscalaDaImagem(BufferedImage bi) {
		double scale = 1l;
		
		if (bi.getHeight() > 600) {
        	scale = (double) 600 / bi.getHeight();
        }
		
        if (bi.getWidth() > 800) {
        	double nScale = (double) 800 / bi.getWidth();
        	scale = scale > nScale ? nScale :scale;
        }
	
        BufferedImage bi2 = new BufferedImage((int) (scale * bi.getWidth()),(int) (scale * bi.getHeight()), BufferedImage.TYPE_INT_RGB);
        Graphics2D grph = (Graphics2D) bi2.getGraphics();
        grph.scale(scale, scale);
        grph.drawImage(bi, 0, 0, null);
        grph.dispose();
        bi = bi2;
	        
        return bi;
	}
	
	private void escreverImagemEmDisco(BufferedImage bi) throws Exception {
		String filename = SecurityUtil.getLoginUsuario() + EXTENSAO_FOTO_COM_PONTO;
		ImageIO.write(bi, EXTENSAO_FOTO, new File(IMAGE_ABSOLUTE_PATH + filename));
    	this.setCaminhoDaImagem(IMAGE_RELATIVE_PATH + filename);
    	this.possuiFotoEmDisco = true;
    }
	
	private void excluirImagemDoDisco() {
		try {
			File f = new File(ROOT_PATH + this.getCaminhoDaImagem()); 		
			f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CroppedImage getCroppedImage() {
		return croppedImage;
	}

	public void setCroppedImage(CroppedImage croppedImage) {
		this.croppedImage = croppedImage;
	}

	public String getCaminhoDaImagem() {
		return caminhoDaImagem;
	}

	public void setCaminhoDaImagem(String caminhoDaImagem) {
		this.caminhoDaImagem = caminhoDaImagem;
	}

	public boolean ispossuiFotoEmDisco() {
		return possuiFotoEmDisco;
	}

	public void setpossuiFotoEmDisco(boolean possuiFotoEmDisco) {
		this.possuiFotoEmDisco = possuiFotoEmDisco;
	}

	public String getTipoFoto() {
		return tipoFoto;
	}

	public void setTipoFoto(String tipoFoto) {
		this.tipoFoto = tipoFoto;
	}
}
