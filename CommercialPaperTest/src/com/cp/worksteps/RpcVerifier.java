package com.cp.worksteps;

import com.cp.utils.*;
import com.newgen.iforms.EControl;
import com.newgen.iforms.FormDef;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.custom.IFormServerEventHandler;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RpcVerifier extends Shared implements IFormServerEventHandler, Constants, SharedI {
    private static final Logger logger = LogGenerator.getLoggerInstance(RpcVerifier.class);
    @Override
    public void beforeFormLoad(FormDef formDef, IFormReference ifr) {
        clearDecHisFlag(ifr);
        if (!isEmpty(getProcess(ifr))) showSelectedProcessSheet(ifr);
        if (getProcess(ifr).equalsIgnoreCase(commercialProcess)) cpFormLoadActivity(ifr);
    }

    @Override
    public String setMaskedValue(String s, String s1) {
        return s1;
    }

    @Override
    public JSONArray executeEvent(FormDef formDef, IFormReference iFormReference, String s, String s1) {
        return null;
    }

    @Override
    public String executeServerEvent(IFormReference ifr, String control, String event, String data) {
        try{
            switch (event){
                case cpApiCallEvent:{
                    switch (control) {
                    }
                }
                case formLoad:
                case onLoad:
                case onClick:{
                    switch (control){

                    }
                }
                case onChange:
                case custom:
                case onDone:{
                    switch (control){
                        case cpLienEvent:{
                            if (getCpDecision(ifr).equalsIgnoreCase(decApprove))
                                 cpProcessLien(ifr);
                        break;
                        }
                    }
                }
                break;
                case decisionHistory: {
                    setCpDecisionHistory(ifr);
                }
                break;
                case sendMail:
            }
        }
        catch (Exception e){
            logger.error("Exception occurred-- "+ e.getMessage());
        }
        return null;
    }

    @Override
    public JSONArray validateSubmittedForm(FormDef formDef, IFormReference iFormReference, String s) {
        return null;
    }

    @Override
    public String executeCustomService(FormDef formDef, IFormReference iFormReference, String s, String s1, String s2) {
        return null;
    }

    @Override
    public String getCustomFilterXML(FormDef formDef, IFormReference iFormReference, String s) {
        return null;
    }

    @Override
    public String generateHTML(EControl eControl) {
        return null;
    }

    @Override
    public String introduceWorkItemInWorkFlow(IFormReference iFormReference, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    public void cpSendMail(IFormReference ifr) {
        if (getCpDecision(ifr).equalsIgnoreCase(decApprove))
            message = "A request to "+getCpLienType(ifr)+" a lien on commercial paper with workItem No. "+getWorkItemNumber(ifr)+" has been activated";

        else if (getCpDecision(ifr).equalsIgnoreCase(decReject))
            message = "A request to "+getCpLienType(ifr)+" a lien on commercial paper with workItem No. "+getWorkItemNumber(ifr)+" has been rejected";

        new MailSetup(ifr,getWorkItemNumber(ifr),getUsersMailsInGroup(ifr,groupName),empty,mailSubject,message);
    }

    @Override
    public void cpFormLoadActivity(IFormReference ifr) {
        hideCpSections(ifr);
        hideShowLandingMessageLabel(ifr,False);
        hideShowBackToDashboard(ifr,False);
        clearFields(ifr,new String[]{cpRemarksLocal,cpDecisionLocal});
        setVisible(ifr,cpMarketSection);
        if (getPrevWs(ifr).equalsIgnoreCase(branchVerifier)){
            if (getCpMandateType(ifr).equalsIgnoreCase(cpMandateTypeLien)){
                setMandatory(ifr,new String[]{cpDecisionLocal,cpRemarksLocal});
                enableFields(ifr,new String[]{cpDecisionLocal,cpRemarksLocal});
                setVisible(ifr,new String[]{cpLienSection,cpDecisionSection});
            }
        }
        cpSetDecision(ifr);
    }

    @Override
    public void cpSetDecision(IFormReference ifr) {
        setDecision(ifr,cpDecisionLocal,new String[]{decApprove,decReject});
    }

    private void cpProcessLien(IFormReference ifr){
        if (getCpLienType(ifr).equalsIgnoreCase(cpLienTypeRemove)){
            new DbConnect(ifr, Query.getCpLienProcessQuery(getCpLienMandateId(ifr),getCpMarket(ifr),cpLienRemoveFlag)).saveQuery();
        }
        else if (getCpLienType(ifr).equalsIgnoreCase(cpLienTypeSet)){
            new DbConnect(ifr, Query.getCpLienProcessQuery(getCpLienMandateId(ifr),getCpMarket(ifr),cpLienSetFlag)).saveQuery();
        }
    }
}
