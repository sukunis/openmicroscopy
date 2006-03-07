package ome.services.query;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import ome.model.core.Image;

public class PojosGetImagesQueryDefinition extends Query
{

    static Definitions defs = new Definitions(// TODO same as PojosFindHierarchy
        new IdsQueryParameterDef(),
        new OptionsQueryParameterDef(),
        new QueryParameterDef(QP.CLASS, Class.class, false));
    
    public PojosGetImagesQueryDefinition(QueryParameter... parameters)
    {
        super( defs, parameters );
    }  

    @Override
    protected Object runQuery(Session session) throws HibernateException, SQLException
    {
        Criteria c = session.createCriteria((Class) value(QP.CLASS));
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        c.add(Restrictions.in("id",(Collection) value(QP.IDS)));
        Hierarchy.fetchChildren(c,(Class) value(QP.CLASS),Integer.MAX_VALUE);
        
        return c.list();
        
    }

}
//select i from Image i
//#bottomUpHierarchy()
//    where 
//#imagelist()
//#filters()
//#typeExperimenter()
