package org.hisp.dhis.patient.action.mobilesetting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientMobileSetting;
import org.hisp.dhis.patient.PatientMobileSettingService;

import com.opensymphony.xwork2.Action;

public class UpdateMobileSettingAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private PatientAttributeService patientAttributeService;

    public PatientAttributeService getPatientAttributeService()
    {
        return patientAttributeService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private PatientMobileSettingService patientMobileSettingService;

    public PatientMobileSettingService getPatientMobileSettingService()
    {
        return patientMobileSettingService;
    }

    public void setPatientMobileSettingService( PatientMobileSettingService patientMobileSettingService )
    {
        this.patientMobileSettingService = patientMobileSettingService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    private Collection<String> selectedList = new HashSet<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    private String gender, dobtype, birthdate, bloodgroup, registrationdate;
    
    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public void setDobtype( String dobtype )
    {
        this.dobtype = dobtype;
    }

    public void setBirthdate( String birthdate )
    {
        this.birthdate = birthdate;
    }

    public void setBloodgroup( String bloodgroup )
    {
        this.bloodgroup = bloodgroup;
    }

    public void setRegistrationdate( String registrationdate )
    {
        this.registrationdate = registrationdate;
    }

    @Override
    public String execute()
        throws Exception
    {
        
        if(selectedList.size() > 0){
            PatientMobileSetting setting;
            if(patientMobileSettingService.getCurrentSetting().size()>0){
                setting = patientMobileSettingService.getCurrentSetting().iterator().next();
                List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                fillValues( attributes );
                setting.setGender( Boolean.parseBoolean( gender ));
                setting.setDobtype( Boolean.parseBoolean(dobtype ));
                setting.setBirthdate( Boolean.parseBoolean(birthdate ));
                setting.setBloodgroup( Boolean.parseBoolean(bloodgroup ));
                setting.setRegistrationdate( Boolean.parseBoolean(registrationdate ));
                patientMobileSettingService.updatePatientMobileSetting( setting );
            }else{
                setting = new PatientMobileSetting();
                List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                setting.setGender( Boolean.parseBoolean( gender ));
                setting.setDobtype( Boolean.parseBoolean(dobtype ));
                setting.setBirthdate( Boolean.parseBoolean(birthdate ));
                setting.setBloodgroup( Boolean.parseBoolean(bloodgroup ));
                setting.setRegistrationdate( Boolean.parseBoolean(registrationdate ));
                fillValues( attributes );
                patientMobileSettingService.savePatientMobileSetting( setting );
            }
        }else{
            PatientMobileSetting setting;
            if(patientMobileSettingService.getCurrentSetting().size()>0){
                setting = patientMobileSettingService.getCurrentSetting().iterator().next();
                List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                setting.setGender( Boolean.parseBoolean( gender ));
                setting.setDobtype( Boolean.parseBoolean(dobtype ));
                setting.setBirthdate( Boolean.parseBoolean(birthdate ));
                setting.setBloodgroup( Boolean.parseBoolean(bloodgroup ));
                setting.setRegistrationdate( Boolean.parseBoolean(registrationdate ));
                fillValues( attributes );
                patientMobileSettingService.updatePatientMobileSetting( setting );
            }else{
                setting = new PatientMobileSetting();
                List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
                setting.setPatientAttributes( attributes );
                setting.setGender( Boolean.parseBoolean( gender ));
                setting.setDobtype( Boolean.parseBoolean(dobtype ));
                setting.setBirthdate( Boolean.parseBoolean(birthdate ));
                setting.setBloodgroup( Boolean.parseBoolean(bloodgroup ));
                setting.setRegistrationdate( Boolean.parseBoolean(registrationdate ));
                fillValues( attributes );
                patientMobileSettingService.savePatientMobileSetting( setting );
            }
        }
        return SUCCESS;
    }
    
    private void fillValues(List<PatientAttribute> attributes){
        for(String id : selectedList){
            attributes.add( patientAttributeService.getPatientAttribute( Integer.parseInt( id )) );
        }
    }

}
