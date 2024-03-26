package br.gov.mt.sejudh.sigepen.base.dataModel;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.bean.MetodosDePaginaBean;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;

public class AbstractDataModel<T extends BaseModel<I>, N extends BaseService<T, I>, I extends Number> extends BaseDataModel<T, I> {

  private static final long serialVersionUID = 7761129644350916416L;

  public AbstractDataModel(N negocio, FiltroConsulta filtro, MetodosDePaginaBean paginaBean) {
    super(negocio, filtro, paginaBean);
  }

}
