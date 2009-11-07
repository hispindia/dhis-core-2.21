
// -----------------------------------------------------------------------------
// $Id: general.js 4948 2008-04-21 06:11:33Z leifas $
// -----------------------------------------------------------------------------
// Selection
// -----------------------------------------------------------------------------

function organisationUnitSelected( orgUnits )
{
    window.location.href = 'select.action';
}

selection.setListenerFunction( organisationUnitSelected );

function changeOrder()
{
    window.open( 'getDataElementOrder.action', '_blank', 'width=700,height=500,scrollbars=yes' );
}

// -----------------------------------------------------------------------------
// Comments
// -----------------------------------------------------------------------------

function commentSelected( dataElementId, optionComboId )
{  
    var commentSelector = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );
    var commentField = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );

    var value = commentSelector.options[commentSelector.selectedIndex].value;
    
    if ( value == 'custom' )
    {
        commentSelector.style.display = 'none';
        commentField.style.display = 'inline';
        
        commentField.select();
        commentField.focus();
    }
    else
    {
        commentField.value = value;
        
        saveComment( dataElementId, optionComboId, value );
    }
}

function commentLeft( dataElementId, optionComboId )
{
    var commentField = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );
    var commentSelector = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );

    saveComment( dataElementId, optionComboId, commentField.value );

    var value = commentField.value;
    
    if ( value == '' )
    {
        commentField.style.display = 'none';
        commentSelector.style.display = 'inline';

        commentSelector.selectedIndex = 0;
    }
}

// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

function saveValue( dataElementId, optionComboId, dataElementName, zeroValueSaveMode )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value' + ':' +  'value[' + optionComboId + '].value');
    var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML; 
    
    field.style.backgroundColor = '#ffffcc';   
    
    if ( field.value != '' )
    {
        if ( type == 'int' )        
        {
        	var value = new Number( field.value );       	        	
        	 
        	if( value * 1 == 0 && !zeroValueSaveMode )
        	{
        		window.alert( i18n_saving_zero_values_unnecessary  );
        		field.select();
   	            field.focus(); 
   	                   		
				return;    
				    		
        	}
            if ( !isInt( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + dataElementName );

                field.select();
                field.focus();

                return;
            }
            else
            {
                var minString = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].min' ).innerHTML;
                var maxString = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].max' ).innerHTML;

                if ( minString.length != 0 && maxString.length != 0 )
                {
                    var value = new Number( field.value );
                    var min = new Number( minString );
                    var max = new Number( maxString );

                    if ( value < min )
                    {
                        var valueSaver = new ValueSaver( dataElementId, optionComboId, field.value, '#ffcccc' );
                        valueSaver.save();
                        
                        window.alert( i18n_value_of_data_element_less + '\n\n' + dataElementName );
                        
                        return;
                    }

                    if ( value > max )
                    {
                        var valueSaver = new ValueSaver( dataElementId, optionComboId, field.value, '#ffcccc' );
                        valueSaver.save();
                        
                        window.alert( i18n_value_of_data_element_greater + '\n\n' + dataElementName);
                        
                        return;
                    }
                }
            }
        }
    }

    var valueSaver = new ValueSaver( dataElementId, optionComboId, field.value, '#ccffcc', '' );
    valueSaver.save();    
    
    if ( type == 'int')
    {
    	calculateCDE(dataElementId);
    }

}

function saveBoolean( dataElementId, optionComboId, selectedOption )
{
    selectedOption.style.backgroundColor = '#ffffcc';
    
    var valueSaver = new ValueSaver( dataElementId, optionComboId, selectedOption.options[selectedOption.selectedIndex].value, '#ccffcc', selectedOption );
    valueSaver.save();
}

function saveDate( dataElementId, dataElementName )
{
	
	var field = document.getElementById( 'value[' + dataElementId + '].date' );
    var type = document.getElementById( 'value[' + dataElementId + '].valueType' ).innerHTML;
    
    field.style.backgroundColor = '#ffffcc';
    
    var valueSaver = new ValueSaver( dataElementId, '', field.value, '#ccffcc', '' );
    valueSaver.save();
}

function saveComment( dataElementId, optionComboId, commentValue )
{
    var field = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );                
    var select = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );
    
    field.style.backgroundColor = '#ffffcc';
    select.style.backgroundColor = '#ffffcc';
    
    var commentSaver = new CommentSaver( dataElementId, optionComboId, commentValue );
    commentSaver.save();
}

function isInt( value )
{
    var number = new Number( value );
    
    if ( isNaN( number ))
    {
        return false;
    }
    
    return true;
}

// -----------------------------------------------------------------------------
// Saver objects
// -----------------------------------------------------------------------------

function ValueSaver( dataElementId_, optionComboId_, value_, resultColor_, selectedOption_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var optionComboId = optionComboId_;
    var value = value_;
    var resultColor = resultColor_;
    var selectedOption = selectedOption_; 
    
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveValue.action?dataElementId=' +
                dataElementId + '&value=' + value );
    };
    
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        
        if ( code == 0 )
        {
            markValue( resultColor );                   
        }
        else
        {
            markValue( ERROR );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }   
    
    function markValue( color )
    {
        var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML;       
        
        var element;
        
        if ( type == 'bool' )
        {
            element = document.getElementById( 'value[' + dataElementId + '].boolean' );
        }
        else if( type == 'date' )
        {
        	element = document.getElementById( 'value[' + dataElementId + '].date' );
        }
        else if( selectedOption )
        {
        	element = selectedOption;    
        }
        else
        {           
            element = document.getElementById( 'value[' + dataElementId + '].value' + ':' +  'value[' + optionComboId + '].value');                      
        }
                
        element.style.backgroundColor = color;
    }
}

function CommentSaver( dataElementId_, optionComboId_, value_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var optionComboId = optionComboId_
    var value = value_;
    
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveComment.action?dataElementId=' +
                dataElementId + '&optionComboId=' + optionComboId + '&comment=' + value );
    };
    
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        
        if ( code == 0 )
        {
            markComment( SUCCESS );           
        }
        else
        {
            markComment( ERROR );
            window.alert( i18n_saving_comment_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markComment( ERROR );
        window.alert( i18n_saving_comment_failed_error_code + '\n\n' + errorCode );
    }
    
    function markComment( color )
    {
        var field = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );                
        var select = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );        

        field.style.backgroundColor = color;
        select.style.backgroundColor = color;
    }
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validate()
{
    window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
}

// -----------------------------------------------------------------------------
// CalculatedDataElements
// -----------------------------------------------------------------------------

/**
 * Calculate and display the value of any CDE the given data element is a part of.
 * @param dataElementId  id of the data element to calculate a CDE for
 */
function calculateCDE( dataElementId )
{
    var cdeId = getCalculatedDataElement(dataElementId);
  
    if ( ! cdeId )
    {
  	    return;
    }
    
    var factorMap = calculatedDataElementMap[cdeId];
    var value = 0;
    var dataElementValue;
    
    for ( dataElementId in factorMap )
    {
    	dataElementValue = document.getElementById( 'value[' + dataElementId + '].value' ).value;
    	value += ( dataElementValue * factorMap[dataElementId] );
    }
    
    document.getElementById( 'value[' + cdeId + '].value' ).value = value;
}

/**
 * Returns the id of the CalculatedDataElement this DataElement id is a part of.
 * @param dataElementId id of the DataElement
 * @return id of the CalculatedDataElement this DataElement id is a part of,
 *     or null if the DataElement id is not part of any CalculatedDataElement
 */
function getCalculatedDataElement( dataElementId )
{
    for ( cdeId in calculatedDataElementMap )
    {
  	    var factorMap = calculatedDataElementMap[cdeId];

  	    if ( deId in factorMap )
  	    {
  	    	return cdeId;
  	    }
    }

    return null;
}

function calculateAndSaveCDEs()
{
    var request = new Request();
    request.setCallbackSuccess( dataValuesReceived );
    request.setResponseTypeXML( 'dataValues' );
    request.send( 'calculateCDEs.action' );
}

function dataValuesReceived( node )
{
	var values = node.getElementsByTagName('dataValue');
    var dataElementId;
    var value;

	for ( var i = 0, value; value = values[i]; i++ )
	{
		dataElementId = value.getAttribute('dataElementId');
		value = value.firstChild.nodeValue;		
		document.getElementById( 'value[' + dataElementId + '].value' ).value = value;
	}
}