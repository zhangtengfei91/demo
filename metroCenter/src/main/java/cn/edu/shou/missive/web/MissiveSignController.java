package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MissiveSignForm;
import cn.edu.shou.missive.domain.missiveDataForm.MissiveVersionFrom;
import cn.edu.shou.missive.domain.missiveDataForm.UserFrom;
import cn.edu.shou.missive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by sqhe18 on 14-9-3.
 */
@Controller
@RequestMapping(value = "/missiveSign")
@SessionAttributes(value = {"userbase64", "user"})
public class MissiveSignController {
    MissivePublishFunction mpf=new MissivePublishFunction();
    //CommonFunction cmmF=new CommonFunction();
    @Value("${spring.main.uploaddir}")
    String fileUploadDir;
    @Value("${server.port}")
    String hostport;
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private MissiveSignRespository msr;
    @Autowired
    private UserRepository ur;
    @Autowired
    private GroupRepository gr;
    @Autowired
    private MissiveRepository mr;
    @Autowired
    private CommentContentRepository ccr;
    @Autowired
    private MissiveVersionRepository mvr;
    @Autowired
    private AttachmentRepository attachr;
    @Autowired
    private SignTypeRespository str;
    @Autowired
    private UrgentLevelRepository ulr;
    @Autowired
    private ActivitiService actService;
    @Autowired
    cn.edu.shou.missive.web.CommonFunction cmmF;

    @RequestMapping(value="/SignMissive/{intanceid}/{taskid}", method= RequestMethod.GET)
    public String index(@PathVariable int intanceid,@PathVariable int taskid,Model model,@AuthenticationPrincipal User currentUser){
        model.addAttribute("instanceID",intanceid);
        model.addAttribute("taskID",taskid);
        model.addAttribute("currentEditableField",taskDAO.getCurrentEditableFieldsByTaskId(taskid,3));
        model.addAttribute("currentTaskName",taskDAO.getCurrentTaskName(taskid));

        List<Map<String,? extends Object>> activitiNextStepInfo =this.actService.getNextTaskInfo(String.valueOf(intanceid),taskid);
        model.addAttribute("activitiNextStepInfo",activitiNextStepInfo);

        MissiveSign missiveSign=msr.findByProcessID(intanceid);//------------->missivePublish add
        MissiveSignForm missiveSignForm =mpf.missiveSignTomissiveSignForm(missiveSign);

        int maxMV=0;
        if(missiveSignForm.missiveInfo!=null&&missiveSignForm.missiveInfo.versions!=null){
            List<MissiveVersionFrom> mvfs=missiveSignForm.missiveInfo.versions;
            for(int i=0;i<mvfs.size()-1;i++){
                if(mvfs.get(i).versionNumber>mvfs.get(i+1).versionNumber){
                    maxMV=i;
                }
                else maxMV=i+1;
            }
            List<MissiveVersionFrom> mvform=new ArrayList<MissiveVersionFrom>();
            mvform.add(mvfs.get(maxMV));
            missiveSignForm.missiveInfo.versions=mvform;
        }

        model.addAttribute("missiveSignForm", missiveSignForm);

        UserFrom userFrom=mpf.userToUserForm(currentUser);
        model.addAttribute("currentUser", userFrom);
        model.addAttribute("user", currentUser);
        model.addAttribute("hasBgPng",(missiveSign.getBackgroudImage()!=null && !missiveSign.getBackgroudImage().equals("")));

//        return "missiveSign";
        String assigeeUserName=taskDAO.getTaskAssigeeUserName(intanceid,taskid);
        if(assigeeUserName!=null&&assigeeUserName.equals(currentUser.getUserName())){
            return "missiveSign";
        }
        else {
            return "index";
        }
    }


    @RequestMapping(value="/setMissiveSign", method= RequestMethod.GET)
    public String getsetMissivePublish(){
        return "index";
    }
    @RequestMapping(value="/setMissiveSign", method= RequestMethod.POST,produces = "text/html;charset=UTF-8")
//    @ResponseBody
    public String setMissivePublish(
//            @Param("UserName") String UserName               //用戶名

            @Param("instanceID") Long instanceID             //實例ID
            ,@Param("taskID") Long taskID                   //任務ID
            //----------寫入公文信息
            ,@Param("missiveNum") String missiveNum         //公文號  賦值給公文Missive的missiveNum

            ,@Param("urgentLevel") Long urgentLevel         //公文密級    根據密級id查詢出密級賦值給公文Missive的secretLevel
            ,@Param("signType") Long signType           //公文類型ID    根據類型ID查出類型賦值給公文Missive的 missiveType
            //versions
            ,@Param("docFilePath") String docFilePath       // 賦值給公文的版本versions   最後公文賦值給公文發佈表單
            ,@Param("pdfFilePath") String pdfFilePath
            ,@Param("missiveTittle") String missiveTittle
            ,@Param("name") String name   //公文名

            ,@Param("dep_LeaderCheck") String dep_LeaderCheck  //处(室)领导核稿
            ,@Param("dep_LeaderCheck_img") String dep_LeaderCheck_img
            ,@Param("dep_LeaderCheck_Base30") String dep_LeaderCheck_Base30
            ,@Param("dep_LeaderCheck_Content") String dep_LeaderCheck_Content

            ,@Param("officeCheck") String officeCheck  //办公室复核
            ,@Param("dep_LeaderCheck_img") String officeCheck_img
            ,@Param("dep_LeaderCheck_Base30") String officeCheck_Base30
            ,@Param("dep_LeaderCheck_Content") String officeCheck_Content

            ,@Param("signIssueUser") String signIssueUserName   //签发人员  最终根据查出List<User>赋值给missivePublish
            ,@Param("signIssueBase30url") String signIssueBase30url
            ,@Param("signIssueImageurl") String signIssueImageurl
            ,@Param("signIssue_Content") String signIssue_Content     //Add to CommentContent then to signIssueContent

            ,@Param("counterSignUsers") String counterSignUsersName   //会签人员  最终根据查出List<User>赋值给missivePublish
            ,@Param("counterSignBase30url") String counterSignBase30url
            ,@Param("counterSignImageurl") String counterSignImageurl
            ,@Param("counterSign_Content") String counterSign_Content     //Add to CommentContent then to counterSign_Content


            ,@Param("drafter") String drafter    //拟稿人


            ,@Param("appendName") String appendName  ////附件内容

            ,@Param("activitiVariables") String activitiVariables//
    ){
//1. get userID by username------>userID//第一次创建
        Date date=new Date();
        String realPath = fileUploadDir+"/missiveSign/";
//        int realPathLen=58;//路径字符数量
//        realPath=realPath.length()>realPathLen?realPath.substring(0,realPathLen):realPath;//如果字符长度大于58个字符就截取
        String newFolder="";//新版本路径
        String oldFolder="";
        MissiveSign tempMissiveSign;
        MissiveSign currentTempMissiveSign=new MissiveSign();//用于判断是否第一次创建表单
        Missive tempMissive;//<---------------------------------公文
        MissiveVersion tempMissiveVersion;
        CommentContent tempdep_LeaderCheckCommentContent;  //部门领导意见内容
        CommentContent tempofficeCheck_CommentContent;//办公室意见内容
        CommentContent tempSignIssueCommentContent;  //签发内容
        CommentContent tempCounterSignCommentContent;//会签内容

        List<String>  attachmentFilePathList=new ArrayList<String>();
        long fileVersionNum = 0;//默认文件版本为0+1
        currentTempMissiveSign=msr.findByProcessID(instanceID);


        if(currentTempMissiveSign!=null){   //如果MissiveSign表存在，则不创建表    如果不存在说明是第一次创建
            tempMissiveSign=currentTempMissiveSign;//则不创建表
            tempMissive=tempMissiveSign.getMissiveInfo();//Missive
            if(tempMissive==null)  tempMissive=new Missive();

            tempdep_LeaderCheckCommentContent = tempMissiveSign.getDep_LeaderCheckContent();  //
            if(tempdep_LeaderCheckCommentContent==null)  tempdep_LeaderCheckCommentContent=new CommentContent();
            tempofficeCheck_CommentContent = tempMissiveSign.getOfficeCheckContent();//
            if(tempofficeCheck_CommentContent==null) tempofficeCheck_CommentContent=new CommentContent();
            tempSignIssueCommentContent = tempMissiveSign.getSignIssueContent();  //获取签发内容 之后修改内容
            if(tempSignIssueCommentContent==null)  tempSignIssueCommentContent=new CommentContent();
            tempCounterSignCommentContent = tempMissiveSign.getCounterSignContent();//获取会签内容 之后修改内容
            if(tempCounterSignCommentContent==null) tempCounterSignCommentContent=new CommentContent();
            List<String> currentEditableField=taskDAO.getCurrentEditableFieldsByTaskId(Integer.parseInt(taskID.toString()),3);

            if(currentEditableField.contains("missiveTittle")||
                    currentEditableField.contains("attachments")){        //以下内容只有在任务回到拟稿才会创建新的公文版本
                //<---------------------------------公文

//                User tempUser=ur.findByUserName(UserName);
//                tempMissive.setMissiveCreateUser(tempUser);//missive增加creatUser
//                tempMissive.setName(name);
//                mr.save(tempMissive);//-------------------save

                tempMissiveVersion=new MissiveVersion();//创建新的公文版本
                tempMissiveVersion.setDocFilePath(docFilePath);//公文doc路径
                tempMissiveVersion.setPdfFilePath(pdfFilePath);//公文pdf
                tempMissiveVersion.setMissiveTittle(missiveTittle);//公文标题


                //点击提交按钮后，首先判断新版本下文件夹是否存在，如果存在则将就版本的文件拷贝到新版本文件夹 否则将整个文件夹拷贝至新版本
                boolean folderExit= FileOperate.exitFolder(newFolder);//判断新版本路径是否存在
//            MissivePublish missivePublish=mpr.findByProcessID(instanceID);//获取最大版本号
//                fileVersionNum=cmmF.getMaxMissiveVersion(instanceID,"missiveSign");
//                MissiveSign =msr.findByProcessID(instanceID);//获取最大版本号
        if(tempMissiveSign!=null){
            if(tempMissiveSign.getMissiveInfo()!=null){
                if(tempMissiveSign.getMissiveInfo().getVersions()!=null){
                    List<MissiveVersion> missiveVersions = tempMissiveSign.getMissiveInfo().getVersions();
                    if(tempMissiveSign.getMissiveInfo().getVersions().size()!=0){
                        List<Long> mvnum=new ArrayList<Long>();
                        for(MissiveVersion mv:missiveVersions){
                            mvnum.add(mv.getVersionNumber());
                        }
                        fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                    }
                }
            }
        }
                oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";
                fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
                newFolder=realPath+instanceID+"/"+fileVersionNum+"/";//新版本路径


                tempMissiveVersion.setVersionNumber(fileVersionNum);//公文版本号
                tempMissiveVersion.setMissive(tempMissive);
                mvr.save(tempMissiveVersion);//存入数据库-----------
                // ------------------------//<-------------附件
                if (fileVersionNum!=1){
                    folderExit= FileOperate.exitFolder(newFolder);//判断新版本路径是否存在
                    if (!folderExit) {
                        //如果存在，不对文件夹进行操作
                        //不存在，将就版本整个文件夹拷贝至新版本路径下
                        FileOperate.newFolder(newFolder);
                        FileOperate.copyFolder(oldFolder, newFolder);
                    }
                }
                else {
//               do nothing
                    if (!folderExit) {
                        //如果存在，不对文件夹进行操作
                        //不存在，将就版本整个文件夹拷贝至新版本路径下
                        FileOperate.newFolder(newFolder);
//                        FileOperate.copyFolder(oldFolder,newFolder);
                    }
                }
                String[] tempappendNames=appendName.split("\\|");
                if(tempappendNames.length!=0){
                    for(int i=0;i<tempappendNames.length;i++){
                        if(tempappendNames[i]!=""){
                            Attachment tempAttachment=new Attachment();
                            tempAttachment.setAttachmentTittle(tempappendNames[i]);
                            tempAttachment.setAttachmentFilePath(newFolder+tempappendNames[i]);
                            tempAttachment.setMissiveVersion(tempMissiveVersion);
                            attachr.save(tempAttachment);//存入数据库-------------
                            attachmentFilePathList.add(tempAttachment.getAttachmentFilePath());//生成附件列表的路径为数组-----给邓豪接口用

                        }
                    }
                }


            }
            else{
//                不创建新的公文版本
                int maxMV=0;
//                if(tempMissivePublish.getMissiveInfo()!=null&&tempMissivePublish.getMissiveInfo().getVersions()!=null) {
                List<MissiveVersion> mvs = tempMissiveSign.getMissiveInfo().getVersions();
                for (int i = 0; i < mvs.size() - 1; i++) {
                    if (mvs.get(i).getVersionNumber() > mvs.get(i + 1).getVersionNumber()) {
                        maxMV = i;
                    } else maxMV = i + 1;
                }

                tempMissiveVersion = mvs.get(maxMV);
                fileVersionNum=tempMissiveVersion.getVersionNumber();
//                }
            }

        }
        else{
            tempMissiveSign=new MissiveSign();
            tempMissive=new Missive();
            tempdep_LeaderCheckCommentContent = new CommentContent();
            tempofficeCheck_CommentContent = new CommentContent();
            tempSignIssueCommentContent = new CommentContent();  //会签内容
            tempCounterSignCommentContent=new CommentContent();//会签内容




            //<---------------------------------公文
            User tempUser=ur.findByUserName(drafter);
            tempMissive.setMissiveCreateUser(tempUser);//missive增加creatUser
            tempMissive.setName(name);
//            tempMissive.setCreatedBy(tempUser);
//            tempMissive.setLastModifiedBy(tempUser);
            mr.save(tempMissive);//-------------------save

            tempMissiveVersion=new MissiveVersion();
            tempMissiveVersion.setDocFilePath(docFilePath);//公文doc路径
            tempMissiveVersion.setPdfFilePath(pdfFilePath);//公文pdf
            tempMissiveVersion.setMissiveTittle(missiveTittle);//公文标题
            tempMissiveVersion.setVersionNumber(1);//第一创建默认为1 公文版本号
            tempMissiveVersion.setMissive(tempMissive);
            mvr.save(tempMissiveVersion);//存入数据库-----------


            //点击提交按钮后，首先判断新版本下文件夹是否存在，如果存在则将就版本的文件拷贝到新版本文件夹 否则将整个文件夹拷贝至新版本
            boolean folderExit= FileOperate.exitFolder(newFolder);//判断新版本路径是否存在

//            MissivePublish missivePublish=mpr.findByProcessID(instanceID);//获取最大版本号
            if(tempMissiveSign!=null){
                if(tempMissiveSign.getMissiveInfo()!=null){
                    if(tempMissiveSign.getMissiveInfo().getVersions()!=null){
                        List<MissiveVersion> missiveVersions = tempMissiveSign.getMissiveInfo().getVersions();
                        if(tempMissiveSign.getMissiveInfo().getVersions().size()!=0){
                            List<Long> mvnum=new ArrayList<Long>();
                            for(MissiveVersion mv:missiveVersions){
                                mvnum.add(mv.getVersionNumber());
                            }
                            fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                        }
                    }
                }
            }
            oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";
            fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
            newFolder=realPath+instanceID+"/"+fileVersionNum+"/";//新版本路径

            if (fileVersionNum!=1){
                folderExit= FileOperate.exitFolder(newFolder);//判断新版本路径是否存在
                if (!folderExit) {
                    //如果存在，不对文件夹进行操作
                    //不存在，将就版本整个文件夹拷贝至新版本路径下
                    FileOperate.newFolder(newFolder);
                    FileOperate.copyFolder(oldFolder, newFolder);
                }
            }
            else {
//               do nothing
                if (!folderExit) {
                    //如果存在，不对文件夹进行操作
                    //不存在，将就版本整个文件夹拷贝至新版本路径下
                    FileOperate.newFolder(newFolder);
//                        FileOperate.copyFolder(oldFolder,newFolder);
                }
            }

            //<-------------附件

            List<Attachment> tempListAttachment=new ArrayList<Attachment>();//附件List
//            String[] tempappendPaths=appendPath.split("\\|");
            String[] tempappendNames=appendName.split("\\|");
            if(tempappendNames.length!=0){
                for(int i=0;i<tempappendNames.length;i++){
                    if(tempappendNames[i]!=""){
                        Attachment tempAttachment=new Attachment();
                        tempAttachment.setAttachmentTittle(tempappendNames[i]);
                        tempAttachment.setAttachmentFilePath(newFolder+tempappendNames[i]);
                        tempAttachment.setMissiveVersion(tempMissiveVersion);
                        attachr.save(tempAttachment);//存入数据库-------------
                        tempListAttachment.add(tempAttachment);
                        attachmentFilePathList.add(tempAttachment.getAttachmentFilePath());//生成附件列表的路径为数组-----给邓豪接口用
                    }
                }
            }
        }

        UrgentLevel tempUrgentLevel=new UrgentLevel();
        if(urgentLevel!=null){
            tempUrgentLevel=ulr.findOne(urgentLevel);
        }
        else tempUrgentLevel=null;

        tempMissive.setUrgentLevel(tempUrgentLevel);//missive增加UrgentLevel
        if(missiveNum!="")
            tempMissive.setMissiveNum(missiveNum);//missive增加MissiveNum
        mr.save(tempMissive);//-------------------save

        //  填写发文表单----
        tempMissiveSign.setProcessID(instanceID);//实例名
        tempMissiveSign.setTaskID(taskID);//任务名

        SignType tempSignType= new SignType();// 、紧急程度 设置---------
        if(signType!=null){
            tempSignType=str.findOne(signType);
        }
        else tempSignType=null;
        tempMissiveSign.setSignType(tempSignType);//missiveSign增加SignType

        User tempDrafterUser=new User();//拟稿人
        tempDrafterUser=ur.findByUserName(drafter);
        tempMissiveSign.setDrafterUser(tempDrafterUser);



        User tempDep_LeaderCheckUser=new User();//处(室)领导核稿ren
        tempDep_LeaderCheckUser=ur.findByUserName(dep_LeaderCheck);
        tempMissiveSign.setDep_LeaderCheckUser(tempDep_LeaderCheckUser);

        tempdep_LeaderCheckCommentContent.setBase30url(dep_LeaderCheck_Base30);
        tempdep_LeaderCheckCommentContent.setImageurl(dep_LeaderCheck_img);
        tempdep_LeaderCheckCommentContent.setContentText(dep_LeaderCheck_Content);

        List<User> temptempDep_LeaderCheckUserList=new ArrayList<User>();
        temptempDep_LeaderCheckUserList.add(tempDep_LeaderCheckUser);
        tempdep_LeaderCheckCommentContent.setContentUsers(temptempDep_LeaderCheckUserList);
        tempdep_LeaderCheckCommentContent.setEnabled(true);
//        ccr.save(tempdep_LeaderCheckCommentContent);//---------save  SignIssueCommentContent



        User tempOfficeCheckUser=new User();//办公室复核
        tempOfficeCheckUser=ur.findByUserName(officeCheck);
        tempMissiveSign.setOfficeCheckUser(tempOfficeCheckUser);

        tempofficeCheck_CommentContent.setBase30url(officeCheck_Base30);
        tempofficeCheck_CommentContent.setImageurl(officeCheck_img);
        tempofficeCheck_CommentContent.setContentText(officeCheck_Content);

        List<User> tempOfficeCheckUsersList=new ArrayList<User>();
        tempOfficeCheckUsersList.add(tempOfficeCheckUser);
        tempofficeCheck_CommentContent.setContentUsers(tempOfficeCheckUsersList);
        tempofficeCheck_CommentContent.setEnabled(true);
//        ccr.save(tempofficeCheck_CommentContent);//---------save  SignIssueCommentContent





        User tempSignIssueUser;//<----------签发用户
        tempSignIssueUser=ur.findByUserName(signIssueUserName);
        tempMissiveSign.setSignIssueUser(tempSignIssueUser);

//        //签发内容
        tempSignIssueCommentContent.setBase30url(signIssueBase30url);
        tempSignIssueCommentContent.setImageurl(signIssueImageurl);
        tempSignIssueCommentContent.setContentText(signIssue_Content);
        List<User> tempSignIssueUsersList=new ArrayList<User>();
        tempSignIssueUsersList.add(tempSignIssueUser);
        tempSignIssueCommentContent.setContentUsers(tempSignIssueUsersList);
        tempSignIssueCommentContent.setEnabled(true);
//        ccr.save(tempSignIssueCommentContent);//---------save  SignIssueCommentContent


        List<User> tempCounterSignUsersList=new ArrayList<User>();//会签用户s
        String[] tempCounterSignUsers=counterSignUsersName.split("\\|");
        for(int i=0;i<tempCounterSignUsers.length;i++){
            User tempCounterSignUser=ur.findByUserName(tempCounterSignUsers[i]);
            tempCounterSignUsersList.add(tempCounterSignUser);
        }
        tempMissiveSign.setCounterSignUsers(tempCounterSignUsersList);

//         //会签内容
        tempCounterSignCommentContent.setBase30url(counterSignBase30url);
        tempCounterSignCommentContent.setImageurl(counterSignImageurl);
        tempCounterSignCommentContent.setContentText(counterSign_Content);
        tempCounterSignCommentContent.setContentUsers(tempCounterSignUsersList);
        tempCounterSignCommentContent.setEnabled(true);
//        ccr.save(tempCounterSignCommentContent);//-----------------save CounterSignCommentContent





        tempMissiveSign.setDep_LeaderCheckContent(tempdep_LeaderCheckCommentContent);//missivePublish add 部门领导意见内容
        tempMissiveSign.setOfficeCheckContent(tempofficeCheck_CommentContent);//missivePublish add 办公室复核内容
        tempMissiveSign.setSignIssueContent(tempSignIssueCommentContent);//missivePublish add 签发内容
        tempMissiveSign.setCounterSignContent(tempCounterSignCommentContent);//missivePublish add 会签内容


        List<User> missiveCopytoUsers=new ArrayList<User>();//公文可见人员
        try{
            missiveCopytoUsers=tempMissiveSign.getMissiveInfo().getCopyToUsers();
            missiveCopytoUsers.clear();
        }
        catch (Exception e){
        }


        tempMissive.setCopyToUsers(missiveCopytoUsers);//missive 可见人员
        tempMissiveSign.setMissiveInfo(tempMissive);//公文信息




        tempMissiveSign.setMissiveTittle(missiveTittle);

        msr.save(tempMissiveSign);

//        tempMainSendGroups

        Map<String,Object> activitiMap = new HashMap<String, Object>();
//        List<String> userlist = new ArrayList<String>();
//        userlist.add("kermit");
//        userlist.add("sqhe18");
//        activitiMap.put("",userlist);

        if(activitiVariables!=""){
            String[] tempActivitiVariables = activitiVariables.split("\\|");
            if(tempActivitiVariables.length!=0){
                for(int i=0;i<tempActivitiVariables.length;i++){
                    String[] tempActivitiVariable=tempActivitiVariables[i].split(",");
                    if(tempActivitiVariable[3].equals("false")){
                        activitiMap.put(tempActivitiVariable[0],tempActivitiVariable[1]);
                    }
                    else{
                        String[] valueList = tempActivitiVariable[i].split(";");
                        List<String> userlist = new ArrayList<String>();
                        for(int j=0;j<valueList.length;j++){
                            userlist.add(valueList[j]);
                        }
                        activitiMap.put(tempActivitiVariable[0],userlist);
                    }
//                    List<String> userlist = valueList;
//                    for()

//
                }
            }
        }
        String currentTaskName=taskDAO.getCurrentTaskName(Integer.parseInt(taskID.toString()));
        String htmlpath="http://localhost:"+hostport+"/missiveSign/missiveSignToPDF/";
        htmlpath+=instanceID;
        String[] attachment_StringArray =new String[attachmentFilePathList.size()];
        for(int attSTR_i=0;attSTR_i<attachmentFilePathList.size();attSTR_i++){
            attachment_StringArray[attSTR_i]=attachmentFilePathList.get(attSTR_i);
        }
        //cmmF.convertAtt2Pdf2(currentTaskName,"处室拟稿",String.valueOf(tempMissiveVersion.getId()),fileVersionNum,attachment_StringArray,Long.parseLong(instanceID.toString()),Long.parseLong(taskID.toString()),htmlpath,"MissiveSign");

        //cmmF.convertAtt2Pdf(currentTaskName,"处室拟稿",tempMissiveSign.getMissiveInfo(),instanceID,taskID,htmlpath,"MissiveSign");

        this.actService.completeTask(taskID,activitiMap,"missiveSign",instanceID);

//        String pdfpath=fileUploadDir+"/"+"html2pdf/missiveSign/";
//        pdfpath+=instanceID;
//        boolean pdffolderExit= FileOperate.exitFolder(pdfpath);//判断pdfpath路径是否存在
//        if (!pdffolderExit) {
//            FileOperate.newFolder(pdfpath);
//        }
//        pdfpath=pdfpath+"/"+instanceID+".pdf";
//        String html2pdf = cmmF.shellOption(htmlpath,pdfpath);

//        return "SUCSESS!";
         return "redirect:/dbzx";
    }




    @RequestMapping(value="/missiveSignToPDF/{intanceid}", method= RequestMethod.GET)
    public String toPDF(@PathVariable int intanceid,Model model,@AuthenticationPrincipal User currentUser){
        MissiveSign missiveSign=msr.findByProcessID(intanceid);//------------->missivePublish add
        MissiveSignForm missiveSignForm =mpf.missiveSignTomissiveSignForm(missiveSign);
        model.addAttribute("instanceID",intanceid);
        int maxMV=0;
        if(missiveSignForm.missiveInfo!=null&&missiveSignForm.missiveInfo.versions!=null){
            List<MissiveVersionFrom> mvfs=missiveSignForm.missiveInfo.versions;
            for(int i=0;i<mvfs.size()-1;i++){
                if(mvfs.get(i).versionNumber>mvfs.get(i+1).versionNumber){
                    maxMV=i;
                }
                else maxMV=i+1;
            }
            List<MissiveVersionFrom> mvform=new ArrayList<MissiveVersionFrom>();
            mvform.add(mvfs.get(maxMV));
            missiveSignForm.missiveInfo.versions=mvform;
        }
        String Dep_LeaderCheckContent_text="";
        if(missiveSignForm.Dep_LeaderCheckContent!=null&&missiveSignForm.Dep_LeaderCheckContent.contentText!=null){
            Dep_LeaderCheckContent_text=missiveSignForm.Dep_LeaderCheckContent.contentText.replaceAll("<br>","\n");
            try{
                if(Dep_LeaderCheckContent_text.substring(0,1).equals("\n"))
                    Dep_LeaderCheckContent_text=" "+Dep_LeaderCheckContent_text;
            }
            catch (Exception e){}

        }
        missiveSignForm.Dep_LeaderCheckContent.contentText=Dep_LeaderCheckContent_text;

        String OfficeCheckContent_text="";
        if(missiveSignForm.OfficeCheckContent!=null&&missiveSignForm.OfficeCheckContent.contentText!=null){
            OfficeCheckContent_text=missiveSignForm.OfficeCheckContent.contentText.replaceAll("<br>","\n");
            try {
                if(OfficeCheckContent_text.substring(0,1).equals("\n"))
                    OfficeCheckContent_text=" "+OfficeCheckContent_text;
            }
            catch (Exception e){}
        }
        missiveSignForm.OfficeCheckContent.contentText=OfficeCheckContent_text;

        String signIssueContent_text="";
        if(missiveSignForm.signIssueContent!=null&&missiveSignForm.signIssueContent.contentText!=null){
            signIssueContent_text=missiveSignForm.signIssueContent.contentText.replaceAll("<br>","\n");
            try{
                if(signIssueContent_text.substring(0,1).equals("\n"))
                    signIssueContent_text=" "+signIssueContent_text;
            }
            catch (Exception e){}
        }
        missiveSignForm.signIssueContent.contentText=signIssueContent_text;

        String counterSignContent_text="";
        if(missiveSignForm.counterSignContent!=null&&missiveSignForm.counterSignContent.contentText!=null){
            counterSignContent_text=missiveSignForm.counterSignContent.contentText.replaceAll("<br>","\n");
            try{
                if(counterSignContent_text.substring(0,1).equals("\n"))
                    counterSignContent_text=" "+counterSignContent_text;
            }
            catch (Exception e){}

        }
        missiveSignForm.counterSignContent.contentText=counterSignContent_text;

        String missiveTittle_text=null;
        if(missiveSignForm.missiveTittle!=null){
            missiveTittle_text=missiveSignForm.missiveTittle.replaceAll("<br>","\n");
//            try{
//                if(missiveTittle_text.substring(0,1).equals("\n"))
//                    missiveTittle_text=" "+missiveTittle_text;
//            }
//            catch (Exception e){}
        }

        missiveSignForm.missiveTittle=missiveTittle_text;

        model.addAttribute("missiveSignForm", missiveSignForm);
        String blankImageurl="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAxEAAAFjCAYAAABCEoaFAAAWB0lEQVR4Xu3XoREAAAgDMbr/0szwPuiqHOZ3jgABAgQIECBAgAABAkFgYWtKgAABAgQIECBAgACBExGegAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSTwRdcBZA2nfHIAAAAASUVORK5CYII=";
        model.addAttribute("blankImage", blankImageurl);
        model.addAttribute("hasBgPng",(missiveSign.getBackgroudImage()!=null && !missiveSign.getBackgroudImage().equals("")));

        return "missiveSignForPDF";
    }

}
