
var aKey	= null;
var aMerged = null;

function reportChanged() {
	
	window.location.href="selectExportReportParam.action?reportGroup=" + getListValue("group") + "&reportId=" + getListValue("report");
}

function previewAdvandReport()
{	
	aKey	= null;
	aMerged = null;
	
	var params = "reportId=" + byId('report').value + "&periodId=" + byId('period').value + "&organisationGroupId=" + byId("availableOrgunitGroups").value;

	
	$("#processing").showAtCenter(true);
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewReportReceived );
	request.sendAsPost( params );
	request.send( "previewAdvancedReportExcel.action" );	
}

function previewReport() {
	
	aKey	= null;
	aMerged = null;
	
	var url = "reportId=" + getListValue('report') + "&periodId=" + getListValue('period');
	
	if ( byId('availableOrgunitGroups') != null ) {
	
		url = "previewAdvancedReportExcel.action?orgunitGroupId=" + orgunitGroupId + "&" + url;
	}
	else {
		url = "previewReportExcel.action?" + url;
	}
	
	$("#processing").showAtCenter(true);
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewReportReceived );
	request.send( url );	
	
}

function previewReportReceived( reportXML ) {
	
	createTabs( reportXML );	
	setMergedNumberForEachCell( reportXML );
	exportFromXMLtoHTML( reportXML );
	
	deleteDivEffect();
	$("#processing").hide();
	
}

function createTabs( reportXML )
{
	var tabs = document.getElementById( 'tabs' );
	
	if(tabs!=null){	
		byId("preview").removeChild(tabs);
	}

	tabs = document.createElement("div");
	tabs.id = "tabs";
	tabs.style.display = "none";
	byId("preview").appendChild(tabs);
	
	var _sheets		= reportXML.getElementsByTagName( 'sheet' );	
	
	var divHTML = "";
	var html = "<ul>";
		
	for(var i=0;i<_sheets.length;i++){
		var sheet = _sheets.item(i) ;
		var id = sheet.getAttribute("id");
		
		html += "<li><a href='#fragment-" + id + "'>" + id + "</a></li>";
		divHTML += "<div id='fragment-" + id + "'></div>";
	}
	
	html += "</ul>";	
	
	byId("tabs").innerHTML = html + divHTML;	
	
	
	$("#tabs").tabs({collapsible : true});
}


function setMergedNumberForEachCell( parentElement ) {
		
	aKey 		= new Array();
	aMerged 	= new Array();
	
	var cells 	= parentElement.getElementsByTagName( 'cell' );
	
	for (var i  = 0 ; i < cells.length ; i ++)
	{	
		aKey[i]	= cells[i].getAttribute( 'iKey' );
		aMerged[i]	= cells[i].firstChild.nodeValue;
	}
}

function getMergedNumberForEachCell( sKey ) {

	for (var i = 0 ; i < aKey.length ; i ++)
	{
		if ( sKey == aKey[i] )
		{
			return Number(aMerged[i]);
		}
	}
return 1;
}

function exportFromXMLtoHTML( parentElement ) {

	var _index		= 0;
	var _orderSheet	= 0;
	var _sHTML		= "";
	var _sPattern	= "";
	var _rows 		= "";
	var _cols 		= "";
	var _sheets		= parentElement.getElementsByTagName( 'sheet' );
	
	for (var s = 0 ; s < _sheets.length ; s ++)
	{
		_rows 		= _sheets[s].getElementsByTagName( 'row' );
		_orderSheet	= getRootElementAttribute( _sheets[s], "id" );

		_sHTML = "<table class='formatTablePreview'>";
		
		for (var i = 0 ; i < _rows.length ; i ++)
		{
			_index	 = 0;
			_sHTML 	+= "<tr>";
			
			_cols 	 = _rows[i].getElementsByTagName( 'col' );
			
			for (var j 	= 0 ; j < _cols.length ; )
			{
				var _number	= getRootElementAttribute( _cols[j], 'no' );
				
				// Printing out the unformatted cells
				for (; _index < _number ; _index ++)
				{
					_sHTML += "<td/>";
				}

				if ( _index == _number )
				{
					var _colspan 	= 1;
					var _sData		= getElementValue( _cols[j], 'data' );				
					var _align		= getElementAttribute( _cols[j], 'format', 'align' );
				
					// If this cell is merged - Key's form: Sheet#Row#Col
					_sPattern 		=  _orderSheet + "#" + i + "#" + _number;
					_colspan 		= getMergedNumberForEachCell( _sPattern );
					
					// Jumping for <For Loop> AND <Empty Cells>
					j 		= Number(j) + Number(_colspan);
					_index 	= Number(_index) + Number(_colspan);
					
					_sHTML += "<td align='" + _align + "' colspan='" + _colspan;
					
					if ( isNaN(_sData) == false )
					{
						_sHTML += "' class='formatNumberPreview";
					}
					else
					{
						_sHTML += "' class='formatStringPreview";
					}
					_sHTML += "'>"+ _sData + "</td>";
				}
			}
			_sHTML += "</tr>";
		}
		_sHTML += "</table><br/>";
		
		if ( byId( "fragment-" + _orderSheet ) != null )
		{
			byId( "fragment-" + _orderSheet ).innerHTML = _sHTML;
		}
	}
	
	showById("tabs");
	
	window.status = "DATAWARE HOUSE";
	window.stop();
}

