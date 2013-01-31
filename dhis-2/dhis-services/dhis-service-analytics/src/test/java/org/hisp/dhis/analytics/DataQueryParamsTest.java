package org.hisp.dhis.analytics;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.period.Period;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hisp.dhis.analytics.DataQueryParams.*;

public class DataQueryParamsTest
{
    @Test
    public void testGetDimensionFromParam()
    {
        assertEquals( DataQueryParams.DATAELEMENT_DIM_ID, DataQueryParams.getDimensionFromParam( "de:D348asd782j;kj78HnH6hgT;9ds9dS98s2" ) );
    }
    
    @Test
    public void testGetDimensionOptionsFromParam()
    {
        List<String> expected = new ArrayList<String>( Arrays.asList( "D348asd782j", "kj78HnH6hgT", "9ds9dS98s2" ) );
        
        assertEquals( expected, DataQueryParams.getDimensionOptionsFromParam( "de:D348asd782j;kj78HnH6hgT;9ds9dS98s2" ) );        
    }
    
    @Test
    public void testGetMeasureCriteriaFromParam()
    {
        Map<MeasureFilter, Double> expected = new HashMap<MeasureFilter, Double>();
        expected.put( MeasureFilter.GT, 100d );
        expected.put( MeasureFilter.LT, 200d );
        
        assertEquals( expected, DataQueryParams.getMeasureCriteriaFromParam( "GT:100;LT:200" ) );
    }
    
    @Test
    public void testHasPeriods()
    {
        DataQueryParams params = new DataQueryParams();
        
        assertFalse( params.hasPeriods() );
        
        List<IdentifiableObject> periods = new ArrayList<IdentifiableObject>();
        
        params.getDimensions().add( new Dimension( PERIOD_DIM_ID, DimensionType.PERIOD, periods ) );
        
        assertFalse( params.hasPeriods() );
        
        params.removeDimension( PERIOD_DIM_ID );

        assertFalse( params.hasPeriods() );
        
        periods.add( new Period() );
        params.getDimensions().add( new Dimension( PERIOD_DIM_ID, DimensionType.PERIOD, periods ) );
        
        assertTrue( params.hasPeriods() );
    }
}
