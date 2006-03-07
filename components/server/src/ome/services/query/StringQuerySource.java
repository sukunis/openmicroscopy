/*
 * ome.services.query.StringQuerySource
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2005 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

/*------------------------------------------------------------------------------
 *
 * Written by:    Josh Moore <josh.moore@gmx.de>
 *
 *------------------------------------------------------------------------------
 */

package ome.services.query;

// Java imports


// Third-party libraries
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;

// Application-internal dependencies


/**
 * creates a query based on the id string.
 *  
 * TODO: should we get rid of the session here? 
 * then we can't parse the query string....
 * 
 * @author Josh Moore, <a href="mailto:josh.moore@gmx.de">josh.moore@gmx.de</a>
 * @version 1.0 <small> (<b>Internal version:</b> $Rev$ $Date$) </small>
 * @since OMERO 3.0
 */
public class StringQuerySource extends QuerySource
{

    private static Log log = LogFactory.getLog(StringQuerySource.class);
    
    public Query lookup(String queryID,QueryParameter...parameters)
    {
        QueryParameter stringQP = QP.String("string",queryID);
        if (parameters != null) 
        {
            QueryParameter[] temp = new QueryParameter[parameters.length+1];
            temp[0] = stringQP;
            System.arraycopy(parameters,0,temp,1,parameters.length);
            return new StringQuery(temp);
        }
        return new StringQuery(stringQP);
        
    }
    
}

class StringQuery extends Query 
{

    static Definitions defs = new Definitions(
        new QueryParameterDef("string", String.class, false));
    
    public StringQuery(QueryParameter...parameters ){
        super( defs, parameters );
    }

    @Override
    protected Object runQuery(Session session) 
        throws HibernateException, SQLException
    {
        org.hibernate.Query query = session.createQuery((String) value("string"));
        String[] params = query.getNamedParameters();
        for (int i = 0; i < params.length; i++)
        {
            String p = params[i];
            Object v = value(p);
            if (Collection.class.isAssignableFrom(v.getClass()))
            {
                query.setParameterList(p,(Collection)v);
            } else {
                query.setParameter(p,v);
            }
        }
        
        return query.list();
        
    }
}