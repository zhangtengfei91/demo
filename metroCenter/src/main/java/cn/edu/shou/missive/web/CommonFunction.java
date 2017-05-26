package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MissivePublishForm;
import cn.edu.shou.missive.domain.missiveDataForm.MissiveSignForm;
import cn.edu.shou.missive.service.*;
import com.itextpdf.text.PageSize;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.tomcat.util.codec.binary.Base64;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.dom4j.DocumentException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.awt.List;
import java.io.FileOutputStream;
//The image class which will hold the input image
import com.itextpdf.text.Image;
//PdfWriter object to write the PDF document
import com.itextpdf.text.pdf.PdfWriter;
//Document object to add logical image files to PDF
import com.itextpdf.text.Document;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created by seky on 14-8-3.
 * 放置一些常用处理函数
 */
@Component
public class CommonFunction {
    //获取系统当前日期

    //转换工具路径

    @Value("${spring.main.convertTool}")
    String convertTool;
    @Value("${spring.main.uploaddir}")
    String uploaddir;
    @Autowired
    private AttachmentRepository actR;
    @Autowired
    private MissiveVersionRepository mvr;
    @Autowired
    FaxCableRepository faxPubR;//传真电报
    @Autowired
    MissiveSignRespository msr;
    @Autowired
    private MissivePublishRepository mpDAO;
    @Autowired
    private ActivitiService actService;


    public Date getCurrentDate(){

        Date now=new Date();

        return now;
    }
    //获取当前时间
    public DateTime getCurrentDateTime(){
        DateTime dateTime=new DateTime();
        return dateTime;
    }

    //获取最新流程定义编号
    public String getCurrentDeId(String processType){
        String proDeId="";
        java.util.List<ProcessDefinition> prcesses =this.actService.getAllProcessD();
        for (ProcessDefinition process : prcesses) {
            String processName = process.getName();
            if (processName.equals(processType)) {
                proDeId = process.getId();
            }
        }
        return proDeId;
    }

    //根据object返回带ID的Json数据
    public String getJsonDataByObject(Object obj){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        String json = null;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
    //根据对象数据返回数据值
    public  String getStrByObject(Object[] obj){
        String str="";
        for (int i=0;i<obj.length;++i){
                str+=((Group) obj[i]).id+",";//获取对象值
        }
        //去除最后一个,
        str=str.length()==0?"":str.substring(0,str.length()-1);

        return str;
    }
    //根据对象数据返回数据值
    public  String getNameStrByObject(Object[] obj){
        String str="";
        for (int i=0;i<obj.length;++i){
            str+=((Group) obj[i]).groupName+",";//获取对象值
        }
        //去除最后一个,
        str=str.length()==0?"":str.substring(0,str.length()-1);

        return str;
    }
    //根据对象数据返回数据值
    public  String getStaticStrByObject(Object[] obj){
        String str="";
        for (int i=0;i<obj.length;++i){
            str+=((Group) obj[i]).groupName+",";//获取对象值
        }
        //去除最后一个,
        str=str.length()==0?"":str.substring(0,str.length()-1);

        return str;
    }
    //根据对象数据返回名称
    public  String getNameByObject(Object[] obj){
        String str="";
        for (int i=0;i<obj.length;++i){
            str+=((Group) obj[i]).groupName+",";//获取对象值
        }
        //去除最后一个,
        str=str.substring(0,str.length()-1);

        return str;
    }
    //上传文件
    public String uploadFiles(String realPath,MultipartFile file){
        //首先判断文件夹是否存在 不存在创建
        FileOperate.newFolder(realPath);
        if (!file.isEmpty()) {

            String name = file.getOriginalFilename();
            String fileName=realPath+name;
            try {

                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(fileName)));
                stream.write(bytes);
                stream.close();
                return "true";
            } catch (Exception e) {
                return "false";
            }
        } else {
            return "false";
        }
    }
    //调用exe将office转换成pdf
    public String officeToPDF(String exePath,String sourceFile,String targetFile){
        //ExecuteShellComand obj = new ExecuteShellComand();
        String domainName = "google.com";
        //String sourceFile = "C:\\Users\\XQQ\\Desktop\\123.docx ";//源文件 包含路径
        //String targetFile="D:\\123.pdf";//目标文件 包含路径

        //in mac oxs
        String command = "ping -c 3 " + domainName;

        //in windows
        //String command="C:\\Users\\XQQ\\Desktop\\word2pdf816.exe"+sourceFile+targetFile;
        //String command=exePath+sourceFile+targetFile;

        String output = this.executeCommand(command);
        return output;
    }
    //调用外部执行html转pdf
    public String htmlToPDF(String sourceFile,String targetFile){
        String command="wkhtmltopdf "+sourceFile+" "+targetFile;
        String output = this.executeCommand(command);
        return output;
    }
    public String shellOption(String htmlURL,String pdfURL) {
        //ExecuteShellComand obj = new ExecuteShellComand();
        String htmlpath=htmlURL;
        String pdfpath=pdfURL;
        String command  = "wkhtmltopdf -T 0 -B 0 -L 0 -R 0";
        command+=" "+htmlpath+" "+pdfpath;
//        command="wkhtmltopdf -T 0 -B 0 -L 0 -R 0 http://localhost:8888/missiveSign/missiveSignToPDF/2522 /Users/sqhe18/Desktop/missive2.pdf";
        String output = this.executeCommand(command);
        return output;
    }
    public String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "OK";

    }
    //图片转换pdf
    //图片包括路径
    public boolean imgToPDF(String imgPath,String pdfPath)
    {
        try{
            //Create Document Object
            Document convertJpgToPdf=new Document(PageSize.A4, 0, 0, 0, 0);
            int indentation = 0;
            //Create PdfWriter for Document to hold physical file
            PdfWriter.getInstance(convertJpgToPdf, new FileOutputStream(pdfPath));
            convertJpgToPdf.open();
            //Get the input image to Convert to PDF
            Image convertJpg=Image.getInstance(imgPath);
            float scaler = ((convertJpgToPdf.getPageSize().getWidth() - convertJpgToPdf.leftMargin()
                    - convertJpgToPdf.rightMargin() - indentation) / convertJpg.getWidth()) * 100;
            convertJpg.scalePercent(scaler);
            //Add image to Document
            convertJpgToPdf.add(convertJpg);
            //Close Document
            convertJpgToPdf.close();
            System.out.println("Successfully Converted JPG to PDF in iText");
            return true;
        }
        catch (Exception i1){
            i1.printStackTrace();
            return false;
        }
    }

    //转换并合并pdf版本2 by db 2014-11-12
    public void convertAtt2Pdf2(String taskName,String needTaskName,String LastVersionID,Long latestVersion,String[] attachs, Long instanceId,Long taskId,String htmlUrl,String processType){
/*        taskName:当前任务名；
        needTaskName:需要进行附件转成pdf的任务名；
        LastVersionID 最新公文版本ID
        LatestVersion:公文最新版本号；
        attachs:最新版本的附件路径数组；
        instanceId:流程实例编号；
        taskId:当前任务编号；
        htmlUrl:静态网页地址；
        processType:流程类型*/

        String uploadPath=uploaddir+"/"+processType+"/";//不同流程上传路径

        String fileBasePath=uploadPath+instanceId+"/"+latestVersion+"/";//版本路径

        MissiveVersion mVersion = mvr.findById(Long.parseLong(LastVersionID));

        //收文表单转换成pdf

        String pdfPath=fileBasePath+"html2pdf/";
        String pdf=pdfPath+taskId+".pdf";
        if(!FileOperate.exitFolder(pdfPath)){
            FileOperate.newFolder(pdfPath);
        }
        ConvertPdf(htmlUrl, pdf, "wkhtmltopdf");

        //附件转换成pdf
        ArrayList<String> pdf4Merge = new ArrayList<String>();
        pdf4Merge.add(pdf);//第一个pdf必须是表单，而不是附件

        if(taskName.equals(needTaskName)) {//附件转换只在某些任务中进行
            java.util.List<String> suffixArr=new ArrayList<String>();
            suffixArr.add("doc");
            suffixArr.add("docx");
            suffixArr.add("ppt");
            suffixArr.add("pptx");
            suffixArr.add("xls");
            suffixArr.add("xlsx");
            for (int i=0;i<attachs.length;i++) {
                String titleWithPath = attachs[i];//getAttachmentTittle();
                String attachSuffix=titleWithPath.substring(titleWithPath.lastIndexOf(".")+1,titleWithPath.length());
                //保存由附件生成的pdf
                if(suffixArr.contains(attachSuffix)) {
                    String attachFile = titleWithPath.substring(0, titleWithPath.lastIndexOf("."));
                    String attachFileName=titleWithPath.substring(titleWithPath.lastIndexOf("/")+1,titleWithPath.lastIndexOf("."));

                    //String attachFile = fileBasePath + title;
                    String attachPdf = attachFile + ".pdf";
                    String attachpdfName = attachFileName+".pdf";
                    //防止重复转换
                      if(!FileOperate.exitFile(attachPdf)) {
                          ConvertPdf(titleWithPath, attachPdf, convertTool);
                      }

                          //pdf信息存入公文附件中，防止重复上传附件信息
                      ArrayList<String> attFiles = actR.getAttachmentByVersionId(Long.parseLong(LastVersionID));

                      if(!attFiles.contains(attachpdfName)||attFiles==null) {
                          Attachment actPdf = new Attachment();
                          actPdf.setOriginalFile(false);
                          actPdf.setAttachmentTittle(attachpdfName);
                          actPdf.setAttachmentFilePath(attachPdf);
                          actPdf.setMissiveVersion(mVersion);
                          actR.save(actPdf);
                          //actl.add(actPdf);
                          pdf4Merge.add(attachPdf);
                      }




                }
                else if(attachSuffix.equals("pdf")||attachSuffix.equals("PDF")){
                    pdf4Merge.add(attachs[i]);
                }
            }
        }
        else{
            for(String attPdf:attachs){
                String attachSuffix=attPdf.substring(attPdf.lastIndexOf(".")+1,attPdf.length());
                if(attachSuffix.equals("pdf")||attachSuffix.equals("PDF")){
                    pdf4Merge.add(attPdf);
                }
            }
        }
        //合并pdf
        mergePdfs(pdf4Merge, instanceId, latestVersion, taskId,processType);//合并pdf
    }

    //合并pdf
    public void mergePdfs(ArrayList<String> webPdfs,@PathVariable Long instanceId,@PathVariable Long versionId,@PathVariable Long taskId,String processType ) {
        PDFMergerUtility ut = new PDFMergerUtility();
        String padPdfDir=uploaddir+"/"+processType+"/"+instanceId+"/"+versionId+"/"+"pdf2Pad/";
        if(!FileOperate.exitFolder(padPdfDir)){//判断文件夹是否存在
            FileOperate.newFolder(padPdfDir);
        }
        for(String webPdf:webPdfs) {
            ut.addSource(webPdf);
        }
        ut.setDestinationFileName(padPdfDir+taskId+".pdf");
        try {
            ut.mergeDocuments();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
    }

    //转换成pdf
    /*@RequestMapping(value="/missiveReceive/html2pdf/", method=RequestMethod.GET)*/
    public String ConvertPdf(String sourceFile,String pdf,String convertTool) {
        //ExecuteShellComand obj = new ExecuteShellComand();


        //in mac oxs
        String command="";
        if(convertTool.equals("wkhtmltopdf"))

        {
            command = convertTool + " -R 0 -L 0 -T 0 -B 0 --page-width 891 " + sourceFile + " " + pdf; //"wkhtmltopdf "+sourceFile+" "+pdf;
        }
        else{
            command = convertTool + "   " + sourceFile + " " + pdf; //"wkhtmltopdf "+sourceFile+" "+pdf;
        }
        //in windows
        //String command = "ping -n 3 " + domainName;
        //String command  ="wkhtmltopdf http://localhost:8888/html2pdf/missiveReceive/25007 C:/Users/hy/Desktop/esicmissive_springboot117/esicmissive_springboot924/esicmissive_springboot_1/upload/missiveReceive/25007/1/html2pdf/25012.pdf";
        String output = this.executeCommands(command);
        try{
            wait();
        }
        catch (Exception ex){}
        return output;
    }



    private String executeCommands(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public Long getLatestVersion(Missive missive){
        Long latestVersion = 0l;
        if(missive!=null) {
            if (missive.getVersions() != null) {
                java.util.List<MissiveVersion> missiveVersions = missive.getVersions();
                if (missive.getVersions().size() != 0) {
                    java.util.List<Long> mvnum = new ArrayList<Long>();
                    for (MissiveVersion mv : missiveVersions) {
                        mvnum.add(mv.getVersionNumber());
                    }
                    latestVersion = Collections.max(mvnum);//获取最新版本号
                }
            }
        }
        return latestVersion;
    }
    //根据ID获取信息
    public FaxCablePublish getFaxCableStatic( String faxCableID){
        Long ID=null;
        ID=faxPubR.getFaxCableIDByMissiveID(Long.parseLong(faxCableID));
        try {
            FaxCablePublish fax = faxPubR.getFaxCableByID(ID);//根据ID获取到FaxCable对象

            return fax;
        }catch (Exception ex){
            return null;
        }
    }
    //根据附件列表和最新版本号，返回对象
    public String[] getAttAndLastVersion(String AttPath,String AttrNameList,String versionNum,String processID){

        String[] returnAttList=null;
        if (AttrNameList!=null && !AttrNameList.equals(""))
        {
            returnAttList=AttrNameList.split(",");
            for (int i=0;i<returnAttList.length;++i)
            {
                returnAttList[i]=AttPath+processID+"/"+versionNum+"/"+returnAttList[i];//将路径和版本号传入数组中
            }
        }
        return returnAttList;
    }

    public ArrayList<String> getAssigneeByAssigneeMap(String processType, long processID, String assigneeFiled){
        HashMap<String,ArrayList<String>> assigneeMap=getAssigneeByProcess(processType,processID);
        ArrayList<String> AssigneeUserName=assigneeMap.get(assigneeFiled);
        return AssigneeUserName;
    }
    public HashMap<String,ArrayList<String>> getAssigneeByProcess(String processType, long processID){
        HashMap<String,ArrayList<String>> assigneeMap=new HashMap<String, ArrayList<String>>();
        MissivePublishFunction mpf=new MissivePublishFunction();
        if(processType.equals("missivePublish")){
            MissivePublish missivePublish=mpDAO.findByProcessID(processID);//------------->missivePublish form get
            MissivePublishForm missivePublishForm=mpf.MissivePublishToMissivePublishForm(missivePublish);

            ArrayList<String> assignee =new ArrayList<String>();
            if(missivePublishForm.getDep_LeaderCheckUser()!=null){
                assignee.add(missivePublishForm.getDep_LeaderCheckUser().userName);
                assigneeMap.put("dep_LeaderCheck",assignee);
            }
            if(missivePublishForm.getDrafterUser()!=null){
                assignee.clear();
                assignee.add(missivePublishForm.getDrafterUser().userName);
                assigneeMap.put("drafterUser",assignee);
            }
            ArrayList<String> multiAssignee=new ArrayList<String>();
            if(missivePublishForm.getCounterSignUsers()!=null){
                for(int i=0;i<missivePublishForm.getCounterSignUsers().size();i++){
                    multiAssignee.add(missivePublishForm.getCounterSignUsers().get(i).userName);
                }
            }
            assigneeMap.put("counterSign_Person",multiAssignee);

            if(missivePublishForm.getOfficeCheckUser()!=null){
                assignee.clear();
                assignee.add(missivePublishForm.getOfficeCheckUser().userName);
                assigneeMap.put("officeCheck",assignee);
            }
            if(missivePublishForm.getSignIssueUser()!=null){
                assignee.clear();
                assignee.add(missivePublishForm.getSignIssueUser().userName);
                assigneeMap.put("signIssue_Person",assignee);
            }
            if(missivePublishForm.getPrinter()!=null){
                assignee.clear();
                assignee.add(missivePublishForm.getPrinter().userName);
                assigneeMap.put("Printer",assignee);
            }
            if(missivePublishForm.getComposeUser()!=null){
                assignee.clear();
                assignee.add(missivePublishForm.getComposeUser().userName);
                assigneeMap.put("composeUser",assignee);
            }
            if(missivePublishForm.getCheckReader()!=null){
                assignee.clear();
                assignee.add(missivePublishForm.getCheckReader().userName);
                assigneeMap.put("CheckReader",assignee);
            }

        }
        else if(processType.equals("missiveSign")){
            MissiveSign missiveSign=msr.findByProcessID(processID);//------------->missivePublish add
            MissiveSignForm missiveSignForm =mpf.missiveSignTomissiveSignForm(missiveSign);
            ArrayList<String> assignee =new ArrayList<String>();
            if(missiveSignForm.getDep_LeaderCheckUser()!=null){
                assignee.add(missiveSignForm.getDep_LeaderCheckUser().userName);
                assigneeMap.put("dep_LeaderCheck",assignee);
            }
            if(missiveSignForm.getDrafterUser()!=null){
                assignee.clear();
                assignee.add(missiveSignForm.getDrafterUser().userName);
                assigneeMap.put("drafterUser",assignee);
            }
            ArrayList<String> multiAssignee=new ArrayList<String>();
            if(missiveSignForm.getCounterSignUsers()!=null){
                for(int i=0;i<missiveSignForm.getCounterSignUsers().size();i++){
                    multiAssignee.add(missiveSignForm.getCounterSignUsers().get(i).userName);
                }
            }
            assigneeMap.put("counterSign_Person",multiAssignee);

            if(missiveSignForm.getOfficeCheckUser()!=null){
                assignee.clear();
                assignee.add(missiveSignForm.getOfficeCheckUser().userName);
                assigneeMap.put("officeCheck",assignee);
            }
            if(missiveSignForm.getSignIssueUser()!=null){
                assignee.clear();
                assignee.add(missiveSignForm.getSignIssueUser().userName);
                assigneeMap.put("signIssue_Person",assignee);
            }
        }
         return assigneeMap;
    }
    //byte[]字符流数组转换base64字符串
    public String converByteToBase64(byte[] bytes){
        String contourChart="";
        StringBuilder sb = new StringBuilder();
        sb.append("data:image/png;base64,");
        sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(bytes, false)));
        contourChart = sb.toString();

        return contourChart;
    }




}
