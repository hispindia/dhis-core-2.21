﻿/*
 * Copyright (C) 2007-2008  Camptocamp
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @requires core/GeoStat/Choropleth.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

/**
 * Class: mapfish.widgets.geostat.Choropleth
 * Use this class to create a widget allowing to display choropleths
 * on the map.
 *
 * Inherits from:
 * - {Ext.FormPanel}
 */

mapfish.widgets.geostat.Mapping = Ext.extend(Ext.FormPanel, {

    /**
     * APIProperty: layer
     * {<OpenLayers.Layer.Vector>} The vector layer containing the features that
     *      are styled based on statistical values. If none is provided, one will
     *      be created.
     */
    layer: null,

    /**
     * APIProperty: format
     * {<OpenLayers.Format>} The OpenLayers format used to get features from
     *      the HTTP request response. GeoJSON is used if none is provided.
     */
    format: null,

    /**
     * APIProperty: url
     * {String} The URL to the web service. If none is provided, the features
     *      found in the provided vector layer will be used.
     */
    url: null,

    /**
     * APIProperty: featureSelection
     * {Boolean} A boolean value specifying whether feature selection must
     *      be put in place. If true a popup will be displayed when the
     *      mouse goes over a feature.
     */
    featureSelection: true,

    /**
     * APIProperty: nameAttribute
     * {String} The feature attribute that will be used as the popup title.
     *      Only applies if featureSelection is true.
     */
    nameAttribute: null,

    /**
     * APIProperty: indicator
     * {String} (read-only) The feature attribute currently chosen
     *     Useful if callbacks are registered on 'featureselected'
     *     and 'featureunselected' events
     */
    indicator: null,

    /**
     * APIProperty: indicatorText
     * {String} (read-only) The raw value of the currently chosen indicator
     *     (ie. human readable)
     *     Useful if callbacks are registered on 'featureselected'
     *     and 'featureunselected' events
     */
    indicatorText: null,

    /**
     * Property: coreComp
     * {<mapfish.GeoStat.ProportionalSymbol>} The core component object.
     */
    coreComp: null,

    /**
     * Property: classificationApplied
     * {Boolean} true if the classify was applied
     */
    classificationApplied: false,

    /**
     * Property: ready
     * {Boolean} true if the widget is ready to accept user commands.
     */
    ready: false,

    /**
     * Property: border
     *     Styling border
     */
    border: false,

    /**
     * APIProperty: loadMask
     *     An Ext.LoadMask config or true to mask the widget while loading (defaults to false).
     */
    loadMask : false,

    /**
     * APIProperty: labelGenerator
     *     Generator for bin labels
     */
    labelGenerator: null,

    getGridPanelHeight : function() {
        var h = screen.height;
        
        if (h <= 800) {
            return 120;
        }
        else if (h <= 1050) {
            return 480;
        }
        else if (h <= 1200) {
            return 600;
        }
        else {
            return 900;
        }
    },
     
    newUrl : false,
    
    /**
     * Method: initComponent
     *    Inits the component
     */    
    initComponent : function() {
    
        mapStore = new Ext.data.JsonStore({
            url: path + 'getAllMaps' + type,
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
            autoLoad: true
        });
            
        gridStore = new Ext.data.JsonStore({
            url: path + 'getAvailableMapOrganisationUnitRelations' + type,
            root: 'mapOrganisationUnitRelations',
            fields: ['id', 'organisationUnit', 'organisationUnitId', 'featureId'],
            sortInfo: { field: 'organisationUnit', direction: 'ASC' },
            autoLoad: false
        });

        gridView = new Ext.grid.GridView({ 
            forceFit: true,
            sortClasses: ['sort-asc'],
            getRowClass: function(row,index) {
                var cls = ''; 
                switch (row.data.featureId) {
                    case '': 
                        cls = 'row-not-assigned';
                        break;
                    default:
                        cls = 'row-assigned';
                }
                
                return cls;                    
            }
        });
    
        this.items =
        [
            {
                xtype: 'combo',
                id: 'maps_cb',
				labelStyle: AA_LIGHT,
                fieldLabel: 'Map',
                typeAhead: true,
                editable: false,
                valueField: 'mapLayerPath',
                displayField: 'name',
                mode: 'remote',
                forceSelection: true,
                triggerAction: 'all',
                emptyText: MENU_EMPTYTEXT,
                selectOnFocus: true,
				labelSeparator: MENU_LABELSEPARATOR,
                width: combo_width2,
                minListWidth: combo_list_width2,
                store: mapStore,
                listeners: {
                    'select': {
                        fn: function() {
                            var mlp = Ext.getCmp('maps_cb').getValue();
                            this.newUrl = mlp;
                            
                            Ext.getCmp('grid_gp').getStore().baseParams = { mapLayerPath: mlp, format: 'json' };
                            Ext.getCmp('grid_gp').getStore().reload();
                            
                            this.classify(false);
                        },
                        scope: this
                    }
                }
            },

            { html: '<br>' },

            {
                xtype: 'grid',
                id: 'grid_gp',
                store: gridStore,
                columns: [ { header: 'Organisation units ', id: 'organisationUnitId', dataIndex: 'organisationUnit', sortable: true, width: gridpanel_width } ],
				  autoExpandColumn: 'organisationUnitId',
				  enableHdMenu: true,
                width: gridpanel_width,
                height: this.getGridPanelHeight(),
                view: gridView,
                style: 'left:0px',
                bbar: new Ext.StatusBar({
						defaultText: '',
                    id: 'relations_sb',
                    items:
                    [
                        {
                            xtype: 'button',
                            id: 'autoassign_be',
                            text: 'Auto-assign',
							cls: 'aa_med',
                            isVisible: false,
                            handler: function()
                            {
                                if (!Ext.getCmp('maps_cb').getValue()) {
                                    Ext.messageRed.msg('Auto-assign', 'Please select a map.');
                                    return;
                                }

                                loadMapData('auto-assignment');
                            },
                            scope: this
                        },
                        {
                            xtype: 'button',
                            id: 'removerelation_b',
                            text: 'Remove',
							cls: 'aa_med',
                            isVisible: false,
                            handler: function()
                            {
                                if (!Ext.getCmp('maps_cb').getValue()) {
                                    Ext.messageRed.msg('Remove relation', 'Please select a map.');
                                    return;
                                }
                                
                                if (!Ext.getCmp('grid_gp').getSelectionModel().getSelected()) {
                                    Ext.messageRed.msg('Remove relation', 'Please select an organisation unit from the list.');
                                    return;
                                }
                                    
                                var selected = Ext.getCmp('grid_gp').getSelectionModel().getSelected();
                                var oui = selected.data['organisationUnitId'];
                                var ou = selected.data['organisationUnit'];
                                var mlp = Ext.getCmp('maps_cb').getValue();
                                
                                Ext.Ajax.request({
                                    url: path + 'deleteMapOrganisationUnitRelation' + type,
                                    method: 'GET',
                                    params: { mapLayerPath: mlp, organisationUnitId: oui },

                                    success: function( responseObject ) {
                                        var mlp = Ext.getCmp('maps_cb').getValue();
                                        Ext.getCmp('grid_gp').getStore().baseParams = { mapLayerPath: mlp, format: 'json' };
                                        Ext.getCmp('grid_gp').getStore().reload();
                                        
                                        Ext.messageBlack.msg('Remove relation', msg_highlight_start + ou + msg_highlight_end + ' relation removed.');
                                        
                                        mapping.classify(true);
                                    },
                                    failure: function() {
                                        alert('Error while deleting MapOrganisationUnitRelation');
                                    } 
                                });
                            },
                            scope: this
                        },
                        {
                            xtype: 'button',
                            id: 'removeallrelations_b',
                            text: 'Remove all',
							cls: 'aa_med',
                            isVisible: false,
                            handler: function() {
                                if (!Ext.getCmp('maps_cb').getValue()) {
                                    Ext.messageRed.msg('Remove all relations', 'Please select a map.');
                                    return;
                                }
                                
                                var mlp = Ext.getCmp('maps_cb').getValue();
                                
                                Ext.Ajax.request({
                                    url: path + 'deleteMapOrganisationUnitRelationsByMap' + type,
                                    method: 'GET',
                                    params: { mapLayerPath: mlp },

                                    success: function( responseObject ) {
                                        var mlp = Ext.getCmp('maps_cb').getValue();
                                        Ext.getCmp('grid_gp').getStore().baseParams = { mapLayerPath: mlp, format: 'json' };
                                        Ext.getCmp('grid_gp').getStore().reload();
                                        
                                        Ext.messageBlack.msg('Remove all relations', 'All relations for the map ' + msg_highlight_start + Ext.getCmp('maps_cb').getRawValue() + msg_highlight_end + ' removed.');
                                        
                                        mapping.classify(true);
                                    },
                                    failure: function() {
                                        alert('Error while deleting MapOrganisationUnitRelation');
                                    } 
                                });
                            },
                            scope: this
                        }
                    ]
                })
             }
        ];

        mapfish.widgets.geostat.Choropleth.superclass.initComponent.apply(this);
    },
    
    setUrl: function(url) {
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    /**
     * Method: requestSuccess
     *      Calls onReady callback function and mark the widget as ready.
     *      Called on Ajax request success.
     */
    requestSuccess: function(request) {
        this.ready = true;

        // if widget is rendered, hide the optional mask
        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    /**
     * Method: requestFailure
     *      Displays an error message on the console.
     *      Called on Ajax request failure.
     */
    requestFailure: function(request) {
        OpenLayers.Console.error('Ajax request failed');
    },

    /**
     * Method: getColors
     *    Retrieves the colors from form elements
     *
     * Returns:
     * {Array(<mapfish.Color>)} an array of two colors (start, end)
     */
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf').getValue());
        return [colorA, colorB];
    },

    /**
     * Method: classify
     *
     * Parameters:
     * exception - {Boolean} If true show a message box to user if either
     *      the widget isn't ready, or no indicator is specified, or no
     *      method is specified.
     */
    classify: function(exception) {
        if (!this.ready) {
alert(2);		
            Ext.MessageBox.alert('Error', 'Component init not complete');
            return;
        }
        
        if (this.newUrl) {
            URL = this.newUrl;
            this.newUrl = false;
			
			if (MAPSOURCE == MAP_SOURCE_TYPE_GEOJSON) {
				this.setUrl(path + 'getGeoJson.action?name=' + URL);
			}
			else if (MAPSOURCE == MAP_SOURCE_TYPE_SHAPEFILE) {
				this.setUrl(path_geoserver + wfs + URL + output);
			}
        }
        
        if (!Ext.getCmp('maps_cb').getValue()) {
                if (exception) {
                    Ext.messageRed.msg('Assign organisation units', 'Please select a map.');
                }
                return;
        }
        
		MASK.msg = 'Loading data...';
        MASK.show();
        
        loadMapData('assignment');
    },

    /**
     * Method: onRender
     * Called by EXT when the component is rendered.
     */
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Choropleth.superclass.onRender.apply(this, arguments);
        if(this.loadMask){
            this.loadMask = new Ext.LoadMask(this.bwrap, this.loadMask);
            this.loadMask.show();
        }

        var coreOptions = {
            'layer': this.layer,
            'format': this.format,
            'url': this.url,
            'requestSuccess': this.requestSuccess.createDelegate(this),
            'requestFailure': this.requestFailure.createDelegate(this),
            'featureSelection': this.featureSelection,
            'nameAttribute': this.nameAttribute,
            'legendDiv': this.legendDiv,
            'labelGenerator': this.labelGenerator
        };

        this.coreComp = new mapfish.GeoStat.Choropleth(this.map, coreOptions);
    }
});

Ext.reg('mapping', mapfish.widgets.geostat.Mapping);