// Global OpenTenure namespace
var OT = OT || {};

// Messages for map
var MSG = MSG || {};
MSG.MAP_CONTROL_LAYERS = "Layers";
MSG.MAP_CONTROL_ZOOM_TO_EXTENT = "Zoom to extent";
MSG.MAP_CONTROL_ZOOM_IN = "Zoom in";
MSG.MAP_CONTROL_ZOOM_OUT = "Zoom out";
MSG.MAP_CONTROL_EDIT_MAP = "Edit map";
MSG.MAP_CONTROL_DRAW_POLYGON = "Draw polygon";
MSG.MAP_CONTROL_DRAW_LINE = "Draw line";
MSG.MAP_CONTROL_DRAW_POINT = "Draw point";
MSG.MAP_CONTROL_EDIT_SHAPE = "Edit shape";
MSG.MAP_CONTROL_DELETE_SHAPE = "Delete shape";
MSG.MAP_CONTROL_EDIT_PROPERTIES = "Edit properties";
MSG.MAP_CONTROL_SNAP = "Snap";
MSG.MAP_CONTROL_CONFIRM_FEATURE_DELETE = "Are you sure you want to delete selected feature?";
MSG.MAP_CONTROL_LOADING = "Loading...";
MSG.MAP_CONTROL_CLAIM_NOT_FOUND = "Claim not found";
MSG.MAP_CONTROL_CLAIMANT = "Claimant";
MSG.MAP_CONTROL_STATUS = "Status";
MSG.MAP_CONTROL_LODGED = "Lodged";
MSG.MAP_CONTROL_CLAIM_INFO = "Claim information";
MSG.MAP_CONTROL_PAN = "Pan";
MSG.MAP_CONTROL_MAXIMIZE = "Maximize";
MSG.MAP_CONTROL_MAXIMIZE_TITLE = "Maximize map";
// Map control
OT.Map = function(mapOptions) {
    var that = this;
    mapOptions = mapOptions ? mapOptions : {};

    // Enable cors for IE
    $.support.cors = true;

    // Boolean flag, indicating whether map can be edited. If false, editing tools will be hidden
    var isMapEditable = mapOptions.isMapEditable ? mapOptions.isMapEditable : false;

    // Map toolbar reference
    var mapToolbar;

    // Map legend
    var mapLegendTree;

    // Selected legend item
    var selectedNode = null;

    // Boolean flag, used to indicate whether map editing is on or off
    var enableMapEditing = false;

    // OL Map
    var map;

    // Map container
    var mapPanelContainer;

    // Map container html container id
    var mapContainerName = mapOptions.mapContainerName ? mapOptions.mapContainerName : "mapCtrl";

    // Idicates whether map was rendered or not
    var isRendered = false;

    // Initial layers
    var layers = mapOptions.layers ? mapOptions.layers : [];

    // Map height
    var mapHeight = mapOptions.mapHeight ? mapOptions.mapHeight : 500;

    // Map max extent bounds, used for full extent action
    this.maxExtentBounds = mapOptions.maxExtentBounds ? mapOptions.maxExtentBounds : null;

    // Initial zoom, required for proper zooming when rendering a map
    var initialZoomBounds = mapOptions.initialZoomBounds ? mapOptions.initialZoomBounds : null;

    // Default feature source CRS
    this.sourceCrs = mapOptions.sourceCrs ? mapOptions.sourceCrs : "EPSG:4326";

    // Default destination CRS
    this.destCrs = mapOptions.destCrs ? mapOptions.destCrs : "EPSG:3857";

    // Default language code, used for localization
    this.languageCode = mapOptions.languageCode ? mapOptions.languageCode : "en-us";

    // Web application URL, used to form JSON requests
    this.applicationUrl = mapOptions.applicationUrl ? mapOptions.applicationUrl : "";

    // Initial snapping layers
    var snappingLayers = mapOptions.snappingLayers ? mapOptions.snappingLayers : [];

    // Public getters
    this.getMap = function() {
        return map;
    };
    this.getMapToolbar = function() {
        return mapToolbar;
    };
    this.getMapLegend = function() {
        return mapLegendTree;
    };
    this.getIsMapEditable = function() {
        return isMapEditable;
    };

    /** Sets snapping layers */
    this.setSnappingLayers = function(layers) {
        if (layers) {
            // Search for toolbar button
            for (var i = 0; i < mapToolbar.items.items.length; i++) {
                var tbButton = mapToolbar.items.items[i];
                if (tbButton.id === OT.Map.TOOLBAR_BUTTON_IDS.SNAP) {
                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.setTargets(layers);

                    if (active)
                        control.activate();
                }
            }
        }
    };

    // Turns off or on map editing
    this.toggleMapEditing = function(enable) {
        if (enableMapEditing !== enable) {
            // Search for toolbar button
            for (var i = 0; i < mapToolbar.items.items.length; i++) {
                var tbButton = mapToolbar.items.items[i];
                if (tbButton.id === OT.Map.TOOLBAR_BUTTON_IDS.EDIT_MAP) {
                    tbButton.toggle(enable);
                }
            }
        }
    };

    // custom layer node UI class
    var LayerNodeUI = Ext.extend(
            GeoExt.tree.LayerNodeUI,
            new GeoExt.tree.TreeNodeUIEventMixin()
            );

    // Init map
    map = new OpenLayers.Map('map', {
        div: "map",
        allOverlays: false,
        maxResolution: 19.296875,
        projection: this.destCrs,
        displayProjection: this.sourceCrs,
        units: 'm',
        zoom: 5
    });

    var gsat = new OpenLayers.Layer.Google("Google Earth", {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22});
    var gmap = new OpenLayers.Layer.Google("Google Map", {numZoomLevels: 20, visibility: false});
    map.addLayers([gsat, gmap]);
   
    if (layers.length > 0) {
        map.addLayers(layers);
    }
    
    map.events.register('changelayer', map, handleLayerChange);
    
    var mapPanel = new GeoExt.MapPanel({
        region: 'center',
        zoom: 6,
        map: map
    });

    mapLegendTree = new Ext.tree.TreePanel({
        region: 'west',
        title: MSG.MAP_CONTROL_LAYERS,
        width: 250,
        autoScroll: true,
        listeners: {
            beforeclick: nodeSelectionHandler
        },
        collapsible: true,
        split: true,
        enableDD: true,
        // apply the tree node component plugin to layer nodes
        plugins: [{
                ptype: "gx_treenodecomponent"
            }],
        loader: {
            applyLoader: false,
            uiProviders: {
                "custom_ui": LayerNodeUI
            }
        },
        root: {
            nodeType: "gx_layercontainer",
            loader: {
                baseAttrs: {
                    uiProvider: "custom_ui"
                },
                createNode: function(attr) {
                    if (attr.layer.CLASS_NAME === 'OpenLayers.Layer.WMS') {
                        attr.component = {
                            xtype: "gx_wmslegend",
                            baseParams: {
                                LEGEND_OPTIONS: 'fontSize:12'
                            },
                            layerRecord: mapPanel.layers.getByLayer(attr.layer),
                            showTitle: false,
                            cls: "legend"
                        };
                    } else {
                        if (attr.layer.CLASS_NAME === 'OpenLayers.Layer.Vector') {
                            attr.component = {
                                xtype: "gx_vectorlegend",
                                untitledPrefix: "",
                                layerRecord: mapPanel.layers.getByLayer(attr.layer),
                                showTitle: false,
                                cls: "legend",
                                clickableTitle: true,
                                selectOnClick: true,
                                node: attr,
                                listeners: {
                                    ruleselected: function(legend, event) {
                                        nodeSelectionHandler(legend.node, event, true);
                                    },
                                    ruleunselected: function(legend, event) {
                                        nodeSelectionHandler(legend.node, event, true);
                                    }
                                }
                            };
                        } else {
                            attr.component = {
                                untitledPrefix: "",
                                layerRecord: mapPanel.layers.getByLayer(attr.layer),
                                showTitle: false,
                                cls: "legend"
                            };
                        }
                    }
                    return GeoExt.tree.LayerLoader.prototype.createNode.call(this, attr);
                }
            }
        },
        rootVisible: false,
        lines: false
    });

    Ext.QuickTips.init();

    // Create toolbar
    var mapToolbarItems = [];

    mapToolbarItems.push({
        id: OT.Map.TOOLBAR_BUTTON_IDS.ZOOM_TO_EXTENT,
        iconCls: 'zoomToExtentIcon',
        text: MSG.MAP_CONTROL_ZOOM_TO_EXTENT,
        tooltip: MSG.MAP_CONTROL_ZOOM_TO_EXTENT,
        handler: function() {
            map.zoomToExtent(that.maxExtentBounds);
        }
    });
    mapToolbarItems.push(new GeoExt.Action({
        id: OT.Map.TOOLBAR_BUTTON_IDS.ZOOM_IN,
        control: new OpenLayers.Control.ZoomBox({out: false}),
        iconCls: 'zoomInIcon',
        toggleGroup: "draw",
        group: "draw",
        map: map,
        text: MSG.MAP_CONTROL_ZOOM_IN,
        tooltip: MSG.MAP_CONTROL_ZOOM_IN
    }));
    mapToolbarItems.push(new GeoExt.Action({
        id: OT.Map.TOOLBAR_BUTTON_IDS.ZOOM_OUT,
        control: new OpenLayers.Control.ZoomBox({out: true}),
        toggleGroup: "draw",
        group: "draw",
        iconCls: 'zoomOutIcon',
        map: map,
        text: MSG.MAP_CONTROL_ZOOM_OUT,
        tooltip: MSG.MAP_CONTROL_ZOOM_OUT
    }));

    mapToolbarItems.push("-");

    mapToolbarItems.push(new GeoExt.Action({
        id: OT.Map.TOOLBAR_BUTTON_IDS.PAN,
        control: new OpenLayers.Control(),
        toggleGroup: "draw",
        group: "draw",
        iconCls: 'panIcon',
        pressed: true,
        map: map,
        text: MSG.MAP_CONTROL_PAN,
        tooltip: MSG.MAP_CONTROL_PAN
    }));

    var claimInfoControl = new OpenLayers.Control();
    OpenLayers.Util.extend(claimInfoControl, {
        draw: function() {
            this.clickHandler = new OpenLayers.Handler.Click(claimInfoControl,
                    {click: handleClaimInfoClick},
            {delay: 0, single: true, double: false, stopSingle: false, stopDouble: true});

        },
        activate: function() {
            return this.clickHandler.activate() &&
                    OpenLayers.Control.prototype.activate.apply(this, arguments);
        },
        deactivate: function() {
            var deactivated = false;
            if (OpenLayers.Control.prototype.deactivate.apply(this, arguments)) {
                this.clickHandler.deactivate();
                deactivated = true;
            }
            return deactivated;
        },
        CLASS_NAME: "OT.Map.Control.ClaimInfo"
    });

//    mapToolbarItems.push(new GeoExt.Action({
//        id: OT.Map.TOOLBAR_BUTTON_IDS.CLAIM_INFO,
//        control: claimInfoControl,
//        iconCls: 'informationIcon',
//        map: map,
//        toggleGroup: "draw",
//        group: "draw",
//        pressed: true,
//        text: MSG.MAP_CONTROL_CLAIM_INFO,
//        tooltip: MSG.MAP_CONTROL_CLAIM_INFO
//    }));

    if (isMapEditable) {
        // Define default layer for editing 
        var defaultEditingLayer = new OpenLayers.Layer.Vector("", {});

        // Enable editing tools
        mapToolbarItems.push("-");

        mapToolbarItems.push({
            id: OT.Map.TOOLBAR_BUTTON_IDS.EDIT_MAP,
            iconCls: 'editMapIcon',
            text: MSG.MAP_CONTROL_EDIT_MAP,
            tooltip: MSG.MAP_CONTROL_EDIT_MAP,
            enableToggle: true,
            toggleHandler: onMapEditToggle,
            pressed: false
        });

        mapToolbarItems.push("-");

        mapToolbarItems.push(new GeoExt.Action({
            id: OT.Map.TOOLBAR_BUTTON_IDS.DRAW_POLYGON,
            control: new OT.Map.Control.DrawFeature(defaultEditingLayer,
                    OpenLayers.Handler.Polygon,
                    {handlerOptions: {holeModifier: "altKey"}, featureAdded: featureAdded}),
            iconCls: 'polygonIcon',
            map: map,
            editingTool: true,
            toggleGroup: "draw",
            group: "draw",
            disabled: true,
            text: MSG.MAP_CONTROL_DRAW_POLYGON,
            tooltip: MSG.MAP_CONTROL_DRAW_POLYGON
        }));

        mapToolbarItems.push(new GeoExt.Action({
            id: OT.Map.TOOLBAR_BUTTON_IDS.DRAW_LINE,
            control: new OT.Map.Control.DrawFeature(defaultEditingLayer,
                    OpenLayers.Handler.Path, {handlerOptions: {}, featureAdded: featureAdded}),
            iconCls: 'polylineIcon',
            map: map,
            editingTool: true,
            toggleGroup: "draw",
            group: "draw",
            disabled: true,
            text: MSG.MAP_CONTROL_DRAW_LINE,
            tooltip: MSG.MAP_CONTROL_DRAW_LINE
        }));

        mapToolbarItems.push(new GeoExt.Action({
            id: OT.Map.TOOLBAR_BUTTON_IDS.DRAW_POINT,
            control: new OpenLayers.Control.DrawFeature(defaultEditingLayer,
                    OpenLayers.Handler.Point, {handlerOptions: {}, featureAdded: featureAdded}),
            iconCls: 'pointIcon',
            map: map,
            editingTool: true,
            toggleGroup: "draw",
            group: "draw",
            disabled: true,
            text: MSG.MAP_CONTROL_DRAW_POINT,
            tooltip: MSG.MAP_CONTROL_DRAW_POINT
        }));

        mapToolbarItems.push("-");

        mapToolbarItems.push(new GeoExt.Action({
            id: OT.Map.TOOLBAR_BUTTON_IDS.EDIT_SHAPE,
            control: new OpenLayers.Control.ModifyFeature(defaultEditingLayer, null),
            iconCls: 'shapeEditIcon',
            map: map,
            editingTool: true,
            toggleGroup: "draw",
            group: "draw",
            disabled: true,
            text: MSG.MAP_CONTROL_EDIT_SHAPE,
            tooltip: MSG.MAP_CONTROL_EDIT_SHAPE
        }));

        mapToolbarItems.push(new GeoExt.Action({
            id: OT.Map.TOOLBAR_BUTTON_IDS.DELETE_FEATURE,
            control: new OpenLayers.Control.SelectFeature(defaultEditingLayer,
                    {clickout: true, multiple: false, hover: true, box: false,
                        clickFeature: function(feature) {
                            this.unselect(feature);
                            if (confirm(MSG.MAP_CONTROL_CONFIRM_FEATURE_DELETE)) {
                                this.layer.removeFeatures(feature);
                                customizeMapToolbar();
                            }
                        }
                    }
            ),
            iconCls: 'featureDeleteIcon',
            map: map,
            editingTool: true,
            toggleGroup: "draw",
            group: "draw",
            disabled: true,
            text: MSG.MAP_CONTROL_DELETE_SHAPE,
            tooltip: MSG.MAP_CONTROL_DELETE_SHAPE
        }));

        mapToolbarItems.push(new GeoExt.Action({
            id: OT.Map.TOOLBAR_BUTTON_IDS.EDIT_FEATURE,
            control: new OpenLayers.Control.SelectFeature(defaultEditingLayer,
                    {clickout: true, multiple: false, hover: true, box: false,
                        clickFeature: function(feature) {
                            this.unselect(feature);
                            if (typeof this.layer.editFeatureFunc !== 'undefined') {
                                this.layer.editFeatureFunc(feature);
                            }
                        }
                    }
            ),
            iconCls: 'featureEditIcon',
            map: map,
            editingTool: true,
            toggleGroup: "draw",
            group: "draw",
            disabled: true,
            text: MSG.MAP_CONTROL_EDIT_PROPERTIES,
            tooltip: MSG.MAP_CONTROL_EDIT_PROPERTIES
        }));

        mapToolbarItems.push("-");

        mapToolbarItems.push(new GeoExt.Action({
            id: OT.Map.TOOLBAR_BUTTON_IDS.SNAP,
            control: new OpenLayers.Control.Snapping({
                layer: defaultEditingLayer,
                targets: snappingLayers,
                greedy: false
            }),
            iconCls: 'snapIcon',
            map: map,
            editingTool: true,
            enableToggle: true,
            disabled: true,
            text: MSG.MAP_CONTROL_SNAP,
            tooltip: MSG.MAP_CONTROL_SNAP
        }));
    }

    mapToolbarItems.push("-");
    mapToolbarItems.push(new Ext.Action({
        id: OT.Map.TOOLBAR_BUTTON_IDS.MAXIMIZE_MAP,
        iconCls: 'maximizeIcon',
        enableToggle: true,
        text: MSG.MAP_CONTROL_MAXIMIZE,
        tooltip: MSG.MAP_CONTROL_MAXIMIZE_TITLE,
        toggleHandler: maximizeMap
    }));

    mapToolbar = new Ext.Toolbar({
        enableOverflow: true,
        items: mapToolbarItems
    });

    var mapStatusBar = new Ext.Toolbar({
        items: [new Ext.form.Label({id: "lblScaleBar", text: ""}),
            '->',
            new Ext.form.Label({id: "lblMapMousePosition", text: ""}),
            {xtype: 'tbspacer', width: 10}
        ]
    });

    mapPanelContainer = new Ext.Panel({
        layout: 'border',
        height: mapHeight,
        tbar: mapToolbar,
        bbar: mapStatusBar,
        items: [mapLegendTree, mapPanel]
    });

    // Maximize or minimize map control
    function maximizeMap(item, pressed) {
        var escContainerName = "#" + mapContainerName.replace(":", "\\:");
        if (pressed) {
            $(escContainerName).addClass("fullScreen");
            mapPanelContainer.setHeight($(window).height());
            mapPanelContainer.setWidth($('body').innerWidth());
        }
        else {
            $(escContainerName).removeClass("fullScreen");
            mapPanelContainer.setHeight(mapHeight);
            mapPanelContainer.setWidth($(escContainerName).parent().width());
        }
    }

    /** Renders map into provided html container */
    this.renderMap = function() {
        setTimeout(function() {
            if (!isRendered) {
                mapPanelContainer.render(mapContainerName);
                map.zoomToExtent(initialZoomBounds);
                mapPanelContainer.setWidth($("#" + mapContainerName.replace(":", "\\:")).parent().width());
                map.addControl(new OpenLayers.Control.MousePosition({div: document.getElementById("lblMapMousePosition")}));
                map.addControl(new OT.Map.Control.ScaleBar({div: document.getElementById("lblScaleBar")}));
                isRendered = true;
            }
        }, 0);
    };

    // Subscribe to map resize event to adjust map width/height
    $(window).resize(function() {
        var escContainerName = "#" + mapContainerName.replace(":", "\\:");
        if ($(escContainerName).hasClass("fullScreen")) {
            mapPanelContainer.setHeight($(window).height());
            mapPanelContainer.setWidth($('body').innerWidth());
        }
        else {
            mapPanelContainer.setWidth($(escContainerName).parent().width());
        }
    });

    // Toolbar legend and layer handlers

    // When map legend node gets selected, underlying layer will be recorded as selected
    function nodeSelectionHandler(node, e, forceSelect) {
        if (forceSelect) {
            node = mapLegendTree.getNodeById(node.id);
            mapLegendTree.getSelectionModel().select(node);
        }
        // Clean selected node editing css class
        if (selectedNode === null || selectedNode.id !== node.id) {
            selectedNode = node;
            if (enableMapEditing) {
                customizeMapToolbar();
            }
        }
        return true;
    }

    // Turns on and off map editing
    function onMapEditToggle(item, pressed) {
        enableMapEditing = pressed;
        customizeMapToolbar();
    }

    // Layer property change handler
    function handleLayerChange(evt) {
        if (evt.property === 'visibility') {
            if (selectedNode !== null && selectedNode.layer.id === evt.layer.id) {
                if (enableMapEditing) {
                    customizeMapToolbar();
                }
            }
        }
    }

    // Customizes map toolbar
    function customizeMapToolbar() {
        if (!enableMapEditing || selectedNode === null || !selectedNode.layer.isEditable || !selectedNode.layer.visibility) {
            disableMapEditing();
            return;
        }

        removeLayerEditingIcon();
        var selectedLayer = selectedNode.layer;

        if (typeof selectedNode.ui.textNode !== 'undefined') {
            $(selectedNode.ui.textNode).parent().parent().addClass('editingNode');
        }

        // Enable toolbar items
        for (var i = 0; i < mapToolbar.items.items.length; i++) {
            var tbButton = mapToolbar.items.items[i];
            if (tbButton.editingTool) {

                // Enable shape editing tool
                var allowDrawing = true;
                if (typeof selectedLayer.allowMultipleFeatures !== 'undefined') {
                    if (!selectedLayer.allowMultipleFeatures && selectedLayer.features.length > 0) {
                        allowDrawing = false;
                    }
                }

                if (tbButton.id === OT.Map.TOOLBAR_BUTTON_IDS.SNAP) {
                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.setLayer(selectedLayer);

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();

                } else if (tbButton.id === OT.Map.TOOLBAR_BUTTON_IDS.DELETE_FEATURE) {
                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.setLayer(selectedLayer);

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();

                } else if (tbButton.id === OT.Map.TOOLBAR_BUTTON_IDS.EDIT_SHAPE) {
                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.layer = selectedLayer;
                    control.virtualStyle = selectedLayer.virtualNodeStyle;

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();

                } else if (tbButton.id === OT.Map.TOOLBAR_BUTTON_IDS.EDIT_FEATURE && typeof selectedLayer.editFeatureFunc !== 'undefined') {
                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.setLayer(selectedLayer);

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();
                } else if (((tbButton.id === OT.Map.TOOLBAR_BUTTON_IDS.DRAW_POLYGON && selectedLayer.allowPolygon) ||
                        (tbButton.id === OT.Map.TOOLBAR_BUTTON_IDS.DRAW_LINE && selectedLayer.allowLine) ||
                        (tbButton.id === OT.Map.TOOLBAR_BUTTON_IDS.DRAW_POINT && selectedLayer.allowPoint))
                        && allowDrawing) {

                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.layer = selectedLayer;
                    control.handler.style = selectedLayer.virtualNodeStyle;

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();

                } else {
                    tbButton.toggle(false);
                    tbButton.disable();
                }
            }
        }
    }

    // Calls feature editing functions, related to the layer it belongs to
    function featureAdded(feature) {
        customizeMapToolbar();
        if (feature !== null && typeof feature.layer.editFeatureFunc !== 'undefined') {
            feature.layer.editFeatureFunc(feature);
        }
    }

    // Removes editing icon from selected layer
    function removeLayerEditingIcon() {
        if (typeof mapLegendTree.root.childNodes !== 'undefined') {
            for (var i = 0; i < mapLegendTree.root.childNodes.length; i++) {
                if (typeof mapLegendTree.root.childNodes[i].ui.textNode !== 'undefined') {
                    $(mapLegendTree.root.childNodes[i].ui.textNode).parent().parent().removeClass('editingNode');
                }
            }
        }
    }

    // Disable all editing tools
    function disableMapEditing() {
        removeLayerEditingIcon();
        for (var i = 0; i < mapToolbar.items.items.length; i++) {
            if (mapToolbar.items.items[i].editingTool) {
                mapToolbar.items.items[i].toggle(false);
                mapToolbar.items.items[i].disable();
            }
        }
    }

    // Claim information tool
    var getClaimUrl = this.applicationUrl + "/ws/" + this.languageCode + "/claim/getclaimbypoint";
    var viewClaimUrl = this.applicationUrl + "/claim/ViewClaim.xhtml";
    var mapWaitContent = "<div id='mapWaitContent' class='mapWaitDiv'>" + MSG.MAP_CONTROL_LOADING + "</div>";
    var mapNoResutlsContent = "<div id='mapNoResutlsContent' class='mapNoResultsDiv'>" + MSG.MAP_CONTROL_CLAIM_NOT_FOUND + "</div>";
    var mapClaimInfoContent = "<div id='mapClaimInfoContent' class='mapClaimInfoDiv'>" +
            "<div class='line'>" +
            "<a href='' id='claimNr' target='_blank'></a>" +
            "</div>" +
            "<div class='line'>" + MSG.MAP_CONTROL_CLAIMANT +
            "<br /><b><span id='claimantName'></span></b> " +
            "</div>" +
            "<div class='line'>" + MSG.MAP_CONTROL_LODGED +
            " <br /><b><span id='claimLodgingDate'></span></b>" +
            "</div>" +
            "<div class='line'>" + MSG.MAP_CONTROL_STATUS +
            " <br /><b><span id='claimStatus'></span></b>" +
            "</div>" +
            "</div>";

    var claimInfoPopup = null;
    var xhr;

    function handleClaimInfoClick(evt)
    {
        if (typeof xhr !== 'undefined') {
            xhr.abort();
        }

        var lonlat = map.getLonLatFromViewPortPx(evt.xy);
        var coords = map.getLonLatFromViewPortPx(evt.xy).transform(that.destCrs, that.sourceCrs);

        if (claimInfoPopup === null) {
            claimInfoPopup = new OpenLayers.Popup.FramedCloud(MSG.MAP_CONTROL_CLAIM_INFO,
                    lonlat,
                    new OpenLayers.Size(220, 220),
                    mapWaitContent,
                    null, true, null);
            claimInfoPopup.panMapIfOutOfView = true;
            map.addPopup(claimInfoPopup);
        } else {
            claimInfoPopup.lonlat = lonlat;
            claimInfoPopup.setContentHTML(mapWaitContent);
            claimInfoPopup.show();
        }

        xhr = $.ajax({
            url: getClaimUrl + '?x=' + coords.lon + '&y=' + coords.lat,
            type: 'GET',
            crossDomain: true,
            dataType: 'json',
            success: function(response) {
                populateFeatureInfo(response);
            },
            error: function(xhr, status) {
                claimInfoPopup.setContentHTML(mapNoResutlsContent);
            }
        });
    }

    function populateFeatureInfo(response) {
        try {
            if (response === "") {
                claimInfoPopup.setContentHTML(mapNoResutlsContent);
            } else {
                var lodgingDate = "";
                if (response.lodgementDate) {
                    lodgingDate = new Date(parseDate(response.lodgementDate)).toDateString();
                }
                claimInfoPopup.setContentHTML(mapClaimInfoContent);
                $("#claimNr").attr('href', viewClaimUrl + '?id=' + response.id);
                $("#claimNr").text('#' + response.nr);
                $("#claimantName").text(response.claimantName);
                $("#claimLodgingDate").text(lodgingDate);
                $("#claimStatus").text(response.statusName);
            }
        }
        catch (ex) {
            claimInfoPopup.hide();
            alert(ex);
        }
    }
};

OT.Map.Layer = {};
OT.Map.Control = {};

// Map toolbar buttons ids
OT.Map.TOOLBAR_BUTTON_IDS = {
    ZOOM_TO_EXTENT: "btnZoomToExtent",
    ZOOM_IN: "btnZoomIn",
    ZOOM_OUT: "btnZoomOut",
    PAN: "btnPan",
    CLAIM_INFO: "btnClaimInfo",
    EDIT_MAP: "btnEditMap",
    DRAW_POLYGON: "btnDrawPolygon",
    DRAW_LINE: "btnDrawLine",
    DRAW_POINT: "btnDrawPoint",
    EDIT_SHAPE: "btnEditShape",
    ADD_NODE: "btnAddNode",
    REMOVE_NODE: "btnRemoveNode",
    DELETE_FEATURE: "btnDeleteFeature",
    EDIT_FEATURE: "btnEditFeature",
    SNAP: "btnSnapping",
    MAXIMIZE_MAP: "btnMaximizeMap"
};

// Map layers ids
OT.Map.LAYER_IDS = {
    COMMUNITY_AREA: "layerCommunityArea",
    GOOGLE_EARTH: "layerGoogleEarth",
    GOOGLE_MAP: "layerGoogleMap",
    CURRENT_CLAIM: "layerCurrentClaim",
    CLAIM_ADDITIONAL_LOCATIONS: "layerClaimAdditionalLocations"
};

// Extend OpenLayers objects
OT.Map.Control.ScaleBar = OpenLayers.Class(OpenLayers.Control.ScaleBar, {
    styleValue: function(selector, key) {
        var value = 0;
        if (this.limitedStyle) {
            value = this.appliedStyles[selector][key];
        } else {
            selector = "." + this.displayClass + selector;
            rules:
                    for (var i = document.styleSheets.length - 1; i >= 0; --i) {
                var sheet = document.styleSheets[i];
                if (!sheet.disabled) {
                    var allRules;
                    try {
                        if (typeof (sheet.cssRules) == 'undefined') {
                            if (typeof (sheet.rules) == 'undefined') {
                                // can't get rules, keep looking
                                continue;
                            } else {
                                allRules = sheet.rules;
                            }
                        } else {
                            allRules = sheet.cssRules;
                        }
                    } catch (err) {
                        continue;
                    }
                    if (allRules && allRules !== null) {
                        for (var ruleIndex = 0; ruleIndex < allRules.length; ++ruleIndex) {
                            var rule = allRules[ruleIndex];
                            if (rule.selectorText &&
                                    (rule.selectorText.toLowerCase() == selector.toLowerCase())) {
                                if (rule.style[key] != '') {
                                    value = parseInt(rule.style[key]);
                                    break rules;
                                }
                            }
                        }
                    }
                }
            }
        }
        // if the key was not found, the equivalent value is zero
        return value ? value : 0;
    }
});

OT.Map.Layer.VectorLayer = function(id, name, params) {
    OpenLayers.Layer.Vector.call(this, name, params);
    if (id) {
        this.id = id;
    }
    this.isEditable = true;
    this.allowPolygon = true;
    this.allowPoint = false;
    this.allowLine = false;
    this.allowMultipleFeatures = false;
    this.virtualNodeStyle = "";
    this.editFeatureFunc;

    if (params.hasOwnProperty('virtualNodeStyle'))
        this.virtualNodeStyle = params.virtualNodeStyle;
    if (params.hasOwnProperty('isEditable'))
        this.isEditable = params.isEditable;
    if (params.hasOwnProperty('allowPolygon'))
        this.allowPolygon = params.allowPolygon;
    if (params.hasOwnProperty('allowPoint'))
        this.allowPoint = params.allowPoint;
    if (params.hasOwnProperty('allowLine'))
        this.allowLine = params.allowLine;
    if (params.hasOwnProperty('allowMultipleFeatures'))
        this.allowMultipleFeatures = params.allowMultipleFeatures;
    if (params.hasOwnProperty('editFeatureFunc'))
        this.editFeatureFunc = params.editFeatureFunc;
    return this;
};
OT.Map.Layer.VectorLayer.prototype = createObject(OpenLayers.Layer.Vector.prototype);
OT.Map.Layer.VectorLayer.prototype.constructor = OT.Map.Layer.VectorLayer;

// Extend drawing control
OT.Map.Control.DrawFeature = OpenLayers.Class(OpenLayers.Control.DrawFeature, {
    handlers: null,
    initialize: function(layer, handler, options) {
        OpenLayers.Control.DrawFeature.prototype.initialize.apply(this, [layer, handler, options]);
        // configure the keyboard handler
        var keyboardOptions = {
            keydown: this.handleKeypress
        };
        this.handlers = {
            keyboard: new OpenLayers.Handler.Keyboard(this, keyboardOptions)
        };
    },
    handleKeypress: function(evt) {
        var code = evt.keyCode;
        // ESCAPE pressed. Remove feature from map
        if (code === 27) {
            this.cancel();
        }
        // DELETE pressed. Remove third last vertix
        if (code === 46) {
            this.undo();
        }
        return true;
    },
    activate: function() {
        return this.handlers.keyboard.activate() &&
                OpenLayers.Control.DrawFeature.prototype.activate.apply(this, arguments);
    },
    deactivate: function() {
        var deactivated = false;
        // the return from the controls is unimportant in this case
        if (OpenLayers.Control.DrawFeature.prototype.deactivate.apply(this, arguments)) {
            this.handlers.keyboard.deactivate();
            deactivated = true;
        }
        return deactivated;
    },
    CLASS_NAME: "OT.DrawFeature"
});

OT.Map.Styles = {
    styleLocationsNode: {
        pointRadius: 5,
        graphicName: "circle",
        fillColor: "white",
        fillOpacity: 0.5,
        strokeWidth: 2,
        strokeOpacity: 0.3,
        strokeColor: "#E96EFF"
    },
    styleMapLocations: new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            label: "${getLabel}",
            fontSize: "12px",
            fontFamily: "Arial",
            labelOutlineColor: "white",
            labelOutlineWidth: 3
        }, {
            context: {getLabel: function(feature) {
                    if (typeof feature.attributes.description !== 'undefined') {
                        return feature.attributes.description;
                    } else {
                        return "";
                    }
                }},
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 5,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 0.7,
                            labelYOffset: -10,
                            strokeWidth: 2,
                            strokeColor: "#E96EFF"
                        },
                        "Line": {
                            strokeWidth: 2,
                            strokeColor: "#E96EFF",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#ED87FF",
                            fillOpacity: 0,
                            strokeColor: "#E96EFF",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        }),
        "select": new OpenLayers.Style(null, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 7,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 1,
                            strokeWidth: 2,
                            strokeColor: "#E96EFF"
                        },
                        "Line": {
                            strokeWidth: 3,
                            strokeColor: "#E96EFF",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#E96EFF",
                            fillOpacity: 0.3,
                            strokeColor: "#E96EFF",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        }),
        "temporary": new OpenLayers.Style(null, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 7,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 1,
                            strokeWidth: 2,
                            strokeColor: "#E96EFF"
                        },
                        "Line": {
                            strokeWidth: 2,
                            strokeColor: "#E96EFF",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#E96EFF",
                            fillOpacity: 0.3,
                            strokeColor: "#E96EFF",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        })
    }),
    styleVirtualNode: {
        pointRadius: 4,
        graphicName: "circle",
        fillColor: "white",
        fillOpacity: 0.5,
        strokeWidth: 2,
        strokeColor: "#F5856F",
        strokeOpacity: 0.3
    },
    styleMapCommunityLayer: new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            labelOutlineColor: "white",
            labelOutlineWidth: 3
        }, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 5,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 0.7,
                            strokeWidth: 2,
                            strokeColor: "#F5856F"
                        },
                        "Line": {
                            strokeWidth: 2,
                            strokeColor: "#F5856F",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#ff0000",
                            fillOpacity: 0,
                            strokeColor: "#F5856F",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        }),
        "select": new OpenLayers.Style(null, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 7,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 1,
                            strokeWidth: 2,
                            strokeColor: "#F5856F"
                        },
                        "Line": {
                            strokeWidth: 3,
                            strokeColor: "#F5856F",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#F5856F",
                            fillOpacity: 0.3,
                            strokeColor: "#F5856F",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        }),
        "temporary": new OpenLayers.Style(null, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 7,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 1,
                            strokeWidth: 2,
                            strokeColor: "#F5856F"
                        },
                        "Line": {
                            strokeWidth: 2,
                            strokeColor: "#F5856F",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#F5856F",
                            fillOpacity: 0.3,
                            strokeColor: "#F5856F",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        })
    })
};

