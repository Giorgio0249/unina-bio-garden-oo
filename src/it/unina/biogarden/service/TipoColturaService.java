package it.unina.biogarden.service;

import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.TipoColturaDao;
import it.unina.biogarden.model.TipoColtura;
import java.util.List;

public class TipoColturaService {
    private final TipoColturaDao tipoColturaDao;

    public TipoColturaService() {
        this.tipoColturaDao = DaoFactory.createTipoColturaDao();
    }

    public List<TipoColtura> getCatalogoColture() throws Exception {
        List<TipoColtura> lista = tipoColturaDao.findAll();
        if (lista.isEmpty()) {
            throw new Exception("Il catalogo delle colture è vuoto nel database.");
        }
        return lista;
    }
}