package br.gov.mt.sejudh.sigepen.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import br.gov.mt.sejudh.sigepen.dao.LogAcessoDao;
import br.gov.mt.sejudh.sigepen.model.LogAcesso;

@RequestScoped
public class LogAcessoBO {

    @Inject
    private LogAcessoDao logAcessoDao;

    @Transactional
    public void salvarLogAcesso(LogAcesso logAcesso) {
        if (logAcesso != null) {
            logAcessoDao.salvar(logAcesso);
        }
    }    
}