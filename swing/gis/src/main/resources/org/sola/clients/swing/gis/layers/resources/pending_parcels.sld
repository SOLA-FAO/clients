<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>Pending parcels</Name>
    <UserStyle>
      <Title>Default polygon style</Title>
      <Abstract>Symbology definition for pending parcels</Abstract>
      <FeatureTypeStyle>
        <Rule>
          <Title>Pending parcels</Title>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
              <CssParameter name="opacity">0.3</CssParameter>
            </Fill>
            <Stroke>
              <CssParameter name="stroke">#800517</CssParameter>
              <CssParameter name="stroke-width">2</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
           <TextSymbolizer>
             <Label>
               <ogc:PropertyName>label</ogc:PropertyName>
             </Label>
             <Font>
               <CssParameter name="font-family">Arial</CssParameter>
               <CssParameter name="font-size">13</CssParameter>
               <CssParameter name="font-style">normal</CssParameter>
               <CssParameter name="font-weight">normal</CssParameter>
             </Font>
             <LabelPlacement>
               <PointPlacement>
                 <AnchorPoint>
                   <AnchorPointX>0.5</AnchorPointX>
                   <AnchorPointY>0.5</AnchorPointY>
                 </AnchorPoint>
               </PointPlacement>
             </LabelPlacement>
             <Fill>
               <CssParameter name="fill">#000000</CssParameter>
             </Fill>
           </TextSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>