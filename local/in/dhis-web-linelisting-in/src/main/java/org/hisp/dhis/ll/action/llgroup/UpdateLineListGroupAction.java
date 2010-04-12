package org.hisp.dhis.ll.action.llgroup;

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
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.swing.JOptionPane;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.ll.action.lldataentry.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

public class UpdateLineListGroupAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }


    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private Collection<String> selectedList;

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    private String periodTypeSelect;

    public void setPeriodTypeSelect( String periodTypeSelect )
    {
        this.periodTypeSelect = periodTypeSelect;
    }

    public String getPeriodTypeSelect()
    {
        return periodTypeSelect;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {

        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        if ( description != null && description.trim().length() == 0 )
        {
            description = null;
        }

        shortName = shortName.replaceAll( " ", "_" );

        // ---------------------------------------------------------------------
        // Update data element
        // ---------------------------------------------------------------------

        LineListGroup lineListGroup = lineListService.getLineListGroup( id );

        lineListGroup.setName( name );
        lineListGroup.setShortName( shortName );
        lineListGroup.setDescription( description );
        lineListGroup.setPeriodType( periodService.getPeriodTypeByName( periodTypeSelect ) );

        List<LineListElement> newElements =  new ArrayList<LineListElement>();
        List<LineListElement> oldElements =  new ArrayList<LineListElement>(lineListGroup.getLineListElements());
        List<LineListElement> removeElementList =  new ArrayList<LineListElement>();
        Collection<LineListElement> updatedDataElementList = new HashSet<LineListElement>();


        if ( selectedList == null )
        {
            System.out.println( "selectedList is null" );
        }
        else
        {
            if(newElements.isEmpty())

            for ( String id : selectedList )
            {
                LineListElement element = lineListService.getLineListElement( Integer.parseInt( id ) );
                if(!(oldElements.contains(element)))
                {
                    newElements.add(element);
                    System.out.println("New element that should be added is: "+element);
                }
                updatedDataElementList.add(element);
            }
        }
        for(int i=0; i<oldElements.size();i++)
        {
             if(!(updatedDataElementList.contains(oldElements.get(i))))
             {

                  boolean doNotDelete = dataBaseManagerInterface.checkDataFromTable(lineListGroup.getShortName(),oldElements.get(i));
                  System.out.println(doNotDelete);
                  if(doNotDelete)
                  {
                      JOptionPane.showMessageDialog(null,oldElements.get(i) + " cannot delete, its having data");
                      updatedDataElementList.add(oldElements.get(i));
                  }
                  else
                  {
                      System.out.println("element that should be removed is: "+oldElements.get(i) +  " " + lineListGroup.getShortName());
                      removeElementList.add(oldElements.get(i));
                      updatedDataElementList.remove(oldElements.get(i));
                  }
              }
        }

        lineListGroup.getLineListElements().retainAll( updatedDataElementList );

        lineListGroup.getLineListElements().addAll( updatedDataElementList );

        System.out.println( lineListGroup.getLineListElements().size() );
        boolean dataUpdated = dataBaseManagerInterface.updateTable(lineListGroup.getShortName(), removeElementList, newElements);
        if(dataUpdated)
            lineListService.updateLineListGroup( lineListGroup );

        return SUCCESS;
    }
}
