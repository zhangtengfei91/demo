package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.*;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by seky on 14-8-1.
 */
@RestController
@RequestMapping(value = "api/UpdateData")
@SessionAttributes(value = {"userbase64","user"})
public class UpdateFaxCableDataContoller {
    @Value("${spring.main.uploaddir}")
    String fileUploadDir;
    @Value("${spring.main.htmlUrlBaseFax}")
    String htmlUrlBaseFax;
    String fileType="/faxCablePublish/";
    String realPath ="";
    int realPathLen=1158;//路径字符数量
    static Object[]  objData;//用来接收对象
    String execOfficeExePath="C:\\Users\\XQQ\\Desktop\\word2pdf816.exe";//执行exe路径
    faxCableReciveClass faxClass=new faxCableReciveClass();
    //CommonFunction commF=new CommonFunction();
    @Autowired
    UserRepository usrR;//用户
    @Autowired
    SecretLevelRepository secR;//密级
    @Autowired
    MissiveTypeRepository mistR;//公文类型
    @Autowired
    MissiveRepository missR;//传真电报
    @Autowired
    CommentContentRepository commR;//签发内容
    @Autowired
    GroupRepository groupR;//用户组
    @Autowired
    FaxCableRepository faxPubR;//传真电报
    @Autowired
    MissiveVersionRepository missVR;//传真电报版本
    @Autowired
    AttachmentRepository attR;//附件信息
    @Autowired
    ActivitiController actR;//流程服务
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private  MissiveRepository misR;
    @Autowired
    cn.edu.shou.missive.web.CommonFunction commF;
    @Autowired
    private ActivitiService actS;



    //获取创建用户信息 根据创建用户ID
    private User getCreateUser(String userID){
        if (userID!=null && !userID.equals("")) {
            Long ID = Long.parseLong(userID);//首先将String转换成long类型
            User usr = usrR.findOne(ID);//获取用户实体类
            return usr;
        }else {
            return null;
        }
    }
    //获取公文密级 根据ID
    private SecretLevel getSecretLvByID(String secretID){
        if (secretID!=null && !secretID.equals("")) {
            Long ID = Long.parseLong(secretID);
            SecretLevel secret = secR.findOne(ID);//获取密级实体类
            return secret;
        }else {
            return null;
        }
    }
    //获取公文类型 根据ID
    private MissiveType getMissiveType(String typeID){
        if (typeID!=null && !typeID.equals("")) {
            Long ID = Long.parseLong(typeID);
            MissiveType missType = mistR.findOne(ID);
            return missType;
        }else {
            return null;
        }
    }
    //获取公文信息 根据ID
    private Missive getMissive(String missID){
        if (missID!=null && !missID.equals("")) {
            Long ID = Long.parseLong(missID);
            Missive mis = missR.findOne(ID);
            return mis;
        }else {
            return null;
        }
    }
    //获取签发人员 根据签发人员ID
    private List<User>getSignIssueUsers(String userID){
        if (userID!=null && !userID.equals("")) {
            Long ID = Long.parseLong(userID);
            List<User> usrList = usrR.getUserInfoByID(ID);
            return usrList;
        }else {
            return null;
        }
    }
    //获取签发内容，在获取前需要将数据先插入
    private CommentContent getSignIssueContent(String commentID){
        if (commentID!=null && !commentID.equals("")) {
            Long ID = Long.parseLong(commentID);
            CommentContent comm = commR.findOne(ID);
            return comm;
        }else {
            return null;
        }
    }
    //获取处室领导人员 根据用户ID
    private List<User>getCounterSignUsers(String userID){
        if (userID!=null && !userID.equals("")) {
            Long ID = Long.parseLong(userID);
            List<User> usrList = usrR.getUserInfoByID(ID);
            return usrList;
        }else {
            return null;
        }
    }
    //获取处室领导内容,在获取前需要将数据先插入
    private CommentContent getLeaderSignContent(String commentID){
        if (commentID!=null && !commentID.equals("")) {
            Long ID = Long.parseLong(commentID);
            CommentContent comm = commR.findOne(ID);
            return comm;
        }else {
            return null;
        }
    }
    //获取主送部门
    //由于groupID可能存在多个，因此需要对其进行分开处理（如：3,4,5,6）
    private List<Group>getMainSendGroups(@PathVariable String groupID){
        Long ID;
        List<Group> gr=new ArrayList<Group>();
        if (groupID!=null && !groupID.equals("")) {
            String[] splitStr = groupID.split(",");//对字符进行切分
            for (int i = 0; i < splitStr.length; ++i) {
                ID = Long.parseLong(splitStr[i]);
                gr.add(groupR.findOne(ID));
            }
        }
        return gr;
    }
    //获取抄送部门
    private List<Group>getCopytoGroups(String groupID){
        Long ID;
        List<Group> gr=new ArrayList<Group>();
        if (groupID!=null && !groupID.equals("")) {
            String[] splitStr = groupID.split(",");//对字符进行切分
            for (int i = 0; i < splitStr.length; ++i) {
                ID = Long.parseLong(splitStr[i]);
                gr.add(groupR.findOne(ID));
            }
        }
        return gr;
    }
    //获取拟稿人 根据userID
    private User getDrafterUser(String userID){
        if (userID!=null && !userID.equals("")) {
            Long ID = Long.parseLong(userID);
            User usr = usrR.findOne(ID);
            return usr;
        }else  {
            return null;
        }
    }
    //获取排版人 根据userID
    private User getComposeUser(String userID){
        if (userID!=null && !userID.equals("")) {
            Long ID = Long.parseLong(userID);
            User usr = usrR.findOne(ID);
            return usr;
        }else {
            return null;
        }
    }
    //获取办公室复核 根据userID
    private User getOfficeCheckUser(String userID){
        if (userID!=null && !userID.equals("")) {
            Long ID = Long.parseLong(userID);
            User usr = usrR.findOne(ID);
            return usr;
        }else {
            return null;
        }
    }
    //获取校对 根据userID
    private User getCheckReader(String userID){
        if (userID!=null && !userID.equals("")) {
            Long ID = Long.parseLong(userID);
            User usr = usrR.findOne(ID);
            return usr;
        }else {
            return null;
        }
    }
    //获取ContentUsers 根据userID
    private List<User>getContentUsers(String userID){
        if (userID!=null && !userID.equals("")) {
            Long ID = Long.parseLong(userID);
            List<User> usrList = usrR.getUserInfoByID(ID);
            return usrList;
        }else {
            return null;
        }
    }
    //获取MissiveVersion
    private MissiveVersion getMissiveVersionByID(String versionID){
        if (versionID!=null && !versionID.equals("")) {
            Long ID = Long.parseLong(versionID);
            MissiveVersion misVersion = missVR.findOne(ID);
            return misVersion;
        }else {
            return null;
        }
    }
    //获取Attachment 根据ID
    private List<Attachment>getAttachmentByID(String attID){
       if (attID!=null && !attID.equals("")) {
           Long ID = Long.parseLong(attID);
           List<Attachment> att = attR.getAttachmentByID(ID);
           return att;
       }else {
           return null;
       }
    }
    //插入MissiveVersion数据
    //插入后返回数据ID
    private String insertMissiveVersion(String versionID,Long versionNum,String missTitle,String docPath,String pdfPath,String missiveID){
        Long returnID;
        MissiveVersion misVer;
        versionID=versionID==null?"0":versionID;
        try {
            misVer=missVR.findOne(Long.parseLong(versionID));
            if(misVer==null) {
                misVer = new MissiveVersion();
            }
            misVer.setVersionNumber(versionNum);//设置版本号
            misVer.setMissiveTittle(missTitle);//设置版本标题
            misVer.setDocFilePath(docPath);//设置版本doc路径
            misVer.setPdfFilePath(pdfPath);//设置版本pdf路径
            //misVer.setAttachments(getAttachmentByID(attID));//设置附件信息
            misVer.setMissive(getMissive(missiveID));//设置missive信息

            missVR.save(misVer);

            returnID=misVer.id;//返回ID

            return returnID.toString();
        }catch (Exception ex){
            return "0";
        }
    }
    //插入Attachment数据
    //插入数据后返回数据ID
    public String insertAttachment( String attrNameList,String versionINum,String attTitle, String filePath, String versionID){
        String returnID;
        String[] attStr=attrNameList.split(",");//对附件数组进行拆分
        try {
            for (int i=0;i<attStr.length;++i)
            {
                Attachment att = new Attachment();
                att.setAttachmentTittle(attStr[i]);//设置附件标题
                att.setAttachmentFilePath(filePath+versionINum+"/"+attStr[i]);//设置附件路径 路径包括文件名和后缀
                att.setMissiveVersion(getMissiveVersionByID(versionID));//设置版本信息
                attR.save(att);//保存操作

            }
            return "true";
        }catch (Exception ex){
            return "0";
        }
    }
    //插入CommentContent数据
    //当插入成功后 返回当前ID
    private String insertCommentContent(String commentID, String base30URL,String imageURL,String contentText,String contentUserID){
        Long returnID;
        CommentContent commC;
        commentID=commentID==null?"0":commentID;
        try {
            commC=commR.findOne(Long.parseLong(commentID));
            if (commC==null ){
                commC=new CommentContent();
            }

            commC.setBase30url(base30URL);//设置手写内容
            commC.setImageurl(imageURL);//设置图片内容
            commC.setContentText(contentText);//设置输入内容
            commC.setContentUsers(getContentUsers(contentUserID));//设置用户
//            if (commentID.equals("0")) {
//                commC.setCreateTime(commF.getCurrentDate());//设置创建时间
//            }
//            commC.setUpdateTime(commF.getCurrentDate());//设置更新时间
            commC.setEnabled(true);//设置是否可用

            commR.save(commC);//保存数据
            returnID=commC.id;
            return returnID.toString();
        }catch (Exception ex){

            return "0";
        }

    }
    //插入faxCablePublish数据
    private boolean insertFaxCablePublish(String faxCableID,String processID,String taskID,String missiveID,String signUserID,String signContent,
                                          String counterSignUserID,String leaderSignContent,String mainSendGp,String copytoGp,
                                          String drafterUserID,String composeUserID,String officeCheckID,String printCount,
                                          String checkReaderID,String govInfo,String missiveTitle,String attTitle) {
        FaxCablePublish faxPub;

        Long faxCablePubID=faxPubR.getFaxCableIDByMissiveID(Long.parseLong(faxCableID));

        try {
            faxPub=faxPubR.findOne(faxCablePubID);
            if (faxPub==null){
                faxPub=new FaxCablePublish();
            }
            faxPub.setProcessID(Long.parseLong(processID));//设置流程ID
            faxPub.setTaskID(Long.parseLong(taskID));//设置任务ID
            faxPub.setMissiveInfo(getMissive(missiveID));//设置MissiveInfo
            faxPub.setSignIssueUsers(getSignIssueUsers(signUserID));//设置签发人员
            faxPub.setSignIssueContent(getSignIssueContent(signContent));//设置签发内容
            faxPub.setCounterSignUsers(getCounterSignUsers(counterSignUserID));//设置处室领导人员
            faxPub.setLeaderSignContent(getLeaderSignContent(leaderSignContent));//设置处室领导内容
            faxPub.setMainSendGroups(getMainSendGroups(mainSendGp));//设置主送部门
            faxPub.setCopytoGroups(getCopytoGroups(copytoGp));//设置抄送部门
            faxPub.setDrafterUser(getDrafterUser(drafterUserID));//设置拟稿人
            faxPub.setComposeUser(getComposeUser(composeUserID));//设置排版人
            faxPub.setOfficeCheckUser(getOfficeCheckUser(officeCheckID));//设置办公室复核
            faxPub.setPrintCount(Integer.parseInt(printCount));//设置打印分数
            faxPub.setCheckReader(getCheckReader(checkReaderID));//设置校对
            faxPub.setGov_info_attr(Integer.parseInt(govInfo));//设置政府信息
            faxPub.setMissiveTittle(missiveTitle);//设置标题
            faxPub.setAttachmentTittle(attTitle);//设置附件标题
            faxPubR.save(faxPub);//保存数据入库

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    //插入Missive数据和保存数据使用同一个方法，当有MissiveID后就先根据ID进行查询 然后更新数据
    //插入数据后返回ID
    private Missive insertMissive( String missiveID,String createUserID, String secretID,
                                 String missiveTypeID, String name, String missiveNum){
        Long returnID;
        Missive mis;
        missiveID=missiveID==null?"0":missiveID;
        try {
            //创建用户
            User usr=getCreateUser(createUserID);
            //密级
            SecretLevel secret=getSecretLvByID(secretID);
            //公文类型
            MissiveType missType=getMissiveType(missiveTypeID);
            mis=missR.findOne(Long.parseLong(missiveID));
            //传真电报
            if (mis==null){
                mis=new Missive();
            }
            mis.setName(name);//设置名称
            mis.setMissiveType(missType);//设置公文类型
            mis.setSecretLevel(secret);//设置密级
            mis.setMissiveNum(missiveNum);//设置公文号
            mis.setMissiveCreateUser(usr);//设置创建者
            missR.save(mis);//保存数据

            //returnID=mis.id;
            return mis;
            //return returnID.toString();
        }catch (Exception ex){
            return null;
        }



    }
    //取消按钮，删除已经上传的文件夹
    @RequestMapping(value = "/cancelData/{processID}/{version}",method = RequestMethod.POST)
    public boolean cancelFaxCableData(@PathVariable("processID") String processID,
                                      @PathVariable("version")String version ){
        //realPath=realPath.length()>realPathLen?realPath.substring(0,realPathLen):realPath;//如果字符长度大于58个字符就截取
        realPath=fileUploadDir+fileType;
        boolean fileExit=false;
        String folderPath=realPath+processID+"/"+version;//文件夹路径
        try {
            fileExit= FileOperate.exitFolder(folderPath);//判断路径是否存在
            if (fileExit){
                FileOperate.delFolder(folderPath);//如果存在，则删除
            }
            return true;
        }catch (Exception ex){
            return false;
        }

    }
    //插入FaxCablePublish数据

    //插入传真电报信息
    //在插入传真电报之前，先将其他内容插入数据库中 最后插入传真电报
    //政府公开属性信息 1代表主动公开 2 申请公开 3不予以公开
    @RequestMapping(value = "/insertData")
    public boolean insertFaxCable(faxCableReciveClass faxClass,HttpServletRequest request){



        Page<MissiveVersion>misPage;
        //获取前端传递值
        String faxCableID=faxClass.getFaxCableID();//获取公文ID 为0的话 就是新建公文
        String processID=faxClass.getProcessID();//流程ID
        String taskID=faxClass.getTaskID();//任务ID
        String secretLv=faxClass.getSecretLv();//密级
        String faxType=faxClass.getFaxType();//公文类别
        String faxID=faxClass.getFaxID();//传真电报ID
        String signIssue_Person=faxClass.getSignIssue_Person();//会签人员
        String signIssue_img=faxClass.getSignIssue_img();//会签手写图片地址
        String signIssue_30url=faxClass.getSignIssue_30url();//会签图片内容
        String signIssue_Content=faxClass.getSignIssue_Content();//会签输入内容
        String leaderSign_Person=faxClass.getLeaderSign_Person();//处室领导人员;
        String leaderSign_Content=faxClass.getLeaderSign_Content();//处室领导输入内容
        String leaderSign_img=faxClass.getLeaderSign_img();//处室领导手写图片地址
        String leaderSign_30url=faxClass.getLeaderSign_30url();//处室领导兽血图片内容
        String mainSend_Person=faxClass.getMainSend_Person();//主送部门
        String copyTo_Person=faxClass.getCopyTo_Person();//抄送部门
        String drafter=faxClass.getDrafter();//拟稿人
        String phoneNum=faxClass.getPhoneNum();//电话号码
        String composeUser=faxClass.getComposeUser();//排版
        String officeCheck=faxClass.getOfficeCheck();//办公室复核
        String printCount=faxClass.getPrintCount();//打印份数
        String CheckReader=faxClass.getCheckReader();//校对人
        String govPublic=faxClass.getGovPublic();//政府公开信息
        String FaxCableTitle=faxClass.getFaxCableTitle();//传真标题
        String FaxCableAttrTittle=faxClass.getFaxCableAttrTittle();//附件标题
        String MissiveID=faxClass.getMissiveID();//获取MissiveID
        String SigCommentID=faxClass.getSigCommentID();//获取签发内容ID
        String DepChkID=faxClass.getDepChkID();//获取处室领导内容ID
        String AttachmentID=faxClass.getAttachmentID();//获取附件ID
        String AttrNameList=faxClass.getAttrNameList();//获取附件名称字符串
        String currentUserID=faxClass.getCurrentUserID();//获取当前用户ID
        Long versionNum;
        Long missiveInfoID=Long.parseLong("0");//missiveID信息
        Pageable topOne = new PageRequest(0, 1);//设置显示数据条数
        File[] files=null;//文件列表
        String extend="";//文件后缀
        User nextUsrInfo=null;//用户信息


        //点击提交按钮后，首先判断新版本下文件夹是否存在，如果存在则将就版本的文件拷贝到新版本文件夹 否则将整个文件夹拷贝至新版本
        //realPath=realPath.length()>realPathLen?realPath.substring(0,realPathLen):realPath;//如果字符长度大于58个字符就截取
        realPath=fileUploadDir+fileType;
        String newFolder="";//新版本路径
        String oldFolder="";
        boolean folderExit=FileOperate.exitFolder(newFolder);//判断新版本路径是否存在
        //对各个数据表进行保存操作
        //首先判断是新建还是更新
        if (!faxCableID.equals("0")){
            //首先通过faxID获取MissiveID
            //missiveInfoID=faxPubR.getMissiveInfoIDByID(Long.parseLong(faxCableID)).id;
            missiveInfoID=Long.parseLong(faxCableID);
            misPage=faxPubR.getMissiveVersionByMissiveID(missiveInfoID,topOne);

            versionNum=misPage.getContent().size()==0?1:misPage.getContent().get(0).versionNumber+1;

            //newFolder=realPath+faxCableID+"/"+versionINum.toString();//新版本路径
            newFolder=realPath+processID+"/"+versionNum.toString();//新版本路径 使用流程实例ID
            //oldFolder=realPath+faxCableID+"/"+Long.toString(versionINum-1);
            oldFolder=realPath+processID+"/"+Long.toString(versionNum-1);//使用流程实例ID
            folderExit=FileOperate.exitFolder(newFolder);//判断新版本路径是否存在

            if (!folderExit) {
                //如果存在，不对文件夹进行操作
                //不存在，将就版本整个文件夹拷贝至新版本路径下
                FileOperate.newFolder(newFolder);
                FileOperate.copyFolder(oldFolder,newFolder);
            }


        }else {
            versionNum=Long.parseLong("1");//版本号
        }
        String returnMissiveID="0";//插入Missive数据返回ID
        Missive mis;
        String returnMissiveVersionID="0";//插入版本数据返回ID
        String returnAttID="0";//插入Attachment数据，返回ID
        String returnSigCommentID="0";//插入签发手写板内容
        String returnDepChkID="0";//插入处室领导核搞手写内容
        boolean insertFaxPubFlag=false;//插入FaxCablePublish标记
        String assineeVal="";//下一步指定用户参数
        String conditionVal="";//判断条件
        String nextUser="";//下一步指定用户
        String needTaskName="处室拟稿";
        String htmlUrl=htmlUrlBaseFax+taskID+"/"+processID;
        String processType="faxCablePublish";
        List<Map<String,? extends Object>>taskVarInfo=null;
        try {
            //首先对公文类型、密级等公文信息进行入库 返回插入数据ID
            mis=insertMissive(missiveInfoID.toString(),currentUserID,secretLv,faxType,"传真电报",faxID);//对Missive数据进行插入
            returnMissiveID=String.valueOf(mis.id);
            //插入签发相关内容
            returnSigCommentID=insertCommentContent(SigCommentID,signIssue_30url,signIssue_img,signIssue_Content,signIssue_Person);//由于是拟稿 只需插入签发人员
            //插入处室领导相关内容
            returnDepChkID=insertCommentContent(DepChkID,leaderSign_30url,leaderSign_img,leaderSign_Content,leaderSign_Person);//由于是拟稿 只需插入处室领导人员
            //插入版本信息
            returnMissiveVersionID=insertMissiveVersion(AttachmentID,versionNum,FaxCableTitle,"C://","D://",returnMissiveID);//插入版本信息
            //插入附件
            returnAttID=insertAttachment(AttrNameList,versionNum.toString(),FaxCableAttrTittle,realPath,returnMissiveVersionID);//插入附件
            //最后插入FaxCablePublish数据
            insertFaxPubFlag=insertFaxCablePublish(faxCableID, processID, taskID, returnMissiveID, signIssue_Person, returnSigCommentID,
                    leaderSign_Person, returnDepChkID, mainSend_Person, copyTo_Person, drafter, composeUser,
                    officeCheck, printCount, CheckReader, govPublic, FaxCableTitle, FaxCableAttrTittle);//插入传真电报内容

            //完成pdf转换，只有在处室拟稿才需要转换
            String currentTaskName=taskDAO.getCurrentTaskName(Integer.parseInt(taskID.toString()));
            //Missive missive=misR.findOne(Long.parseLong(returnMissiveID));
            String attList[]=commF.getAttAndLastVersion(realPath,AttrNameList,versionNum.toString(),processID);//获取到当前最新版本的附件数组
            //由于mac系统无法完成转换，所以代码先不使用
            //commF.convertAtt2Pdf2(currentTaskName,needTaskName,returnMissiveVersionID,versionNum,attList,Long.parseLong(processID),Long.parseLong(taskID),htmlUrl,processType);
            //流程流转 下一步的操作为处室领导
            //查找下一步用户
            nextUsrInfo=usrR.findOne(Long.parseLong(leaderSign_Person));
            nextUser=nextUsrInfo.getUserName();//获取到下一步用户
            //taskVarInfo=actR.getNextTaskInfo(Integer.parseInt(taskID), Long.parseLong(processID));//获取流程需要的参数
            taskVarInfo=actS.getNextTaskInfo(processID,Integer.parseInt(taskID));//获取流程需要的参数
            FaxCableTitle=FaxCableTitle+" 公文处理提醒";
            for (int i=0;i<taskVarInfo.size();++i){
                if (!taskVarInfo.get(i).containsValue("Back")){
                    assineeVal=taskVarInfo.get(i).get("assignee").toString();//获取下一步
                    conditionVal=taskVarInfo.get(i).get("condition").toString();//获取条件值
                    actR.completeTask(Long.parseLong(taskID),assineeVal,conditionVal,nextUser);
                    //每一步完成任务都需要给下一步用户发送短信和邮件提醒
                    actS.emailSender("faxCable",nextUsrInfo,FaxCableTitle,taskID,processID);//发送邮件
                    actS.msgSender(nextUsrInfo,"您有一份公文需要处理，请注意及时处理。");//发送短信提醒
                }
            }
            return true;

        }catch (Exception ex){
            return false;
        }



    }

    //根据公文ID对公文内容进行加载
    @RequestMapping(value = "/getFaxCableData/{faxCableID}",produces = "text/html;charset=UTF-8")
    private String getFaxCableData(@PathVariable String faxCableID){
        Long ID=null;
        ID=faxPubR.getFaxCableIDByMissiveID(Long.parseLong(faxCableID));
        try {

            List<FaxCablePublish> fax = faxPubR.getFaxCablePublishByID(ID);//根据ID获取到FaxCable对象
            Object[] obj = fax.toArray();
            objData = obj;
            return bindFaxCableReciveInfo(objData);

        }catch (Exception ex){
            return "0";
        }


    }
    @RequestMapping(value = "/getFaxCableData/static/{faxCableID}",produces = "text/html;charset=UTF-8")
    private String getFaxCableDataStatic(@PathVariable String faxCableID){
        Long ID=null;
        ID=faxPubR.getFaxCableIDByMissiveID(Long.parseLong(faxCableID));
        try {
            List<FaxCablePublish> fax = faxPubR.getFaxCablePublishByID(ID);//根据ID获取到FaxCable对象
            Object[] obj = fax.toArray();
            objData = obj;
            return bindFaxCableReciveInfoStatic(objData);
        }catch (Exception ex){
            return "0";
        }
    }

    @RequestMapping(value = "/getFaxCableData/getMissiveInfo")
    private Missive getMissiveInfo(){
        Missive misInfo=((FaxCablePublish) objData[0]).missiveInfo;
        return misInfo;
    }
    //实例化通用类 形成jason 返回前台
    private String bindFaxCableReciveInfo(Object[] obj){
        Long versionINum;
        Page<MissiveVersion> misPage;
        Long misVer;//版本信息
        faxClass.setFaxCableID(Long.toString(((FaxCablePublish) obj[0]).processID));//设置公文ID
        faxClass.setProcessID(Long.toString(((FaxCablePublish) obj[0]).processID));//设置流程ID
        faxClass.setTaskID(Long.toString(((FaxCablePublish) obj[0]).taskID));//设置任务ID
        faxClass.secretLv=((FaxCablePublish) obj[0]).missiveInfo.secretLevel==null?"0":Long.toString(((FaxCablePublish) obj[0]).missiveInfo.secretLevel.id);
        faxClass.secretLvName=((FaxCablePublish) obj[0]).missiveInfo.secretLevel==null?"":((FaxCablePublish) obj[0]).missiveInfo.secretLevel.secretLevelName;
        //faxClass.setSecretLv(Long.toString(((FaxCablePublish) obj[0]).missiveInfo.secretLevel.id));//设置密级ID
        faxClass.faxType=((FaxCablePublish) obj[0]).missiveInfo.missiveType==null?"0":Long.toString(((FaxCablePublish) obj[0]).missiveInfo.missiveType.id);
        faxClass.faxTypeName=((FaxCablePublish) obj[0]).missiveInfo.missiveType==null?"":((FaxCablePublish) obj[0]).missiveInfo.missiveType.typeName;
        //faxClass.setFaxType(Long.toString(((FaxCablePublish) obj[0]).missiveInfo.missiveType.id));//设置类别ID
        //faxClass.setFaxID(((FaxCablePublish) obj[0]).missiveInfo.missiveNum);//设置传真号
        faxClass.faxID=((FaxCablePublish) obj[0]).missiveInfo.missiveNum==null?"":((FaxCablePublish) obj[0]).missiveInfo.missiveNum;
        if(((FaxCablePublish) obj[0]).signIssueUsers.size()!=0) {
            Object[] sign = ((FaxCablePublish) obj[0]).signIssueUsers.toArray();//先转换成数组
            faxClass.setSignIssue_Person(Long.toString(((User) sign[0]).id));//设置会签人员
            faxClass.setSignIssue_PersonName(((User) sign[0]).Name);//设置会签人员姓名
        }else {
            faxClass.signIssue_Person="0";
            faxClass.signIssue_PersonName="";
        }
        faxClass.SigCommentID=((FaxCablePublish) obj[0]).signIssueContent==null?"0":Long.toString(((FaxCablePublish) obj[0]).signIssueContent.id);
        //faxClass.setSigCommentID(Long.toString(((FaxCablePublish) obj[0]).signIssueContent.id));//设置签发内容ID
        faxClass.signIssue_img=((FaxCablePublish) obj[0]).signIssueContent==null?"":((FaxCablePublish) obj[0]).signIssueContent.Imageurl;
        //faxClass.setSignIssue_img(((FaxCablePublish) obj[0]).signIssueContent.Imageurl);//设置会签图片地址
        faxClass.signIssue_30url=((FaxCablePublish) obj[0]).signIssueContent==null?"":((FaxCablePublish) obj[0]).signIssueContent.Base30url;
        //faxClass.setSignIssue_30url(((FaxCablePublish) obj[0]).signIssueContent.Base30url);//设置会签图片
        faxClass.signIssue_Content=((FaxCablePublish) obj[0]).signIssueContent==null?"":((FaxCablePublish) obj[0]).signIssueContent.contentText;
        //faxClass.setSignIssue_Content(((FaxCablePublish) obj[0]).signIssueContent.contentText);//设置会签内容
        faxClass.DepChkID=((FaxCablePublish) obj[0]).leaderSignContent==null?"0":Long.toString(((FaxCablePublish) obj[0]).leaderSignContent.id);
        //faxClass.setDepChkID(Long.toString(((FaxCablePublish) obj[0]).leaderSignContent.id));//设置处室领导内容
        if(((FaxCablePublish) obj[0]).CounterSignUsers.size()!=0) {
            Object[] counter = ((FaxCablePublish) obj[0]).CounterSignUsers.toArray();
            faxClass.setLeaderSign_Person(Long.toString(((User) counter[0]).id));//设置处室领导
            faxClass.setLeaderSign_PersonName(((User) counter[0]).Name);//设置处室领导姓名
        }else
        {
            faxClass.setLeaderSign_Person("0");
            faxClass.setLeaderSign_PersonName("");
        }
        faxClass.leaderSign_Content=((FaxCablePublish) obj[0]).leaderSignContent==null?"":((FaxCablePublish) obj[0]).leaderSignContent.contentText;
        //faxClass.setLeaderSign_Content(((FaxCablePublish) obj[0]).leaderSignContent.contentText);//设置处室领导内容
        faxClass.leaderSign_img=((FaxCablePublish) obj[0]).leaderSignContent==null?"":((FaxCablePublish) obj[0]).leaderSignContent.Imageurl;
        //faxClass.setLeaderSign_img(((FaxCablePublish) obj[0]).leaderSignContent.Imageurl);//设置处理领导图片地址
        faxClass.leaderSign_30url=((FaxCablePublish) obj[0]).leaderSignContent==null?"":((FaxCablePublish) obj[0]).leaderSignContent.Base30url;
        //faxClass.setLeaderSign_30url(((FaxCablePublish) obj[0]).leaderSignContent.Base30url);//设置处理领导图片
        if(((FaxCablePublish) obj[0]).MainSendGroups.size()!=0) {
            Object[] mainSend = ((FaxCablePublish) obj[0]).MainSendGroups.toArray();
            faxClass.setMainSend_Person(commF.getStrByObject(mainSend));//设置主送人员
            faxClass.setMainSend_PersonName(commF.getNameStrByObject(mainSend));//设置主送人员姓名
        }else {
            faxClass.setMainSend_Person("0");
            faxClass.setMainSend_PersonName("");//主送人员姓名
        }
        if (((FaxCablePublish) obj[0]).CopytoGroups.size()!=0) {
            Object[] copy = ((FaxCablePublish) obj[0]).CopytoGroups.toArray();//转换数组
            faxClass.setCopyTo_Person(commF.getStrByObject(copy));//抄送人员
            faxClass.setCopyTo_PersonName(commF.getNameStrByObject(copy));//抄送人员姓名
        }else {
            faxClass.setCopyTo_Person("0");
            faxClass.setCopyTo_PersonName("");
        }
        faxClass.drafter=((FaxCablePublish) obj[0]).DrafterUser==null?"0":Long.toString(((FaxCablePublish) obj[0]).DrafterUser.id);
        faxClass.drafterName=((FaxCablePublish) obj[0]).DrafterUser==null?"":((FaxCablePublish) obj[0]).DrafterUser.Name;//拟稿人员姓名
        //faxClass.setDrafter(Long.toString(((FaxCablePublish) obj[0]).DrafterUser.id));//设置拟稿人
        faxClass.composeUser=((FaxCablePublish) obj[0]).ComposeUser==null?"0":Long.toString(((FaxCablePublish) obj[0]).ComposeUser.id);
        faxClass.composeUserName=((FaxCablePublish) obj[0]).ComposeUser==null?"":((FaxCablePublish) obj[0]).ComposeUser.Name;//排版人姓名
        //faxClass.setComposeUser(Long.toString(((FaxCablePublish) obj[0]).ComposeUser.id));//设置排版人
        faxClass.officeCheck=((FaxCablePublish) obj[0]).OfficeCheckUser==null?"0":Long.toString(((FaxCablePublish) obj[0]).OfficeCheckUser.id);
        faxClass.officeCheckName=((FaxCablePublish) obj[0]).OfficeCheckUser==null?"":((FaxCablePublish) obj[0]).OfficeCheckUser.Name;
        //faxClass.setOfficeCheck(Long.toString(((FaxCablePublish) obj[0]).OfficeCheckUser.id));//办公室复核
        //faxClass.printCount=((FaxCablePublish) obj[0]).printCount==null?"1":Long.toString(((FaxCablePublish) obj[0]).printCount);
        faxClass.setPrintCount(Long.toString(((FaxCablePublish) obj[0]).printCount));//设置打印份数
        faxClass.CheckReader=((FaxCablePublish) obj[0]).CheckReader==null?"0":Long.toString(((FaxCablePublish) obj[0]).CheckReader.id);
        faxClass.CheckReaderName=((FaxCablePublish) obj[0]).CheckReader==null?"":((FaxCablePublish) obj[0]).CheckReader.Name;
        //faxClass.setCheckReader(Long.toString(((FaxCablePublish) obj[0]).CheckReader.id));//设置校对

        faxClass.setGovPublic(Integer.toString(((FaxCablePublish) obj[0]).Gov_info_attr));//设置政府公开属性
        faxClass.setFaxCableTitle(((FaxCablePublish) obj[0]).missiveTittle);//设置传真标题
        faxClass.setFaxCableAttrTittle(((FaxCablePublish) obj[0]).attachmentTittle);//设置附件标题
        //更新数据使用
        faxClass.MissiveID=((FaxCablePublish) objData[0]).missiveInfo==null?"0":Long.toString(((FaxCablePublish) objData[0]).missiveInfo.id);
        faxClass.setMissiveID(Long.toString(((FaxCablePublish) objData[0]).missiveInfo.id));//设置公文相关信息ID

        //Object[] ver=((FaxCablePublish) obj[0]).missiveInfo.versions.toArray();

        //获取最新版本号
        Pageable topOne = new PageRequest(0, 1);
        misPage=faxPubR.getMissiveVersionByMissiveID(Long.parseLong(faxClass.MissiveID),topOne);//获取最新版本信息
        if(misPage.getContent().size()!=0) {
            versionINum = misPage.getContent().get(0).versionNumber;//获取最新版本号

            Object[] misVerInfo = ((FaxCablePublish) obj[0]).missiveInfo.versions.toArray();//获取所有版本信息
            int len = misVerInfo.length - 1;
            Object[] att = ((MissiveVersion) misVerInfo[len]).attachments.toArray();//取最新版本的附件

            String attPath = "";
            for (int i = 0; i < att.length; ++i) {
                attPath += ((Attachment) att[i]).attachmentTittle + ",";
            }

            attPath =attPath.length()==0?"": attPath.substring(0, attPath.length() - 1);//去除最后一个，
            faxClass.AttrNameList = attPath;
            faxClass.VersionNum = Long.toString(versionINum);
        }
        String st=commF.getJsonDataByObject(faxClass);
        return st;
    }
    //静态化页面
    private String bindFaxCableReciveInfoStatic(Object[] obj){
        Long versionINum;
        Page<MissiveVersion> misPage;
        Long misVer;//版本信息
        faxClass.setFaxCableID(Long.toString(((FaxCablePublish) obj[0]).processID));//设置公文ID
        faxClass.setProcessID(Long.toString(((FaxCablePublish) obj[0]).processID));//设置流程ID
        faxClass.setTaskID(Long.toString(((FaxCablePublish) obj[0]).taskID));//设置任务ID
        faxClass.secretLv=((FaxCablePublish) obj[0]).missiveInfo.secretLevel==null?"0":((FaxCablePublish) obj[0]).missiveInfo.secretLevel.secretLevelName;
        //faxClass.setSecretLv(Long.toString(((FaxCablePublish) obj[0]).missiveInfo.secretLevel.id));//设置密级ID
        faxClass.faxType=((FaxCablePublish) obj[0]).missiveInfo.missiveType==null?"0":((FaxCablePublish) obj[0]).missiveInfo.missiveType.typeName;
        //faxClass.setFaxType(Long.toString(((FaxCablePublish) obj[0]).missiveInfo.missiveType.id));//设置类别ID
        //faxClass.setFaxID(((FaxCablePublish) obj[0]).missiveInfo.missiveNum);//设置传真号
        faxClass.faxID=((FaxCablePublish) obj[0]).missiveInfo.missiveNum==null?"":((FaxCablePublish) obj[0]).missiveInfo.missiveNum;
        if(((FaxCablePublish) obj[0]).signIssueUsers.size()!=0) {
            Object[] sign = ((FaxCablePublish) obj[0]).signIssueUsers.toArray();//先转换成数组
            faxClass.setSignIssue_Person(((User) sign[0]).Name);//设置会签人员
        }else {
            faxClass.signIssue_Person="";
        }
        faxClass.SigCommentID=((FaxCablePublish) obj[0]).signIssueContent==null?"0":Long.toString(((FaxCablePublish) obj[0]).signIssueContent.id);
        //faxClass.setSigCommentID(Long.toString(((FaxCablePublish) obj[0]).signIssueContent.id));//设置签发内容ID
        faxClass.signIssue_img=((FaxCablePublish) obj[0]).signIssueContent==null?"":((FaxCablePublish) obj[0]).signIssueContent.Imageurl;
        //faxClass.setSignIssue_img(((FaxCablePublish) obj[0]).signIssueContent.Imageurl);//设置会签图片地址
        faxClass.signIssue_30url=((FaxCablePublish) obj[0]).signIssueContent==null?"":((FaxCablePublish) obj[0]).signIssueContent.Base30url;
        //faxClass.setSignIssue_30url(((FaxCablePublish) obj[0]).signIssueContent.Base30url);//设置会签图片
        faxClass.signIssue_Content=((FaxCablePublish) obj[0]).signIssueContent==null?"":((FaxCablePublish) obj[0]).signIssueContent.contentText;
        //faxClass.setSignIssue_Content(((FaxCablePublish) obj[0]).signIssueContent.contentText);//设置会签内容
        faxClass.DepChkID=((FaxCablePublish) obj[0]).leaderSignContent==null?"0":Long.toString(((FaxCablePublish) obj[0]).leaderSignContent.id);
        //faxClass.setDepChkID(Long.toString(((FaxCablePublish) obj[0]).leaderSignContent.id));//设置处室领导内容
        if(((FaxCablePublish) obj[0]).CounterSignUsers.size()!=0) {
            Object[] counter = ((FaxCablePublish) obj[0]).CounterSignUsers.toArray();
            faxClass.setLeaderSign_Person(((User) counter[0]).Name);//设置处室领导
        }else
        {
            faxClass.setLeaderSign_Person("");
        }
        faxClass.leaderSign_Content=((FaxCablePublish) obj[0]).leaderSignContent==null?"":((FaxCablePublish) obj[0]).leaderSignContent.contentText;
        //faxClass.setLeaderSign_Content(((FaxCablePublish) obj[0]).leaderSignContent.contentText);//设置处室领导内容
        faxClass.leaderSign_img=((FaxCablePublish) obj[0]).leaderSignContent==null?"":((FaxCablePublish) obj[0]).leaderSignContent.Imageurl;
        //faxClass.setLeaderSign_img(((FaxCablePublish) obj[0]).leaderSignContent.Imageurl);//设置处理领导图片地址
        faxClass.leaderSign_30url=((FaxCablePublish) obj[0]).leaderSignContent==null?"":((FaxCablePublish) obj[0]).leaderSignContent.Base30url;
        //faxClass.setLeaderSign_30url(((FaxCablePublish) obj[0]).leaderSignContent.Base30url);//设置处理领导图片
        if(((FaxCablePublish) obj[0]).MainSendGroups.size()!=0) {
            Object[] mainSend = ((FaxCablePublish) obj[0]).MainSendGroups.toArray();
            faxClass.setMainSend_Person(commF.getStaticStrByObject(mainSend));//设置主送人员
        }else {
            faxClass.setMainSend_Person("");
        }
        if (((FaxCablePublish) obj[0]).CopytoGroups.size()!=0) {
            Object[] copy = ((FaxCablePublish) obj[0]).CopytoGroups.toArray();//转换数组
            faxClass.setCopyTo_Person(commF.getStaticStrByObject(copy));
        }else {
            faxClass.setCopyTo_Person("");
        }
        faxClass.drafter=((FaxCablePublish) obj[0]).DrafterUser==null?"0":((FaxCablePublish) obj[0]).DrafterUser.Name;
        //faxClass.setDrafter(Long.toString(((FaxCablePublish) obj[0]).DrafterUser.id));//设置拟稿人
        faxClass.phoneNum=((FaxCablePublish) obj[0]).DrafterUser==null?"0":((FaxCablePublish) obj[0]).DrafterUser.tel;//拟稿人电话号码
        faxClass.phoneNum=faxClass.phoneNum==null?"":faxClass.phoneNum;
        faxClass.composeUser=((FaxCablePublish) obj[0]).ComposeUser==null?"0":((FaxCablePublish) obj[0]).ComposeUser.Name;
        //faxClass.setComposeUser(Long.toString(((FaxCablePublish) obj[0]).ComposeUser.id));//设置排版人
        faxClass.officeCheck=((FaxCablePublish) obj[0]).OfficeCheckUser==null?"0":((FaxCablePublish) obj[0]).OfficeCheckUser.Name;
        //faxClass.setOfficeCheck(Long.toString(((FaxCablePublish) obj[0]).OfficeCheckUser.id));//办公室复核
        //faxClass.printCount=((FaxCablePublish) obj[0]).printCount==null?"1":Long.toString(((FaxCablePublish) obj[0]).printCount);
        faxClass.setPrintCount(Long.toString(((FaxCablePublish) obj[0]).printCount));//设置打印份数
        faxClass.CheckReader=((FaxCablePublish) obj[0]).CheckReader==null?"0":((FaxCablePublish) obj[0]).CheckReader.Name;
        //faxClass.setCheckReader(Long.toString(((FaxCablePublish) obj[0]).CheckReader.id));//设置校对

        faxClass.setGovPublic(Integer.toString(((FaxCablePublish) obj[0]).Gov_info_attr));//设置政府公开属性
        faxClass.setFaxCableTitle(((FaxCablePublish) obj[0]).missiveTittle);//设置传真标题
        faxClass.setFaxCableAttrTittle(((FaxCablePublish) obj[0]).attachmentTittle);//设置附件标题
        //更新数据使用
        faxClass.MissiveID=((FaxCablePublish) objData[0]).missiveInfo==null?"0":Long.toString(((FaxCablePublish) objData[0]).missiveInfo.id);
        faxClass.setMissiveID(Long.toString(((FaxCablePublish) objData[0]).missiveInfo.id));//设置公文相关信息ID

        //Object[] ver=((FaxCablePublish) obj[0]).missiveInfo.versions.toArray();

        //获取最新版本号
        Pageable topOne = new PageRequest(0, 1);
        misPage=faxPubR.getMissiveVersionByMissiveID(Long.parseLong(faxClass.MissiveID),topOne);//获取最新版本信息
        versionINum=misPage.getContent().get(0).versionNumber;//获取最新版本号

        Object[] misVerInfo=((FaxCablePublish) obj[0]).missiveInfo.versions.toArray();//获取所有版本信息
        int len=misVerInfo.length-1;
        Object[] att=((MissiveVersion) misVerInfo[len]).attachments.toArray();//取最新版本的附件

        String attPath="";
        for (int i=0;i<att.length;++i){
            attPath+=((Attachment) att[i]).attachmentTittle+",";
        }
        attPath=attPath.length()==0?"":attPath.substring(0,attPath.length()-1);//去除最后一个，
        faxClass.AttrNameList=attPath;
        faxClass.VersionNum=Long.toString(versionINum);

        String st=commF.getJsonDataByObject(faxClass);
        return st;
    }

    //更新手写以及输入内容
    @RequestMapping(value = "/updateSigCommentContent/{status}")
    public String updateCommentSigContentByID(faxCableReciveClass faxClass,HttpServletRequest request,@PathVariable String status){

        User nextUsrInfo=null;//用户信息
        //status ok表示同意 no表示不同意
        //获取前端传递值
        String faxCableID=faxClass.getFaxCableID();//获取公文ID 为0的话 就是新建公文
        String processID=faxClass.getProcessID();//流程ID
        String taskID=faxClass.getTaskID();//任务ID

        String signIssue_Person=faxClass.getSignIssue_Person();//会签人员;
        String signIssue_Content=faxClass.getSignIssue_Content();//会签输入内容
        String signIssue_img=faxClass.getSignIssue_img();//会签手写图片地址
        String signIssue_30url=faxClass.getSignIssue_30url();//会签手写图片内容
        String drafter=faxClass.getDrafter();//拟稿人，由于局领导签发同意和不同意下一步都是给拟稿人

        String assineeVal="";//下一步指定用户参数
        String conditionVal="";//判断条件
        String nextUser="";//下一步用户
        String FaxCableTitle=faxClass.getFaxCableTitle();//获取公文标题
        FaxCableTitle=FaxCableTitle+" 公文处理提醒";
        Long faxCablePubID=faxPubR.getFaxCableIDByMissiveID(Long.parseLong(faxCableID));

        List<Map<String,? extends Object>>taskVarInfo=null;
        try{
            String signID=faxPubR.getSignCommentContentIDByFaxCableID(faxCablePubID);//获取手写板内容ID
             //更新输入内容以及手写内容
            signID=insertCommentContent(signID,signIssue_30url,signIssue_img,signIssue_Content,signIssue_Person);
            //更新内容后，对流程完成流程操作，首先获取流程
            taskVarInfo=actR.getNextTaskInfo(Integer.parseInt(taskID),Long.parseLong(processID));//获取流程需要的参数
            nextUsrInfo=usrR.findOne(Long.parseLong(drafter));
            nextUser=nextUsrInfo.getUserName();//获取到下一步用户
            //nextUser=usrR.findOne(Long.parseLong(drafter)).getUserName();//如果局领导同意与否下一步都是拟稿人

            if (status.equals("ok")){
                for (int i=0;i<taskVarInfo.size();++i){
                    if (!taskVarInfo.get(i).containsValue("Back")){
                        assineeVal=taskVarInfo.get(i).get("assignee").toString();//获取下一步
                        conditionVal=taskVarInfo.get(i).get("condition").toString();//获取条件值
                        actR.completeTask(Long.parseLong(taskID),assineeVal,conditionVal,nextUser);
                        //每一步完成任务都需要给下一步用户发送短信和邮件提醒
                        actS.emailSender("faxCable",nextUsrInfo,FaxCableTitle,taskID,processID);//发送邮件
                        actS.msgSender(nextUsrInfo,"您有一份公文需要处理，请注意及时处理。");//发送短信提醒
                    }
                }
            }else
            {
                for (int i=0;i<taskVarInfo.size();++i){
                    if (taskVarInfo.get(i).containsValue("Back")){
                        assineeVal=taskVarInfo.get(i).get("assignee").toString();//获取下一步
                        conditionVal=taskVarInfo.get(i).get("condition").toString();//获取条件值
                        actR.completeTask(Long.parseLong(taskID),assineeVal,conditionVal,nextUser);
                        //每一步完成任务都需要给下一步用户发送短信和邮件提醒
                        actS.emailSender("faxCable",nextUsrInfo,FaxCableTitle,taskID,processID);//发送邮件
                        actS.msgSender(nextUsrInfo,"您有一份公文需要处理，请注意及时处理。");//发送短信提醒
                    }
                }
            }
            return "true";
        }catch (Exception ex){
            return "false";
        }
    }
    //更新手写以及输入内容
    @RequestMapping(value = "/updateDepCommentContent/{status}")
    public String updateDepCommentContentByID(faxCableReciveClass faxClass,HttpServletRequest request,@PathVariable String status){

        User nextUsrInfo=null;//用户信息
        //获取前端传递值
        String faxCableID=faxClass.getFaxCableID();//获取公文ID 为0的话 就是新建公文
        String processID=faxClass.getProcessID();//流程ID
        String taskID=faxClass.getTaskID();//任务ID
        String signIssue_Person=faxClass.getSignIssue_Person();//局领导签发 给下一步流程用户
        String drafter=faxClass.getDrafter();//拟稿人 不同意的时候流程转发人员
        String leaderSign_Person=faxClass.getLeaderSign_Person();//处室领导人员;
        String leaderSign_Content=faxClass.getLeaderSign_Content();//处室领导输入内容
        String leaderSign_img=faxClass.getLeaderSign_img();//处室领导手写图片地址
        String leaderSign_30url=faxClass.getLeaderSign_30url();//处室领导兽血图片内容

        String assineeVal="";//下一步指定用户参数
        String conditionVal="";//判断条件
        String nextUser="";//下一步用户
        String FaxCableTitle=faxClass.getFaxCableTitle();//获取公文标题
        FaxCableTitle=FaxCableTitle+" 公文处理提醒";
        Long faxCablePubID=faxPubR.getFaxCableIDByMissiveID(Long.parseLong(faxCableID));

        List<Map<String,? extends Object>>taskVarInfo=null;
        try {
            String signID = faxPubR.getDepCommentContentIDByFaxCableID(faxCablePubID);//获取手写板内容ID
            //更新输入内容以及手写内容
            signID = insertCommentContent(signID, leaderSign_30url, leaderSign_img, leaderSign_Content, leaderSign_Person);
            //更新内容后，对流程完成流程操作，首先获取流程，处室领导下一步为局领导签发

            taskVarInfo=actR.getNextTaskInfo(Integer.parseInt(taskID),Long.parseLong(processID));//获取流程需要的参数
            if (status.equals("ok")) {
                nextUsrInfo=usrR.findOne(Long.parseLong(signIssue_Person));
                nextUser=nextUsrInfo.getUserName();//获取到下一步用户
                //nextUser=usrR.findOne(Long.parseLong(signIssue_Person)).getUserName();//如果处室领导同意则下一步为领导签发
                for (int i = 0; i < taskVarInfo.size(); ++i) {
                    if (!taskVarInfo.get(i).containsValue("Back")) {
                        assineeVal = taskVarInfo.get(i).get("assignee").toString();//获取下一步
                        conditionVal = taskVarInfo.get(i).get("condition").toString();//获取条件值
                        actR.completeTask(Long.parseLong(taskID), assineeVal, conditionVal, nextUser);
                        actS.emailSender("faxCable",nextUsrInfo,FaxCableTitle,taskID,processID);//发送邮件
                        actS.msgSender(nextUsrInfo,"您有一份公文需要处理，请注意及时处理。");//发送短信提醒
                    }
                }
            }else {
                nextUser=usrR.findOne(Long.parseLong(drafter)).getUserName();//如果处室领导不同意则下一步为拟稿人
                for (int i = 0; i < taskVarInfo.size(); ++i) {
                    if (taskVarInfo.get(i).containsValue("Back")) {
                        assineeVal = taskVarInfo.get(i).get("assignee").toString();//获取下一步
                        conditionVal = taskVarInfo.get(i).get("condition").toString();//获取条件值
                        actR.completeTask(Long.parseLong(taskID), assineeVal, conditionVal, nextUser);
                        actS.emailSender("faxCable",nextUsrInfo,FaxCableTitle,taskID,processID);//发送邮件
                        actS.msgSender(nextUsrInfo,"您有一份公文需要处理，请注意及时处理。");//发送短信提醒
                    }
                }
            }
            return "true";
        }catch (Exception ex){
            return "false";
        }

    }
    //检测公文编号是否可以用
    @RequestMapping(value = "/missiveNumExit/{missiveNum}")
    public boolean getMissiveByNum(@PathVariable String missiveNum){
        List<Missive> mis=missR.getMissiveByMissiveNum(missiveNum);
        if (mis.size()>0){
            return false;//该公文号已经存在
        }else {
            return true;//该公文号能使用
        }
    }
}
