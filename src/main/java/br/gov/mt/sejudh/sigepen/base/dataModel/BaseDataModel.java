package br.gov.mt.sejudh.sigepen.base.dataModel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.bean.MetodosDePaginaBean;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;

public abstract class BaseDataModel<T extends BaseModel<I>, I extends Number> extends LazyDataModel<T> {

    private static final long serialVersionUID = 1L;

    private final BaseService<T, I> baseService;
    private final FiltroConsulta filtro;
    private final MetodosDePaginaBean paginaBean;

    public BaseDataModel(BaseService<T, I> baseService, FiltroConsulta filtro, MetodosDePaginaBean paginaBean) {
        this.baseService = baseService;
        this.filtro = filtro;
        this.paginaBean = paginaBean;
    }

    @SuppressWarnings("unchecked")
    public boolean isEmpty() {
        if ((super.getWrappedData()) == null) {
            return true;
        }
        return ((List<T>) super.getWrappedData()).isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getRowData(String rowKey) {
        try {
            int value = Integer.parseInt(rowKey);
            List<T> data = (List<T>) super.getWrappedData();
            
            if(data == null) {
            	return null;
            }
            
            if(value < 0 || value >= data.size()) {
            	return null;
            }
            
            return data.get(value);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    @SuppressWarnings("unchecked")
    public Object getRowKey(T object) {
        return ((List<T>) super.getWrappedData()).indexOf(object);
    }

    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        if (this.filtro == null) return new ArrayList<>();
        try {
            setRowCount(baseService.quantidadeFiltrados(this.filtro));

            first = this.getFirst(first);

            this.configurarPaginacao(first, pageSize, sortField, sortOrder);

            return baseService.buscaPorFiltro(this.filtro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void configurarPaginacao(int first, int pageSize, String sortField, SortOrder sortOrder) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (this.paginaBean != null) {
            paginaBean.configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, this.filtro);
            return;
        }

        this.filtro.organizaFiltro(first, pageSize, sortField, sortOrder);
    }

    private int getFirst(int first) {
        if (first >= getRowCount()) {
            first = 0;
            DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("registrosForm:dataTable");
            if (dt != null) {
                dt.setFirst(first);
            }
        }
        return first;
    }
}
