package org.hisp.dhis.security.hibernate;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates and returns org.acegisecurity.userdetails.UserDetails objects based
 * on requests from the Acegi framework. The returned UserDetails objects
 * contain the username, password and authorities of the requested users.
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: HibernateUserDetailsService.java 3109 2007-03-19 17:05:21Z torgeilo $
 */
public class HibernateUserDetailsService
    implements UserDetailsService
{
    public static final String ID = UserDetailsService.class.getName();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // UserDetailsService implementation
    // -------------------------------------------------------------------------

    @Transactional
    public final UserDetails loadUserByUsername( String username )
        throws UsernameNotFoundException, DataAccessException
    {
        UserCredentials credentials = loadUserCredentials( username );

        return new User( credentials.getUsername(), credentials.getPassword(), true,
            true, true, true, getGrantedAuthorities( credentials ) );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<GrantedAuthority> getGrantedAuthorities( UserCredentials credentials )
    {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        for ( UserAuthorityGroup group : credentials.getUserAuthorityGroups() )
        {
            for ( String authority : group.getAuthorities() )
            {
                authorities.add( new GrantedAuthorityImpl( authority ) );
            }
        }

        return authorities;
    }

    private UserCredentials loadUserCredentials( String username )
        throws UsernameNotFoundException
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserCredentials.class );
        criteria.add( Restrictions.eq( "username", username ) );

        UserCredentials userCredentials = (UserCredentials) criteria.uniqueResult();

        if ( userCredentials == null )
        {
            throw new UsernameNotFoundException( "Username doesn't exist" );
        }

        return userCredentials;
    }
}
