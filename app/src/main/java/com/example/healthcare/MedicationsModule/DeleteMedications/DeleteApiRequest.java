package com.example.healthcare.MedicationsModule.DeleteMedications;

public class DeleteApiRequest {
    private Integer patientId;
    private Integer careplanId;
    private Integer medicationId;
    private String lastEffectiveDate;
    private String activeFlag;
    private String logId;
    private String careplanLogMessageUserInput;
    private String careplanLogMessage;
    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getCareplanId() {
        return careplanId;
    }

    public void setCareplanId(Integer careplanId) {
        this.careplanId = careplanId;
    }

    public Integer getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Integer medicationId) {
        this.medicationId = medicationId;
    }

    public String getLastEffectiveDate() {
        return lastEffectiveDate;
    }

    public void setLastEffectiveDate(String lastEffectiveDate) {
        this.lastEffectiveDate = lastEffectiveDate;
    }

    public String getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getCareplanLogMessageUserInput() {
        return careplanLogMessageUserInput;
    }

    public void setCareplanLogMessageUserInput(String careplanLogMessageUserInput) {
        this.careplanLogMessageUserInput = careplanLogMessageUserInput;
    }

    public String getCareplanLogMessage() {
        return careplanLogMessage;
    }

    public void setCareplanLogMessage(String careplanLogMessage) {
        this.careplanLogMessage = careplanLogMessage;
    }


}
