package org.hisp.dhis.datadictionary;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.common.GenericNameStore;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public class DefaultDataDictionaryService
    implements DataDictionaryService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericNameStore<DataDictionary> dataDictionaryStore;
    
    public void setDataDictionaryStore( GenericNameStore<DataDictionary> dataDictionaryStore )
    {
        this.dataDictionaryStore = dataDictionaryStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // DataDictionary
    // -------------------------------------------------------------------------

    public int saveDataDictionary( DataDictionary dataDictionary )
    {
        int dataDictionaryId = dataDictionary.getId();
        
        int id = dataDictionaryStore.save( dataDictionary );
        
        if ( dataDictionaryId == 0 )
        {
            i18nService.addObject( dataDictionary );
        }
        else
        {
            i18nService.verify( dataDictionary );
        }
        
        return id;
    }
    
    public DataDictionary getDataDictionary( int id )
    {
        return dataDictionaryStore.get( id );
    }
    
    public Collection<DataDictionary> getDataDictionaries( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllDataDictionaries();
        }
        
        Collection<DataDictionary> dictionaries = new ArrayList<DataDictionary>();
        
        for ( Integer id : identifiers )
        {
            dictionaries.add( getDataDictionary( id ) );
        }
        
        return dictionaries;
    }
    
    public void deleteDataDictionary( DataDictionary dataDictionary )
    {
        i18nService.removeObject( dataDictionary );
        
        dataDictionaryStore.delete( dataDictionary );
    }
    
    public DataDictionary getDataDictionaryByName( String name )
    {
        return dataDictionaryStore.getByName( name );
    }
    
    public Collection<DataDictionary> getAllDataDictionaries()
    {
        return dataDictionaryStore.getAll();
    }
}
