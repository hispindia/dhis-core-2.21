package org.hisp.dhis.common;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @author Lars Helge Overland
 */
public class QueryFilter
{
    public static final String OPTION_SEP = ";";
    
    public static final Map<String, String> OPERATOR_MAP = new HashMap<String, String>() { {
        put( "eq", "=" );
        put( "gt", ">" );
        put( "ge", ">=" );
        put( "lt", "<" );
        put( "le", "<=" );
        put( "ne", "!=" );
        put( "like", "like" );
        put( "in", "in" );
    } };
    
    protected String operator;

    protected String filter;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public QueryFilter()
    {
    }
    
    public QueryFilter( String operator, String filter )
    {
        this.operator = operator;
        this.filter = filter;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------
    
    public boolean hasFilter()
    {
        return operator != null && !operator.isEmpty() && filter != null && !filter.isEmpty();
    }
    
    public String getSqlOperator()
    {
        if ( operator == null )
        {
            return null;
        }
        
        return OPERATOR_MAP.get( operator.toLowerCase() );
    }
    
    public String getSqlFilter( String encodedFilter )
    {
        if ( operator == null || encodedFilter == null )
        {
            return null;
        }
                
        if ( operator.equalsIgnoreCase( "like" ) )
        {
            return "'%" + encodedFilter + "%'";
        }
        else if ( operator.equalsIgnoreCase( "in" ) )
        {
            String[] split = encodedFilter.split( OPTION_SEP );
            
            final StringBuffer buffer = new StringBuffer( "(" );        
            
            for ( String el : split )
            {
                buffer.append( "'" ).append( el ).append( "'," );
            }
            
            return buffer.deleteCharAt( buffer.length() - 1 ).append( ")" ).toString();
        }
        
        return "'" + encodedFilter + "'";
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------
    
    @Override
    public String toString()
    {
        return "[Operator: " + operator + ", filter: " + filter + "]";
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( filter == null) ? 0 : filter.hashCode() );
        result = prime * result + ( ( operator == null) ? 0 : operator.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        QueryFilter other = (QueryFilter) object;
        
        if ( filter == null )
        {
            if ( other.filter != null )
            {
                return false;
            }
        }
        else if ( !filter.equals( other.filter ) )
        {
            return false;
        }
        
        if ( operator == null )
        {
            if ( other.operator != null )
            {
                return false;
            }
        }
        else if ( !operator.equals( other.operator ) )
        {
            return false;
        }
        
        return true;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public String getOperator()
    {
        return operator;
    }

    public void setOperator( String operator )
    {
        this.operator = operator;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter( String filter )
    {
        this.filter = filter;
    }
}
