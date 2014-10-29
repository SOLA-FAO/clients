function saveCommunityArea() {
    mapControl.toggleMapEditing(false);
    var wkt = new OpenLayers.Format.WKT();
    var communityAreaLayer = mapControl.getMap().getLayer(OT.Map.LAYER_IDS.COMMUNITY_AREA);

    // Do Checks
    if (communityAreaLayer.features.length < 1) {
        alert('Provide community area');
        return false;
    }

    var feature = communityAreaLayer.features[0].clone();
    feature.geometry.transform(destCrs, sourceCrs);
    $("#mainForm\\:hCommunityArea").val(wkt.write(feature));
    return true;
}