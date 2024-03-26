package br.gov.mt.sejudh.sigepen.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.UserFilter;

import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class SigepenFilter extends UserFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object obj) {
		UnidadePrisional unidade = SecurityUtil.getUnidadeSelecionada();
		
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		
		String url = req.getRequestURI();
		String urlSelecionarUnidade = Sigepen.APPLICATION_SELECIONAR_UNIDADE_URL;
		String urlAlterarSenha = Sigepen.APPLICATION_ALTERAR_SENHA_URL;
		
		List<String> urlsLiberadas = new ArrayList<String>();
		
		urlsLiberadas.add(urlAlterarSenha);
		
		if (url.equals(Sigepen.APPLICATION_DOWNLOAD_URL)) {
			return true;
		}
		
		if (SecurityUtils.getSubject().isAuthenticated()) {
			if (SecurityUtil.getUsuario().getPrimeiroAcesso()) {
				if (!url.equals(urlAlterarSenha)) {
					try {
						resp.sendRedirect(urlAlterarSenha);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			else if (unidade == null  && !url.equals(urlSelecionarUnidade) && !urlsLiberadas.contains(url) && SecurityUtil.getUsuario().getId() > 0) {
				try {
					resp.sendRedirect(urlSelecionarUnidade);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (unidade != null && url.equals(Sigepen.APPLICATION_LOGIN_URL)) {
				try {
					resp.sendRedirect(Sigepen.APPLICATION_DASHBOARD_URL);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return super.isAccessAllowed(request, response, obj);
	}

	private static final String FACES_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<partial-response><redirect url=\"%s\"></redirect></partial-response>";
	
	@Override
    protected void redirectToLogin(ServletRequest req, ServletResponse res) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;

        if ("partial/ajax".equals(request.getHeader("Faces-Request"))) {
            res.setContentType("text/xml");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().printf(FACES_REDIRECT_XML, request.getContextPath() + getLoginUrl());
        }
        else {
            super.redirectToLogin(req, res);
        }
    }
}
