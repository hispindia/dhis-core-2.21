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
package org.hisp.dhis.reportexcel.organisationunitgrouplisting.action;

import java.util.List;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.reportexcel.ReportExcelOganiztionGroupListing;
import org.hisp.dhis.reportexcel.ReportExcelService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ListOrganisationUnitGroupAtLevelAction
    implements Action
{

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private Map<OrganisationUnitGroup, OrganisationUnitLevel> organisationUnitGroupAtLevel;

    public Map<OrganisationUnitGroup, OrganisationUnitLevel> getOrganisationUnitGroupAtLevel()
    {
        return organisationUnitGroupAtLevel;
    }

    private List<OrganisationUnitGroup> availableOrganisationUnitGroups;

    public List<OrganisationUnitGroup> getAvailableOrganisationUnitGroups()
    {
        return availableOrganisationUnitGroups;
    }

    private List<OrganisationUnitLevel> organisationUnitLevel;

    public List<OrganisationUnitLevel> getOrganisationUnitLevel()
    {
        return organisationUnitLevel;
    }

    private ReportExcelOganiztionGroupListing reportExcel;

    public ReportExcelOganiztionGroupListing getReportExcel()
    {
        return reportExcel;
    }

    @Override
    public String execute()
        throws Exception
    {
        organisationUnitLevel = organisationUnitService.getOrganisationUnitLevels();

        reportExcel = (ReportExcelOganiztionGroupListing) reportService.getReportExcel( id );

        availableOrganisationUnitGroups = reportExcel.getOrganisationUnitGroups();
        
        organisationUnitGroupAtLevel = reportExcel.getOrganisationUnitLevels();

        return SUCCESS;
    }

}
