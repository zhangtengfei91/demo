package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MissiveReceiveRender;
import cn.edu.shou.missive.domain.missiveDataForm.MissiveVersionFrom;
import cn.edu.shou.missive.domain.missiveDataForm.UserFrom;
import cn.edu.shou.missive.service.*;
import jodd.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TISSOT on 2014/7/31.
 */
@RequestMapping(value="")
@SessionAttributes(value = {"userbase64","user"})
@Controller
public class templatesControl {
    @Autowired
    private MissiveFieldRepository mfr;
    @Autowired
    private TaskNameRepository tnr;
    @Autowired
    private ProcessTypeRepository ptr;
    @Autowired
    private ActivitiService actService;
    @Autowired
    private MissiveRecSeeCardRepository misssCardR;

    MissivePublishFunction mpf=new MissivePublishFunction();

    @RequestMapping(value="/{processDeID:[A-Za-z]*:[0-9]*:[0-9]*}/{instanceid:[0-9]*}/{taskid:[0-9]*}")
    public String newIn2ReceiptId(@PathVariable String processDeID,@PathVariable int instanceid,@PathVariable int taskid,Model model,@AuthenticationPrincipal User currentUser)
    {
        if(processDeID.contains("Receipt")) {
            UserFrom userFrom=mpf.userToUserForm(currentUser);
            model.addAttribute("user", userFrom);
            model.addAttribute("processDeId", processDeID);
            model.addAttribute("instanceId", instanceid);
            model.addAttribute("taskId", taskid);
            String name = actService.getCurrentTask(taskid).getName();

            ProcessType pt = ptr.findByName("收文流程");
            MetroCenTaskName tn = tnr.findByName(name, 0);
            List<MissiveField> lmf = mfr.getEditControl(tn);
            List controls = new ArrayList();
            for (MissiveField mf : lmf) {
                controls.add(mf.getInputId());
            }
            MissiveRecSeeCard missCard = misssCardR.getMissData(String.valueOf(instanceid));
            List<String> missiveCodes = misssCardR.getMissiveCode(String.valueOf(instanceid));
            model.addAttribute("exsistCodes",missiveCodes);

            MissiveReceiveFunction missF = new MissiveReceiveFunction();
           // MissiveReceiveRender missRender = missF.MissiveReceive2MissiveReceiveRender(missCard);

            model.addAttribute("editControl", controls);

            MissiveReceiveRender missiveReceiveForm=missF.MissiveReceive2MissiveReceiveRender(missCard,name);

            int maxMV=0;
            if(missiveReceiveForm.getMissiveInfo()!=null&&missiveReceiveForm.getMissiveInfo().versions!=null){
                List<MissiveVersionFrom> mvfs=missiveReceiveForm.getMissiveInfo().versions;
                for(int i=0;i<mvfs.size()-1;i++){
                    if(mvfs.get(i).versionNumber>mvfs.get(i+1).versionNumber){
                        maxMV=i;
                    }
                    else maxMV=i+1;
                }
                List<MissiveVersionFrom> mvform=new ArrayList<MissiveVersionFrom>();
                mvform.add(mvfs.get(maxMV));
                missiveReceiveForm.getMissiveInfo().setVersions(mvform);
            }
            model.addAttribute("missiveReceiveForm", missiveReceiveForm);

            return "ReceiptId";
        }

        else if(processDeID.contains("Cable")){
            return "";
        }
        else{
            return "";
        }
    }
    @RequestMapping(value="/xjlc")
    public String html2xjlc(Model model,@AuthenticationPrincipal User currentUser)
    {
        //UserFrom userFrom=mpf.userToUserForm(currentUser);
        model.addAttribute("username", currentUser.userName);
        return "newInstance";
    }
    @RequestMapping(value="/dbzx")
    public String html2dbzx(Model model,@AuthenticationPrincipal User currentUser){
        //UserFrom userFrom=mpf.userToUserForm(currentUser);
        model.addAttribute("username", currentUser.userName);
        return "dbrw";
    }

    @RequestMapping(value="/test")
    public String html2test(){
        return "missivePublish";
    }
    @RequestMapping(value="testFax")
    public String html2fax()
    {
        return "faxCablePublish";
    }
    @RequestMapping(value="/upload/{file}")
    public String downFile(@PathVariable String file){
        return "upload/"+file;
    }
    @RequestMapping(value="/html2pdf/missiveReceive/{instanceId}",method = RequestMethod.GET)
    public String html2Pdf(@PathVariable String instanceId,Model model){

        MissiveRecSeeCard missCard = misssCardR.getMissData(instanceId);
        MissiveReceiveFunction missF =new MissiveReceiveFunction();
        MissiveReceiveRender missRender = missF.MissiveReceive2MissiveReceiveRender(missCard,"");
        model.addAttribute("missiveReceiveForm",missRender);
        model.addAttribute("hasBgPng",(missCard.getBgPngPath()!=null && !missCard.getBgPngPath().equals("")));
        model.addAttribute("instanceid",instanceId);
        return "MissiveReceivePDF";
    }
    @RequestMapping(value="/schedule")
    public String html2Schedule(Model model,@AuthenticationPrincipal User currentUser){
        //UserFrom userFrom=mpf.userToUserForm(currentUser);
        model.addAttribute("username", currentUser.userName);
        return "newSchedule";
    }
    @RequestMapping(value="/tzgg")
    public String html2Notication(Model model,@AuthenticationPrincipal User currentUser){
        //UserFrom userFrom=mpf.userToUserForm(currentUser);
        model.addAttribute("username", currentUser.userName);
        return "notification";
    }
    @RequestMapping(value="indexOfDb")
    public String html2indexOfDb(Model model,@AuthenticationPrincipal User currentUser){
        //UserFrom userFrom=mpf.userToUserForm(currentUser);
        model.addAttribute("username", currentUser.userName);
        return "dbzxOfIndex";
    }
    @ModelAttribute(value = "userbase64")
    public String addUserBase64toPage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object temp = ((auth != null) ? auth.getPrincipal() :  null);
        if (temp!=null && temp.getClass()== org.springframework.security.core.userdetails.User.class )
        {
            User myUser = (User) temp;
            String base64Code = "Basic " + Base64.encodeToString(myUser.getUsername() + ":" + myUser.getUsername());
            return base64Code;
        }
        else
            return "";

    }

    @ModelAttribute(value = "user")
    public User addUsertoPage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object temp = ((auth != null) ? auth.getPrincipal() :  null);
        if (temp!=null && temp.getClass()==User.class )
        {
            return (User)temp;
        }
        else
            return null;

    }




}
