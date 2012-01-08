<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
   <NamedLayer>
    <Name>Road</Name>
    <UserStyle>
    <!-- Styles can have names, titles and abstracts -->
      <Title>Road newwork</Title>
      <Abstract>A symbology defintion for SOLA cadastre map point</Abstract>
      <!-- FeatureTypeStyles describe how to render different features -->
      <!-- A FeatureTypeStyle for rendering lines -->
      <FeatureTypeStyle>
	  <Rule>
       <Name>Scale &lt; 2500</Name>
       <MaxScaleDenominator>2500</MaxScaleDenominator>
       <PolygonSymbolizer>
         <Stroke>
           <CssParameter name="stroke">#F88017</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>
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
         <Fill>
           <CssParameter name="fill">#151B8D</CssParameter>
         </Fill>
         <VendorOption name="followLine">true</VendorOption>
         <VendorOption name="maxAngleDelta">90</VendorOption>
         <VendorOption name="maxDisplacement">10</VendorOption>
         <VendorOption name="repeat">150</VendorOption>
         <VendorOption name="labelAllGroup">true</VendorOption>
         <LabelPlacement>
           <LinePlacement >
 		<PerpendicularOffset>5</PerpendicularOffset> 
		</LinePlacement>         
          </LabelPlacement>
       </TextSymbolizer>  	   
     </Rule>
     <Rule>
       <Name>Scale 2500 - 90000</Name>
       <MinScaleDenominator>2500</MinScaleDenominator>
       <MaxScaleDenominator>90000</MaxScaleDenominator>
       <PolygonSymbolizer>
         <Stroke>
           <CssParameter name="stroke">#F88017</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>
         </Stroke>
       </PolygonSymbolizer>
     </Rule>	  
	 <Rule>
       <Name>Scale &gt; 90000</Name>
       <MinScaleDenominator>90000</MinScaleDenominator>
       <PolygonSymbolizer>
         <Stroke>
           <CssParameter name="stroke">#F88017</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>
         </Stroke>
       </PolygonSymbolizer>
     </Rule>	  
 </FeatureTypeStyle>	  	  
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>