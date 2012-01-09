<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
<NamedLayer>
     <Name>Survey Controls</Name>
    <UserStyle>
      <Title>Default point</Title>
      <Abstract>A sample style that just prints out a 6px wide red square</Abstract>
	<FeatureTypeStyle>
     <Rule>
       <Name>Scale &lt; 2500</Name>
       <MaxScaleDenominator>2500</MaxScaleDenominator>
       <PointSymbolizer>
         <Graphic>
           <Mark>
             <WellKnownName>square</WellKnownName>
             <Fill>
               <CssParameter name="fill">#6C2DC7</CssParameter>
             </Fill>
           </Mark>
           <Size>8</Size>
         </Graphic>
       </PointSymbolizer>
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
               <AnchorPointY>0.0</AnchorPointY>
             </AnchorPoint>
             <Displacement>
               <DisplacementX>0</DisplacementX>
               <DisplacementY>5</DisplacementY>
             </Displacement>
           </PointPlacement>
         </LabelPlacement> 		 
         <Fill>
           <CssParameter name="fill">#000000</CssParameter>
         </Fill>
       </TextSymbolizer>
     </Rule>
     <Rule>
       <Name>Scale 2500 - 90000</Name>
       <MinScaleDenominator>2500</MinScaleDenominator>
       <MaxScaleDenominator>90000</MaxScaleDenominator>
       <PointSymbolizer>
         <Graphic>
           <Mark>
             <WellKnownName>square</WellKnownName>
             <Fill>
               <CssParameter name="fill">#6C2DC7</CssParameter>
             </Fill>
           </Mark>
           <Size>4</Size>
         </Graphic>
       </PointSymbolizer>
	    <TextSymbolizer>
         <Label>
           <ogc:PropertyName>label</ogc:PropertyName>
         </Label>
         <Fill>
           <CssParameter name="fill">#000000</CssParameter>
         </Fill>
       </TextSymbolizer>
	   </Rule>
     <Rule>
       <Name>Scale &gt; 90000</Name>
       <MinScaleDenominator>90000</MinScaleDenominator>
       <PointSymbolizer>
         <Graphic>
           <Mark>
             <WellKnownName>square</WellKnownName>
             <Fill>
               <CssParameter name="fill">#6C2DC7</CssParameter>
             </Fill>
           </Mark>
           <Size>4</Size>
         </Graphic>       
	   </PointSymbolizer>	   
     </Rule>
   </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>

   
   
   
   
 
   
   