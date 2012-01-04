package org.hisp.dhis.settings.action.user;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import static org.hisp.dhis.user.UserSettingService.AUTO_SAVE_DATA_ENTRY_FORM;
import static org.hisp.dhis.user.UserSettingService.KEY_CHARTS_IN_DASHBOARD;

import java.util.Locale;

import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.options.displayproperty.DisplayPropertyManager;
import org.hisp.dhis.options.sortorder.SortOrderManager;
import org.hisp.dhis.options.style.StyleManager;
import org.hisp.dhis.user.UserSettingService;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class SetGeneralSettingsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DisplayPropertyManager displayPropertyManager;

    public void setDisplayPropertyManager( DisplayPropertyManager displayPropertyManager )
    {
        this.displayPropertyManager = displayPropertyManager;
    }

    private LocaleManager localeManagerInterface;

    public void setLocaleManagerInterface( LocaleManager localeManagerInterface )
    {
        this.localeManagerInterface = localeManagerInterface;
    }

    private LocaleManager localeManagerDB;

    public void setLocaleManagerDB( LocaleManager localeManagerDB )
    {
        this.localeManagerDB = localeManagerDB;
    }

    private SortOrderManager sortOrderManager;

    public void setSortOrderManager( SortOrderManager sortOrderManager )
    {
        this.sortOrderManager = sortOrderManager;
    }

    private StyleManager styleManager;

    public void setStyleManager( StyleManager styleManager )
    {
        this.styleManager = styleManager;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Boolean autoSave;

    public void setAutoSave( Boolean autoSave )
    {
        this.autoSave = autoSave;
    }

    private Integer chartsInDashboard;

    public void setChartsInDashboard( Integer chartsInDashboard )
    {
        this.chartsInDashboard = chartsInDashboard;
    }

    private String currentDisplayProperty;

    public void setCurrentDisplayProperty( String currentDisplayProperty )
    {
        this.currentDisplayProperty = currentDisplayProperty;
    }

    private String currentLocale;

    public void setCurrentLocale( String locale )
    {
        this.currentLocale = locale;
    }

    private String currentLocaleDb;

    public void setCurrentLocaleDb( String currentLocaleDb )
    {
        this.currentLocaleDb = currentLocaleDb;
    }

    private String currentSortOrder;

    public void setCurrentSortOrder( String currentSortOrder )
    {
        this.currentSortOrder = currentSortOrder;
    }

    private String currentStyle;

    public void setCurrentStyle( String style )
    {
        this.currentStyle = style;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        localeManagerInterface.setCurrentLocale( getRespectiveLocale( currentLocale ) );

        localeManagerDB.setCurrentLocale( getRespectiveLocale( currentLocaleDb ) );

        sortOrderManager.setCurrentSortOrder( currentSortOrder );

        displayPropertyManager.setCurrentDisplayProperty( currentDisplayProperty );

        styleManager.setUserStyle( currentStyle );

        userSettingService.saveUserSetting( KEY_CHARTS_IN_DASHBOARD, chartsInDashboard );

        userSettingService.saveUserSetting( AUTO_SAVE_DATA_ENTRY_FORM, autoSave );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Locale getRespectiveLocale( String locale )
    {
        String[] tokens = locale.split( "_" );
        Locale newLocale = null;

        switch ( tokens.length )
        {
        case 1:
            newLocale = new Locale( tokens[0] );
            break;

        case 2:
            newLocale = new Locale( tokens[0], tokens[1] );
            break;

        case 3:
            newLocale = new Locale( tokens[0], tokens[1], tokens[2] );
            break;

        default:
        }

        return newLocale;
    }
}
