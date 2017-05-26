package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.web.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sqhe on 14/11/24.
 */
@Component
public class FullSearchService {

    @Value("${spring.main.uploaddir}")
    String fileUploadDir;
    @Value("${spring.main.missivePdfUrl}")
    String missivePdfUrl;//链接地址
    @Autowired FaxCableRepository faxR;
    @Autowired MissivePublishRepository misR;
    @Autowired MissiveSignRespository misSR;
    @Autowired MissiveRecSeeCardRepository misRecR;
    @Autowired PdfService pdfS;
    faxCableReciveClass faxRev=new faxCableReciveClass();
    @Autowired
    cn.edu.shou.missive.web.CommonFunction commF;
    @Autowired
    ActivitiService act;
    @Autowired
    UserRepository usrR;


    public String setSearchContent(String taskType,String taskID,String processID){
        int[] assignsUsers={};
        //将公文插入到全文检索数据库中
        if (!taskType.equals("")){
            assignsUsers=getActivitiMissive(processID);
            setSearchContent(taskType,processID,taskID,assignsUsers);
        }

        return "true";
    }
    //activiti查询的公文
    private int[] getActivitiMissive(String processID){
        int[] assigns={};//获取所有参与流程用户
        String userID="";
        List<String> assignStr= act.getProcessUsersByProcessID(processID);//根据用户，获取到用所参与的所有任务
        if (assignStr.size()>0) {
            assigns = new int[assignStr.size()];
            for (int i = 0; i < assignStr.size(); ++i) {
                userID = usrR.getUserIDByUserName(assignStr.get(i));//获取用户ID
                assigns[i] = Integer.parseInt(userID);
            }
        }
        return assigns;
    }

    public void setSearchContent(String taskType,String processID,String taskID,int[] assignsUsers)
    {
        //taskID="1";
        Page<MissiveVersion> misPage;
        Long versionNum=Long.parseLong("1");//版本号
        Pageable topOne = new PageRequest(0, 1);//设置显示数据条数
        String missivePdfViewer="/pdf/";
        String fileType="";//公文类型
        //传真电报
        if (taskType.equals("faxCable")){
            fileType="/FaxCablePublish/";
            //将传真电报信息插入到全文检索数据库中
            FaxCablePublish faxPub=faxR.getFaxCableByProcessID(Long.parseLong(processID));//根据processID获取公文信息
            misPage=faxR.getMissiveVersionByMissiveID(faxPub.getMissiveInfo().id,topOne);
            versionNum=misPage.getContent().size()==0?1:misPage.getContent().get(0).versionNumber;
            //公文pdf路径
            String pdfPath=fileUploadDir+fileType+processID+"/"+versionNum.toString()+"/html2pdf/"+taskID+".pdf";
            faxRev=setFaxCableReciveFax(faxPub, pdfPath);

            //setContentEvent(assignsUsers,pdfPath);//将信息插入全文检索数据库中
        }
        else if(taskType.equals("missivePublish")){//发文流程
            fileType="/MissivePublish/";
            MissivePublish misPub=misR.findByProcessID(Long.parseLong(processID));//根据processID获取MissivePublish信息
            misPage=faxR.getMissiveVersionByMissiveID(misPub.getMissiveInfo().id,topOne);
            versionNum=misPage.getContent().size()==0?1:misPage.getContent().get(0).versionNumber;
            //公文pdf路径
            String pdfPath=fileUploadDir+fileType+processID+"/"+versionNum.toString()+"/pdf2Pad/"+taskID+".pdf";
            faxRev=setFaxCableReciveMissivePub(misPub, pdfPath);
            //setContentEvent(assignsUsers,pdfPath);//将信息插入全文检索数据库中

        }else if (taskType.equals("missiveSign")){//签报流程
            fileType="/MissiveSign/";
            MissiveSign misSign=misSR.findByProcessID(Long.parseLong(processID));//根据processID获取MissiveSign信息
            misPage=faxR.getMissiveVersionByMissiveID(misSign.getMissiveInfo().id,topOne);
            versionNum=misPage.getContent().size()==0?1:misPage.getContent().get(0).versionNumber;
            //公文pdf路径
            String pdfPath=fileUploadDir+fileType+processID+"/"+versionNum.toString()+"/pdf2Pad/"+taskID+".pdf";
            faxRev=setFaxCableReciveMissiveSign(misSign, pdfPath);
            //setContentEvent(assignsUsers,pdfPath);//将信息插入全文检索数据库中

        }else if (taskType.equals("missiveReceive")){//收文流程
            fileType="/missiveReceive/";
            MissiveRecSeeCard misRecCard=misRecR.getMissData(processID);//根据processID获取MissiveSign信息
            misPage=faxR.getMissiveVersionByMissiveID(misRecCard.getMissiveInfo().id,topOne);
            versionNum=misPage.getContent().size()==0?1:misPage.getContent().get(0).versionNumber;
            //公文pdf路径
            String pdfPath=fileUploadDir+fileType+processID+"/"+versionNum.toString()+"/pdf2Pad/"+taskID+".pdf";
            faxRev=setFaxCableReciveMissiveRececive(misRecCard, pdfPath);
            //setContentEvent(assignsUsers,pdfPath);//将信息插入全文检索数据库中
        }

        missivePdfViewer=missivePdfUrl+fileType+processID+"/"+versionNum.toString()+"/pdf2Pad/"+taskID+".pdf";
        setContentEvent(assignsUsers,missivePdfViewer);//将信息插入全文检索数据库中
    }
    //将检索内容插入到全文检索数据库中
    private String setContentEvent(int[] assignsUsers,String missivePdfUrl)
    {
        //int[] assignsUsers = {1,2,3};//流程经手者 根据processID获取该流程下所有经手用户
        int[] mainSendUserGroup={};//主送用户组
        int[] copySendUserGroup={};//抄送用户组
        assignsUsers=new int[]{1};
        Long id = Long.parseLong(faxRev.getFaxCableID());//使用流程ID作为全文检索ID
        //Long id = Long.parseLong("1");//公文ID
        RestTemplate template = new RestTemplate();
        String url="http://localhost:9200/search/process/{id}";
        Map<String,Object> request = new HashMap<String, Object>();
        request.put("missiveTitle", faxRev.MissiveTitle);//
        //request.put("missiveTitle", "我是刘刻福");//
        request.put("author",faxRev.AuthorName);
        //request.put("author","刘刻福");
        request.put("time",faxRev.MissiveDateTime);//missive数据表中的CreatedDate
        request.put("missiveType",faxRev.MissiveType);//missive数据表中的MissiveType
        request.put("missiveNum", faxRev.MissiveNum);//missive数据表中的missiveNum
        request.put("missiveContent", faxRev.MissivePDFContent);//获取公文附件内容
        request.put("mainSend",getUserGroupArrByGroupString(faxRev.mainSend_Person));//主送
        request.put("copySend",getUserGroupArrByGroupString(faxRev.copyTo_Person));//抄送
        request.put("assigns",assignsUsers);
        request.put("missivePdfUrl",missivePdfUrl);//公文pdf路径

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<Object>(request,headers);

        ResponseEntity<String> response = template.exchange(url, HttpMethod.PUT, entity, String.class, id);
        // check the response, e.g. Location header,  Status, and body
        response.getHeaders().getLocation();
        response.getStatusCode();
        String responseBody = response.getBody();
        return "";
    }
    //获取的传真电报相关信息赋值给接收对象
    private faxCableReciveClass setFaxCableReciveFax(FaxCablePublish fax,String pdfPath){
        if (fax!=null && fax.getMissiveInfo()!=null) {
            String pdfText=pdfS.pdfToText(pdfPath);//获取公文附件pdf文本信息
            faxRev.setFaxCableID(Long.toString(fax.getProcessID()));
            faxRev.setMissiveTitle(fax.getMissiveTittle());
            faxRev.setAuthorName(fax.getDrafterUser().getName());
            faxRev.setMissiveDateTime(fax.getMissiveInfo().getCreatedDate().toString("yyyy/MM/dd"));
            faxRev.setMissiveType(fax.getMissiveInfo().getMissiveType().getTypeName());
            faxRev.setMissiveNum(fax.getMissiveInfo().getMissiveNum());
            faxRev.setMissivePDFContent(pdfText);
            if(fax.MainSendGroups.size()!=0) {
                Object[] mainSend = fax.MainSendGroups.toArray();
                faxRev.setMainSend_Person(commF.getStrByObject(mainSend));//设置主送人员
            }else {
                faxRev.setMainSend_Person("0");
            }
            if (fax.CopytoGroups.size()!=0) {
                Object[] copy = fax.CopytoGroups.toArray();//转换数组
                faxRev.setCopyTo_Person(commF.getStrByObject(copy));
            }else {
                faxRev.setCopyTo_Person("0");
            }
        }

        return faxRev;
    }
    //根据组字符串拆分成int数组
    private int[] getUserGroupArrByGroupString(String groupString){
        int[] groupArr;
        String[] Str={};
        if (!groupString.equals("") && !groupString.equals("0")){
            Str= groupString.split(",");
            groupArr=new int[Str.length];
            for (int i=0;i<Str.length;++i){
                groupArr[i]=Integer.parseInt(Str[i]);
            }

        }else {
            return null;
        }
        return groupArr;
    }
    //获取的发文相关信息赋值给接收对象
    private faxCableReciveClass setFaxCableReciveMissivePub(MissivePublish misPub,String pdfPath){
        if (misPub!=null && misPub.getMissiveInfo()!=null) {
            String pdfText=pdfS.pdfToText(pdfPath);//获取公文附件pdf文本信息

            faxRev.setFaxCableID(Long.toString(misPub.getProcessID()));
            faxRev.setMissiveTitle(misPub.getMissiveTittle());
            faxRev.setAuthorName(misPub.getDrafterUser().getName());
            faxRev.setMissiveDateTime(misPub.getMissiveInfo().getCreatedDate().toString("yyyy/MM/dd"));
            faxRev.setMissiveType(misPub.getMissiveInfo().getMissiveType().getTypeName());
            faxRev.setMissiveNum(misPub.getMissiveInfo().getMissiveNum());
            faxRev.setMissivePDFContent(pdfText);
            if(misPub.getMainSendGroups().size()!=0) {
                Object[] mainSend = misPub.getMainSendGroups().toArray();
                faxRev.setMainSend_Person(commF.getStrByObject(mainSend));//设置主送人员
            }else {
                faxRev.setMainSend_Person("0");
            }
            if (misPub.getCopytoGroups().size()!=0) {
                Object[] copy = misPub.getCopytoGroups().toArray();//转换数组
                faxRev.setCopyTo_Person(commF.getStrByObject(copy));
            }else {
                faxRev.setCopyTo_Person("0");
            }
        }

        return faxRev;
    }
    //获取的发文相关信息赋值给接收对象
    private faxCableReciveClass setFaxCableReciveMissiveSign(MissiveSign misSign,String pdfPath){
        if (misSign!=null && misSign.getMissiveInfo()!=null) {
            String pdfText=pdfS.pdfToText(pdfPath);//获取公文附件pdf文本信息
            faxRev.setFaxCableID(Long.toString(misSign.getProcessID()));
            faxRev.setMissiveTitle(misSign.getMissiveTittle());
            faxRev.setAuthorName(misSign.getDrafterUser().getName());
            faxRev.setMissiveDateTime(misSign.getMissiveInfo().getCreatedDate().toString("yyyy/MM/dd"));
            faxRev.setMissiveType(misSign.getSignType().getTypeName());
            faxRev.setMissiveNum(misSign.getMissiveInfo().getMissiveNum());
            faxRev.setMissivePDFContent(pdfText);
            faxRev.setMainSend_Person("");//主送没有
            faxRev.setCopyTo_Person("");//抄送没有
        }
        return faxRev;
    }
    //获取的发文相关信息赋值给接收对象
    private faxCableReciveClass setFaxCableReciveMissiveRececive(MissiveRecSeeCard misRecCard,String pdfPath){
        if (misRecCard!=null && misRecCard.getMissiveInfo()!=null) {
            String pdfText=pdfS.pdfToText(pdfPath);//获取公文附件pdf文本信息
            faxRev.setFaxCableID(misRecCard.getInstanceId());
            faxRev.setMissiveTitle(misRecCard.getTitle());
            faxRev.setAuthorName(misRecCard.getOfficeToDo());
            faxRev.setMissiveDateTime(misRecCard.getMissiveInfo().getCreatedDate().toString("yyyy/MM/dd"));
            faxRev.setMissiveType(misRecCard.getMissiveInfo().getMissiveType().getTypeName());
            faxRev.setMissiveNum(misRecCard.getMissiveInfo().getMissiveNum());
            faxRev.setMissivePDFContent(pdfText);
            faxRev.setMainSend_Person("");//主送没有
            faxRev.setCopyTo_Person("");//抄送没有
        }
        return faxRev;
    }
}
