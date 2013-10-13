jQuery( document ).ready( function()
{
    jQuery( "#name" ).focus();
    
    changeRuleType();

    validation2( 'updateValidationRuleGroupForm', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            selectAllById( 'groupMembers' );
            selectAllById( 'userRolesToAlert' );
        },
        'rules' : getValidationRules( "validationRuleGroup" )
    } );

    checkValueIsExist( "name", "validateValidationRuleGroup.action", {
        id : getFieldValue( 'id' )
    } );
} );
