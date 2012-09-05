jQuery(document).ready(	function(){
	
	validation( 'addProgramForm', function( form ){ 
		enable('dateOfEnrollmentDescription');
		enable('dateOfIncidentDescription');
		form.submit();
	}, function(){
		var selectedPropertyIds = jQuery( "#selectedPropertyIds" );
		selectedPropertyIds.empty();
		var personDisplayNames = jQuery( "#personDisplayNames" );
		personDisplayNames.empty();
		
		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			
			selectedPropertyIds.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
			
			var displayed = jQuery( item ).find( "input[name='displayed']:first");
			var checked = displayed.attr('checked') ? true : false;
			personDisplayNames.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
		});
	});
	
	
	checkValueIsExist( "name", "validateProgram.action");
});	