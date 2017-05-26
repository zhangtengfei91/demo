package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MissivePublishForm;
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
 * Created by sqhe on 14-7-19.
 */
@Controller
@RequestMapping(value = "/missive")
@SessionAttributes(value = {"userbase64", "user"})
public class MissivePublishController {
    @Value("${spring.main.uploaddir}")
    String fileUploadDir;
    @Value("${server.port}")
    String hostport;
    @Autowired
    cn.edu.shou.missive.web.CommonFunction cmmF;

    MissivePublishFunction mpf=new MissivePublishFunction();
   // CommonFunction cmmF=new CommonFunction();
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private MissivePublishRepository mpDAO;

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
    private MissiveTypeRepository mtr;
    @Autowired
    private SecretLevelRespository slr;
    @Autowired
    private ActivitiService actService;

    @RequestMapping(value="/missivePublish/{intanceid}/{taskid}", method= RequestMethod.GET)
    public String index(@PathVariable int intanceid,@PathVariable int taskid,Model model,@AuthenticationPrincipal User currentUser){
        model.addAttribute("instanceID",intanceid);
        model.addAttribute("taskID",taskid);
        model.addAttribute("currentEditableField",taskDAO.getCurrentEditableFieldsByTaskId(taskid,1));
        model.addAttribute("currentTaskName",taskDAO.getCurrentTaskName(taskid));

        List<Map<String,? extends Object>> activitiNextStepInfo =new ArrayList<Map<String, ? extends Object>>();

        activitiNextStepInfo =this.actService.getNextTaskInfo(String.valueOf(intanceid),taskid);

        model.addAttribute("activitiNextStepInfo",activitiNextStepInfo);
        MissivePublish missivePublish=mpDAO.findByProcessID(intanceid);//------------->missivePublish add
        MissivePublishForm missivePublishForm=mpf.MissivePublishToMissivePublishForm(missivePublish);

        int maxMV=0;
        if(missivePublishForm.missiveInfo!=null&&missivePublishForm.missiveInfo.versions!=null){
            List<MissiveVersionFrom> mvfs=missivePublishForm.missiveInfo.versions;
            for(int i=0;i<mvfs.size()-1;i++){
                if(mvfs.get(i).versionNumber>mvfs.get(i+1).versionNumber){
                    maxMV=i;
                }
                else maxMV=i+1;
            }
            List<MissiveVersionFrom> mvform=new ArrayList<MissiveVersionFrom>();
            mvform.add(mvfs.get(maxMV));
            missivePublishForm.missiveInfo.versions=mvform;
        }

//        List<Long> mvnum=new ArrayList<Long>();
//        for(MissiveVersionFrom mvf:mvfs){
//            mvnum.add(mvf.versionNumber);
//        }
//        int maxVN= Integer.parseInt(Collections.max(mvnum).toString());

        model.addAttribute("missivePublishForm", missivePublishForm);

//        List<GroupFrom> allGroups= new ArrayList<GroupFrom>();//------------>all groups add
//        List<Group> groups=gr.findAll();
//        for(Group g:groups){
//            GroupFrom gf=mpf.groupToGroupFrom(g);
//            allGroups.add(gf);
//        }
//        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//        String json=null;
//        try{
//            json = ow.writeValueAsString(allGroups);
//        }
//        catch (Exception e){}
//
//
//        model.addAttribute("allGroups", json);
//
//        List<UserFrom> allUsers =  new ArrayList<UserFrom>();//----------->all users add
//        List<User> users=ur.findAll();
//        for (User user:users){
//            UserFrom userFrom=mpf.userToUserForm(user);
//            allUsers.add(userFrom);
//        }
//        model.addAttribute("allUsers", allUsers);

        UserFrom userFrom=mpf.userToUserForm(currentUser);
        model.addAttribute("currentUser", userFrom);
        model.addAttribute("user", currentUser);
        model.addAttribute("hasBgPng",(missivePublish.getBackgroudImage()!=null && !missivePublish.getBackgroudImage().equals("")));
        String assigeeUserName=taskDAO.getTaskAssigeeUserName(intanceid,taskid);
        if(assigeeUserName!=null&&assigeeUserName.equals(currentUser.getUserName())){
            return "missivePublish";
        }
        else {
            return "index";
        }
    }
    @RequestMapping(value="/setMissivePublish", method= RequestMethod.GET)
    public String getsetMissivePublish(){
        return "index";
    }
    @RequestMapping(value="/setMissivePublish", method= RequestMethod.POST,produces = "text/html;charset=UTF-8")
//    @ResponseBody
    public String setMissivePublish(
            @Param("UserName") String UserName               //用戶名

            ,@Param("instanceID") Long instanceID             //實例ID
            ,@Param("taskID") Long taskID                   //任務ID
            //----------寫入公文信息
            ,@Param("missiveNum") String missiveNum         //公文號  賦值給公文Missive的missiveNum
            ,@Param("secretLevel") Long secretLevel         //公文密級    根據密級id查詢出密級賦值給公文Missive的secretLevel
            ,@Param("typeName") Long typeName           //公文類型ID    根據類型ID查出類型賦值給公文Missive的 missiveType
            //versions
            ,@Param("docFilePath") String docFilePath       // 賦值給公文的版本versions   最後公文賦值給公文發佈表單
            ,@Param("pdfFilePath") String pdfFilePath
            ,@Param("missiveTittle") String missiveTittle
//            ,@Param("content") String content
            //,@Param("missiveId") String missiveId  //
            ,@Param("name") String name   //公文名

            ,@Param("signIssueUser") String signIssueUserName   //签发人员  最终根据查出List<User>赋值给missivePublish
            ,@Param("signIssueBase30url") String signIssueBase30url
            ,@Param("signIssueImageurl") String signIssueImageurl
            ,@Param("signIssue_Content") String signIssue_Content     //Add to CommentContent then to signIssueContent

            ,@Param("counterSignUsers") String counterSignUsersName   //会签人员  最终根据查出List<User>赋值给missivePublish
            ,@Param("counterSignBase30url") String counterSignBase30url
            ,@Param("counterSignImageurl") String counterSignImageurl
            ,@Param("counterSign_Content") String counterSign_Content     //Add to CommentContent then to counterSign_Content

            ,@Param("mainSendGroups") String mainSendGroups      //主送部门   根据username查出ID赋值给  //Add to CommentContent then to counterSign_Content
            ,@Param("copytoGroups") String copytoGroups      //--抄送部门
            ,@Param("sponsorUnit") String sponsorUnit    //主办单位
            ,@Param("phoneNum") String phoneNum    //电话
            ,@Param("drafter") String drafter    //拟稿人
            ,@Param("composeUser") String composeUser  //排版人
            ,@Param("dep_LeaderCheck") String dep_LeaderCheck  //处(室)领导核稿
            ,@Param("officeCheck") String officeCheck  //办公室复核
            ,@Param("printCount") int printCount  //打印份数
            ,@Param("CheckReader") String CheckReader  ////校对人
            ,@Param("Printer") String Printer  ////校对人Printer
            ,@Param("Gov_info_attr") int Gov_info_attr  ////政府信息属性
            ,@Param("appendTittle") String appendTittle  ////附件标题
            ,@Param("appendName") String appendName  ////附件内容
            ,@Param("appendPath") String appendPath  ////附件内容
            ,@Param("activitiVariables") String activitiVariables//
            ,@Param("mainSend") String mainSend//主送外单位 郑小罗 2014/12/25
            ,@Param("copyTo") String copyTo//主送外单位 郑小罗 2014/12/25
    ){
//1. get userID by username------>userID//第一次创建
        Date date=new Date();
        String realPath = fileUploadDir+"/missivePublish/";
//        int realPathLen=58;//路径字符数量
//        realPath=realPath.length()>realPathLen?realPath.substring(0,realPathLen):realPath;//如果字符长度大于58个字符就截取
        String newFolder="";//新版本路径
        String oldFolder="";
        MissivePublish tempMissivePublish;
        MissivePublish currentTempMissivePublish=new MissivePublish();//用于判断是否第一次创建公文表
        Missive tempMissive;//<---------------------------------公文
        MissiveVersion tempMissiveVersion;
        CommentContent tempSignIssueCommentContent;  //签发内容
        CommentContent tempCounterSignCommentContent;//会签内容
//        String[] attachmentFilePathList=null;//附件路径列表
        List<String>  attachmentFilePathList=new ArrayList<String>();
        long fileVersionNum = 0;//默认文件版本为0+1
        currentTempMissivePublish=mpDAO.findByProcessID(instanceID);

        if(currentTempMissivePublish!=null){   //如果MissivePublish表存在，则不创建表    如果不存在说明是第一次创建
            tempMissivePublish=currentTempMissivePublish;//则不创建表
            tempMissive=tempMissivePublish.getMissiveInfo();//Missive
            if(tempMissive==null)  tempMissive=new Missive();
            tempSignIssueCommentContent = tempMissivePublish.getSignIssueContent();  //获取签发内容 之后修改内容
            if(tempSignIssueCommentContent==null)  tempSignIssueCommentContent=new CommentContent();
            tempCounterSignCommentContent = tempMissivePublish.getCounterSignContent();//获取会签内容 之后修改内容
            if(tempCounterSignCommentContent==null) tempCounterSignCommentContent=new CommentContent();
            List<String> currentEditableField=taskDAO.getCurrentEditableFieldsByTaskId(Integer.parseInt(taskID.toString()),1);
            if(currentEditableField.contains("missiveTittle")||
                    currentEditableField.contains("appendTittle")||
                    currentEditableField.contains("initiativePublic")||
                    currentEditableField.contains("applyForPublic")||
                    currentEditableField.contains("forbiddenPublic")||
                    currentEditableField.contains("printCount")||
                    currentEditableField.contains("copyTo_Person")||
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
                if(tempMissivePublish!=null){
                    if(tempMissivePublish.getMissiveInfo()!=null){
                        if(tempMissivePublish.getMissiveInfo().getVersions()!=null){
                            List<MissiveVersion> missiveVersions = tempMissivePublish.getMissiveInfo().getVersions();
                            if(tempMissivePublish.getMissiveInfo().getVersions().size()!=0){
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

//
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
//                            attachmentFilePathList[i]=tempAttachment.getAttachmentFilePath();//生成附件列表的路径为数组-----给邓豪接口用
                            attachmentFilePathList.add(tempAttachment.getAttachmentFilePath());//生成附件列表的路径为数组-----给邓豪接口用
                        }
                    }
                }


            }
            else{
                int maxMV=0;
//                if(tempMissivePublish.getMissiveInfo()!=null&&tempMissivePublish.getMissiveInfo().getVersions()!=null) {
                    List<MissiveVersion> mvs = tempMissivePublish.getMissiveInfo().getVersions();
                    for (int i = 0; i < mvs.size() - 1; i++) {
                        if (mvs.get(i).getVersionNumber() > mvs.get(i + 1).getVersionNumber()) {
                            maxMV = i;
                        } else maxMV = i + 1;
                    }
                    tempMissiveVersion = mvs.get(maxMV);
//                fileVersionNum=Long.parseLong(String.valueOf(tempMissiveVersion.getVersionNumber()));
                fileVersionNum=tempMissiveVersion.getVersionNumber();
//                }
//                不创建新的公文版本
            }

        }
        else{
            tempMissivePublish=new MissivePublish();
            tempMissive=new Missive();
            tempSignIssueCommentContent = new CommentContent();  //会签内容
            tempCounterSignCommentContent=new CommentContent();//会签内容



            //<---------------------------------公文
            User tempUser=ur.findByUserName(UserName);
//            tempMissive.setMissiveCreateUser(tempUser);//missive增加creatUser
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
            if(tempMissivePublish!=null){
                if(tempMissivePublish.getMissiveInfo()!=null){
                    if(tempMissivePublish.getMissiveInfo().getVersions()!=null){
                        List<MissiveVersion> missiveVersions = tempMissivePublish.getMissiveInfo().getVersions();
                        if(tempMissivePublish.getMissiveInfo().getVersions().size()!=0){
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
//                        attachmentFilePathList[i]=tempAttachment.getAttachmentFilePath();//生成附件列表的路径为数组-----给邓豪接口用
                        attachmentFilePathList.add(tempAttachment.getAttachmentFilePath());//生成附件列表的路径为数组-----给邓豪接口用
                    }
                }
            }
        }

        MissiveType tempMissiveType=mtr.findOne(typeName);// 公文类型、公文密级、公文号 设置---------
        tempMissive.setMissiveType(tempMissiveType);//missive增加MissiveType

        SecretLevel tempSecretLevel=new SecretLevel();
        if(secretLevel!=null){
            tempSecretLevel=slr.findOne(secretLevel);
        }
        else tempSecretLevel=null;
        tempMissive.setSecretLevel(tempSecretLevel);//missive增加SecretLevel
        if(missiveNum!="")
        tempMissive.setMissiveNum(missiveNum);//missive增加MissiveNum
        mr.save(tempMissive);//-------------------save

        //  填写发文表单----
        tempMissivePublish.setProcessID(instanceID);//实例名
        tempMissivePublish.setTaskID(taskID);//任务名


        User tempSignIssueUser;//<----------签发用户
        tempSignIssueUser=ur.findByUserName(signIssueUserName);
        tempMissivePublish.setSignIssueUser(tempSignIssueUser);

//        //签发内容
        tempSignIssueCommentContent.setBase30url(signIssueBase30url);
        tempSignIssueCommentContent.setImageurl(signIssueImageurl);
        tempSignIssueCommentContent.setContentText(signIssue_Content);
        List<User> tempSignIssueUsersList=new ArrayList<User>();
        tempSignIssueUsersList.add(tempSignIssueUser);
        tempSignIssueCommentContent.setContentUsers(tempSignIssueUsersList);
        tempSignIssueCommentContent.setEnabled(true);
        ccr.save(tempSignIssueCommentContent);//---------save  SignIssueCommentContent
        tempMissivePublish.setSignIssueContent(tempSignIssueCommentContent);//missivePublish add 签发内容

        List<User> tempCounterSignUsersList=new ArrayList<User>();//会签用户s
        String[] tempCounterSignUsers=counterSignUsersName.split("\\|");
        for(int i=0;i<tempCounterSignUsers.length;i++){
            User tempCounterSignUser=ur.findByUserName(tempCounterSignUsers[i]);
            tempCounterSignUsersList.add(tempCounterSignUser);
        }
        tempMissivePublish.setCounterSignUsers(tempCounterSignUsersList);

//         //会签内容
        tempCounterSignCommentContent.setBase30url(counterSignBase30url);
        tempCounterSignCommentContent.setImageurl(counterSignImageurl);
        tempCounterSignCommentContent.setContentText(counterSign_Content);
        tempCounterSignCommentContent.setContentUsers(tempCounterSignUsersList);
        tempCounterSignCommentContent.setEnabled(true);
        ccr.save(tempCounterSignCommentContent);//-----------------save CounterSignCommentContent
        tempMissivePublish.setCounterSignContent(tempCounterSignCommentContent);//missivePublish add 会签内容

        List<User> missiveCopytoUsers=new ArrayList<User>();//公文可见人员
        try{
            missiveCopytoUsers=tempMissivePublish.getMissiveInfo().getCopyToUsers();
            missiveCopytoUsers.clear();
        }
        catch (Exception e){
        }

        List<Group> tempMainSendGroupsList=new ArrayList<Group>();//----主送部门


        String[] tempMainSendGroups=mainSendGroups.split("\\|");
        for(int i=0;i<tempMainSendGroups.length;i++){
            if(tempMainSendGroups[i]!=""){
                try{
                    Group tempMainSendGroup=gr.findOne(Long.parseLong(tempMainSendGroups[i]));
                    if(tempMainSendGroup.getUsers()!=null){
                        for(User cptuser:tempMainSendGroup.getUsers()){
                            missiveCopytoUsers.add(cptuser);
                        }
                    }
                    tempMainSendGroupsList.add(tempMainSendGroup);
                }
                catch (Exception e){}

            }

        }
        tempMissivePublish.setMainSendGroups(tempMainSendGroupsList);//missivePublish add 主送部门

        List<Group> tempCopytoGroupsList=new ArrayList<Group>();//----抄送部门
        String[] tempCopytoGroups=copytoGroups.split("\\|");
        for(int i=0;i<tempCopytoGroups.length;i++){
            if(tempCopytoGroups[i]!=""){
                try {
                    Group tempCopytoGroup=gr.findOne(Long.parseLong(tempCopytoGroups[i]));
                    if(tempCopytoGroup.getUsers()!=null){
                        for(User cptuser:tempCopytoGroup.getUsers()){
                            missiveCopytoUsers.add(cptuser);
                        }
                    }
                    tempCopytoGroupsList.add(tempCopytoGroup);
                }
                catch (Exception e){}
            }

        }
        tempMissivePublish.setCopytoGroups(tempCopytoGroupsList);//missivePublish add 抄送部门
        tempMissive.setCopyToUsers(missiveCopytoUsers);//missive 可见人员
        tempMissivePublish.setMissiveInfo(tempMissive);//公文信息

        User tempDrafterUser=new User();//拟稿人
        tempDrafterUser=ur.findByUserName(drafter);
        tempMissivePublish.setDrafterUser(tempDrafterUser);

        User tempComposeUser=new User();
        tempComposeUser=ur.findByUserName(composeUser);
        tempMissivePublish.setComposeUser(tempComposeUser);//排版人

        User tempDep_LeaderCheckUser=new User();//处(室)领导核稿ren
        tempDep_LeaderCheckUser=ur.findByUserName(dep_LeaderCheck);
        tempMissivePublish.setDep_LeaderCheckUser(tempDep_LeaderCheckUser);

        User tempOfficeCheckUser=new User();//办公室复核
        tempOfficeCheckUser=ur.findByUserName(officeCheck);
        tempMissivePublish.setOfficeCheckUser(tempOfficeCheckUser);

        tempMissivePublish.setPrintCount(printCount);//打印份数

        User tempCheckReader=new User();//校对人
        tempCheckReader=ur.findByUserName(CheckReader);
        tempMissivePublish.setCheckReader(tempCheckReader);

        if(Printer!=""){
            User tempPrinter=new User();//打印
            tempPrinter=ur.findByUserName(Printer);
            tempMissivePublish.setPrinter(tempPrinter);
        }

        tempMissivePublish.setGov_info_attr(Gov_info_attr);
        tempMissivePublish.setMissiveTittle(missiveTittle);
        tempMissivePublish.setAttachmentTittle(appendTittle);
        //郑小罗 2014/12/25 添加主送、抄送外单位信息
        tempMissivePublish.setMainSend(mainSend);
        tempMissivePublish.setCopyTo(copyTo);

        mpDAO.save(tempMissivePublish);

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
        String htmlpath="http://localhost:"+hostport+"/missive/missivePublishToPDF/";
        htmlpath+=instanceID;
        String currentTaskName=taskDAO.getCurrentTaskName(Integer.parseInt(taskID.toString()));
        String[] attachment_StringArray =new String[attachmentFilePathList.size()];
        for(int attSTR_i=0;attSTR_i<attachmentFilePathList.size();attSTR_i++){
            attachment_StringArray[attSTR_i]=attachmentFilePathList.get(attSTR_i);
        }
        //cmmF.convertAtt2Pdf2(currentTaskName,"处室拟稿",String.valueOf(tempMissiveVersion.getId()),fileVersionNum,attachment_StringArray,Long.parseLong(instanceID.toString()),Long.parseLong(taskID.toString()),htmlpath,"MissivePublish");
        //cmmF.convertAtt2Pdf(currentTaskName,"处室拟稿",tempMissivePublish.getMissiveInfo(),instanceID,taskID,htmlpath,"MissivePublish");
        this.actService.completeTask(taskID,activitiMap,"missivePublish",instanceID);


//        String pdfpath=fileUploadDir+"/"+"html2pdf/missivePublish/";
//        pdfpath+=instanceID;
//        boolean pdffolderExit= FileOperate.exitFolder(pdfpath);//判断pdfpath路径是否存在
//        if (!pdffolderExit) {
//            FileOperate.newFolder(pdfpath);
//        }
//        pdfpath=pdfpath+"/"+instanceID+".pdf";
//        String html2pdf = cmmF.shellOption(htmlpath,pdfpath);






        return "taskList";

    }

    @RequestMapping(value="/missivePublishToPDF/{intanceid}", method= RequestMethod.GET)
    public String toPDF(@PathVariable int intanceid,Model model,@AuthenticationPrincipal User currentUser){

        model.addAttribute("instanceID",intanceid);
        MissivePublish missivePublish=mpDAO.findByProcessID(intanceid);//------------->missivePublish add
        MissivePublishForm missivePublishForm=mpf.MissivePublishToMissivePublishForm(missivePublish);
        int maxMV=0;
        if(missivePublishForm.missiveInfo!=null&&missivePublishForm.missiveInfo.versions!=null){
            List<MissiveVersionFrom> mvfs=missivePublishForm.missiveInfo.versions;
            for(int i=0;i<mvfs.size()-1;i++){
                if(mvfs.get(i).versionNumber>mvfs.get(i+1).versionNumber){
                    maxMV=i;
                }
                else maxMV=i+1;
            }
            List<MissiveVersionFrom> mvform=new ArrayList<MissiveVersionFrom>();
            mvform.add(mvfs.get(maxMV));
            missivePublishForm.missiveInfo.versions=mvform;
        }


        String signIssueContent_text="";
        if(missivePublishForm.signIssueContent!=null&&missivePublishForm.signIssueContent.contentText!=null){
            signIssueContent_text=missivePublishForm.signIssueContent.contentText.replaceAll("<br>","\n");
            try{
                if(signIssueContent_text.substring(0,1).equals("\n"))
                    signIssueContent_text=" "+signIssueContent_text;
            }
            catch (Exception e){}

        }
        missivePublishForm.signIssueContent.contentText=signIssueContent_text;

        String counterSignContent_text="";
        if(missivePublishForm.counterSignContent!=null&&missivePublishForm.counterSignContent.contentText!=null){
            counterSignContent_text=missivePublishForm.counterSignContent.contentText.replaceAll("<br>","\n");
            try{
                if(counterSignContent_text.substring(0,1).equals("\n"))
                    counterSignContent_text=" "+counterSignContent_text;
            }
            catch (Exception e){}
        }
        missivePublishForm.counterSignContent.contentText=counterSignContent_text;

        String missiveTittle_text=null;
        if(missivePublishForm.missiveTittle!=null){
            missiveTittle_text=missivePublishForm.missiveTittle.replaceAll("<br>","\n");
        }
        missivePublishForm.missiveTittle=missiveTittle_text;
        String attachmentTittle_text=null;
        if(missivePublishForm.attachmentTittle!=null){
            attachmentTittle_text=missivePublishForm.attachmentTittle.replaceAll("<br>","\n");
        }
        missivePublishForm.attachmentTittle=attachmentTittle_text;

        model.addAttribute("missivePublishForm", missivePublishForm);


        String mainsendGroupString=null;
        if(missivePublishForm.MainSendGroups !=null){//主送人员
            for(int i=0;i<missivePublishForm.MainSendGroups.size();i++){

                if(i==0) mainsendGroupString = missivePublishForm.MainSendGroups.get(i).groupName;
                else{
                    mainsendGroupString = mainsendGroupString+", "+missivePublishForm.MainSendGroups.get(i).groupName;
                }
            }
        }//主送内容end
        model.addAttribute("mainsendGroup", mainsendGroupString);

        String copytoGroupString=null;
        if(missivePublishForm.CopytoGroups!=null){//chao送人员
            for(int i=0;i<missivePublishForm.CopytoGroups.size();i++){
                if(i==0) copytoGroupString= missivePublishForm.CopytoGroups.get(i).groupName;
                else{
                    copytoGroupString = copytoGroupString+", "+missivePublishForm.CopytoGroups.get(i).groupName;
                }
            }

        }//chao送内容end
        model.addAttribute("copytoGroup", copytoGroupString);
        String blankImageurl="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAxEAAAFjCAYAAABCEoaFAAAWB0lEQVR4Xu3XoREAAAgDMbr/0szwPuiqHOZ3jgABAgQIECBAgAABAkFgYWtKgAABAgQIECBAgACBExGegAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSTwRdcBZA2nfHIAAAAASUVORK5CYII=";
        model.addAttribute("blankImage", blankImageurl);
        model.addAttribute("hasBgPng",(missivePublish.getBackgroudImage()!=null && !missivePublish.getBackgroudImage().equals("")));
        return "missivePublishForPDF";
    }

}