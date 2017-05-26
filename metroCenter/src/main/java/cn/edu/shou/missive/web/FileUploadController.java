package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Controller
public class FileUploadController {
    //----------------wls------------used---------------------->>>>>>>>>start

    @Value("${spring.main.uploaddir}")
    String fileUploadDir;
    @Autowired
    MissivePublishRepository mpr;
    @Autowired
    MissiveSignRespository msr;
    @Autowired
    MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissivePublishRepository mPublishR;
    @Autowired
    private MissiveSignRespository mSignR;
    @Autowired
    private FaxCableRepository fcr;
    @Autowired
    private UserRepository useR;
    //    int realPathLen=58;//路径字符数量
    CommonFunction commF=new CommonFunction();

    //上传文件
    @RequestMapping(value="/{fileType}/upload/{instanceID}", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("files") MultipartFile file,
                                                 @PathVariable("fileType") String fileType,
                                                 @PathVariable("instanceID")Long instanceID){
        //文件路径由公文ID和版本号组成 如果是新建 则版本号为1
        //首先根据公文ID查找当前版本最新号码

        boolean folderExit;//文件夹是否存在
        String upSuccess="true";//上传成功标志
        String newFolder=null;
        String oldFolder=null;
        String realPath=fileUploadDir+"/"+fileType+"/";


        int fileVersionNum = 0;//默认文件版本为0+1
        fileVersionNum=getMaxMissiveVersion(instanceID,fileType);////获取最新版本号
        oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";
        fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
        newFolder=realPath+instanceID+"/"+fileVersionNum+"/";
        folderExit=FileOperate.exitFolder(newFolder);
        if (!folderExit) {
            {
                //如果不存在，则先将文件夹拷贝
                FileOperate.newFolder(newFolder);//创建新版本文件夹
                if (fileVersionNum!=1) {
                    FileOperate.copyFolder(oldFolder, newFolder);//拷贝文件夹 如果是新建则不需要拷贝
                }
            }
        }else {
            //如果文件夹存在 则删除文件夹
            //FileOperate.delFolder(newFolder);//删除文件夹
            if (fileVersionNum!=1) {
                FileOperate.copyFolder(oldFolder, newFolder);//拷贝文件夹 如果是新建则不需要拷贝
            }
        }
        upSuccess=commF.uploadFiles(newFolder,file);//上传文件
        return upSuccess;
    }
    //删除文件

    @RequestMapping(value="/file/{fileType}/remove/{instanceID}", method=RequestMethod.POST,produces = "application/json")
    public @ResponseBody String handleFileRemove1(@RequestParam String[] fileNames,
                                                 @PathVariable("fileType") String fileType,
                                                 @PathVariable("instanceID")Long instanceID){
        //在删除文件之前，先判断该版本下面是否有文件，没有则新建下一个版本，把该版本下的文件夹全部拷贝到新建版本下
        //然后在新建版本下删除文件
        boolean folderExit=false;//文件夹是否存在
        boolean delFlag=true;//删除标志
        boolean copyFolderFlag=true;//拷贝文件夹成功标志
        String realPath=fileUploadDir+"/"+fileType+"/";
//        realPath=realPath.length()>realPathLen?realPath.substring(0,realPathLen):realPath;//如果字符长度大于58个字符就截取
        String fileName=fileNames[0];//删除文件名称

        int fileVersionNum = 0;//默认文件版本为0+1
        fileVersionNum=getMaxMissiveVersion(instanceID,fileType);////获取最新版本号
        String oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";//原有路径
        fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
        String newFolder=realPath+instanceID+"/"+fileVersionNum+"/";//新路径
        folderExit=FileOperate.exitFolder(newFolder);
        //如果是新建公文 则直接在文件夹下删除文件
        if (fileVersionNum!=1) {
            if (folderExit) {
                //如果存在，则查找需要删除的文件进行删除
                if (FileOperate.exitFile(newFolder + fileName)) {
                    delFlag = FileOperate.delFile(newFolder + fileName);
                } else {
                    delFlag = true;//如果文件不存在 则直接返回删除为true
                }
            } else {
                //如果不存在，则先将文件夹拷贝，然后再进行文件删除
                //先创建文件夹路径
                FileOperate.newFolder(newFolder);//创建新版本文件夹
                copyFolderFlag = FileOperate.copyFolder(oldFolder, newFolder);//拷贝文件夹
                delFlag = FileOperate.delFile(newFolder + fileName);

            }
        }else {
//            faxCableMaxID=faxR.getMaxID()+1;//获取公文ID
//            versionINum=Long.parseLong("1");//最新新建公文的版本号 默认为1
            realPath=realPath+instanceID+"/"+fileVersionNum+"/"+fileName;
            delFlag=FileOperate.delFile(realPath);
        }

        if(copyFolderFlag&&delFlag){
            return "true";
        }else {
            return "false";
        }

    }
    @RequestMapping(value="/{fileType}/remove/{instanceID}", method=RequestMethod.POST,produces = "application/json")
    public @ResponseBody String handleFileRemove(@RequestParam String[] fileNames,
                                                 @PathVariable("fileType") String fileType,
                                                 @PathVariable("instanceID")Long instanceID){


        //在删除文件之前，先判断该版本下面是否有文件，没有则新建下一个版本，把该版本下的文件夹全部拷贝到新建版本下
        //然后在新建版本下删除文件
        boolean folderExit=false;//文件夹是否存在
        boolean delFlag=true;//删除标志
        boolean copyFolderFlag=true;//拷贝文件夹成功标志
        String realPath=fileUploadDir+"/"+fileType+"/";
//        realPath=realPath.length()>realPathLen?realPath.substring(0,realPathLen):realPath;//如果字符长度大于58个字符就截取
        String fileName=fileNames[0];//删除文件名称

        int fileVersionNum = 0;//默认文件版本为0+1
        fileVersionNum=getMaxMissiveVersion(instanceID,fileType);////获取最新版本号
        String oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";//原有路径
        fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
        String newFolder=realPath+instanceID+"/"+fileVersionNum+"/";//新路径
        folderExit=FileOperate.exitFolder(newFolder);
        //如果是新建公文 则直接在文件夹下删除文件
        if (fileVersionNum!=1) {
            if (folderExit) {
                //如果存在，则查找需要删除的文件进行删除
                if (FileOperate.exitFile(newFolder + fileName)) {
                    delFlag = FileOperate.delFile(newFolder + fileName);
                } else {
                    delFlag = true;//如果文件不存在 则直接返回删除为true
                }
            } else {
                //如果不存在，则先将文件夹拷贝，然后再进行文件删除
                //先创建文件夹路径
                FileOperate.newFolder(newFolder);//创建新版本文件夹
                copyFolderFlag = FileOperate.copyFolder(oldFolder, newFolder);//拷贝文件夹
                delFlag = FileOperate.delFile(newFolder + fileName);

            }
        }else {
//            faxCableMaxID=faxR.getMaxID()+1;//获取公文ID
//            versionINum=Long.parseLong("1");//最新新建公文的版本号 默认为1
            realPath=realPath+instanceID+"/"+fileVersionNum+"/"+fileName;
            delFlag=FileOperate.delFile(realPath);
        }

        if(copyFolderFlag&&delFlag){
            return "true";
        }else {
            return "false";
        }

    }

    /**
     * 文件下载
     *
     * @param fname 文件名称（含后缀）
     * @throws
     */


    @RequestMapping("/pdf/{fileType}/{instanceID}/{VersionNUM}/{fname}.{filetype}")
     public ResponseEntity<byte[]> downFile(@PathVariable String fileType,@PathVariable Long instanceID,@PathVariable Long VersionNUM,
                                            @PathVariable String fname,@PathVariable String filetype)  {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String path = req.getSession().getServletContext().getRealPath("/");
        String path = fileUploadDir+"/"+fileType+"/"+instanceID+"/"+VersionNUM+"/";
        //默认文件名称
        String downFileName = fname+"."+filetype;
        try {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", downFileName);

        try {
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(path  + URLDecoder.decode(downFileName, "UTF-8"))),
                    headers,
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            //日志
            //TODO
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }

    //pdf文件下载 by db 2014-10-22 供移动端下载
    @RequestMapping("/download/pdf/{fileType}/{instanceID}/{fname}.{filetype}")
    public ResponseEntity<byte[]> downPDFFile(@PathVariable String fileType,@PathVariable Long instanceID,
                                           @PathVariable String fname,@PathVariable String filetype)  {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String path = req.getSession().getServletContext().getRealPath("/");
        String versionNum =String.valueOf(getMaxMissiveVersion(instanceID,fileType));
        String path = fileUploadDir+"/"+fileType+"/"+instanceID+"/"+versionNum+"/"+"pdf2Pad/";
        //默认文件名称
        String downFileName = fname+"."+filetype;
        try {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", downFileName);

        try {
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(path  + URLDecoder.decode(downFileName, "UTF-8"))),
                    headers,
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            //日志
            //TODO
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }

    //移动端上传图片文件
    @RequestMapping("upload/img/{fileType}/{processDeId}/{instanceId}/{VersionNum}/{taskId}")
    public @ResponseBody String ipadUploadImg(@RequestParam("files") MultipartFile file,@PathVariable String fileType,@PathVariable String processDeId,@PathVariable Long instanceId,@PathVariable Long VersionNum,@PathVariable Long taskId){
        String imgUploadDir=this.fileUploadDir+"/"+fileType+"/"+instanceId+"/"+VersionNum+"/pdf2Pad/";//图片文件存放地址
        boolean isFileExist = FileOperate.exitFolder(imgUploadDir);
        String result="";
        commF.uploadFiles(imgUploadDir,file);
        String exsImg = imgUploadDir+taskId+".png";//新上传的png图片
        String aftImg = imgUploadDir+taskId+"new.png";//合并后的png图片
        if(processDeId.contains("ReceiptId")) {
            MissiveRecSeeCard mrsc =mrscr.getMissData(String.valueOf(instanceId));
            String preImg = mrsc.getBgPngPath();
            if(preImg!=null&&!preImg.equals("")) {
                ImageMerge.merge(exsImg, preImg, aftImg);
                mrsc.setBgPngPath(aftImg);
            }
            else{
                mrsc.setBgPngPath(exsImg);
            }
            mrscr.save(mrsc);
        }
        else if(processDeId.contains("PublishMissiveId")) {
            MissivePublish mp = mPublishR.findByProcessID(instanceId);
            String preImg=mp.getBackgroudImage();
            if(preImg!=null&&!preImg.equals("")) {
                ImageMerge.merge(exsImg, preImg, aftImg);
                mp.setBackgroudImage(aftImg);
            }
            else{
                mp.setBackgroudImage(exsImg);
            }
            mPublishR.save(mp);
        }
        else if(processDeId.contains("SignId")) {
            MissiveSign ms = mSignR.findByProcessID(instanceId);
            String preImg = ms.getBackgroudImage();
            if(preImg!=null&&!preImg.equals("")) {
                ImageMerge.merge(exsImg, preImg, aftImg);
                ms.setBackgroudImage(aftImg);
            }
            else{
                ms.setBackgroudImage(exsImg);
            }
            mSignR.save(ms);
        }
        else if(processDeId.contains("FaxId")) {
            FaxCablePublish fcp = fcr.getMissiveByProcessID(instanceId);
            String preImg=fcp.getBgPngPath();
            if(preImg!=null&&!preImg.equals("")) {
                ImageMerge.merge(exsImg, preImg, aftImg);
                fcp.setBgPngPath(aftImg);
            }
            else{
                fcp.setBgPngPath(exsImg);
            }
            fcr.save(fcp);
        }
       return "ok";
    }
    /**
     * 创建存放上传文件的文件夹，如果不存在则自动创建
     * @param realPath
     * @param uploadDate
     * @return
     */
    public String createFolder(String realPath,String uploadDate){
        String currFoder = "upload/"+uploadDate;
        String fileFoder = realPath+currFoder;


        FileOperate op = new FileOperate();
        op.newFolder(fileFoder);    //调用newFolder()方法创建文件夹
        return currFoder;
    }

    //获取最新版本号
    public  int getMaxMissiveVersion(Long instanceID,String fileType){
        int fileVersionNum = 0;//默认文件版本为0+1
        if(fileType.equals("missivePublish")){
            MissivePublish missiveForm=new MissivePublish();
            missiveForm = mpr.findByProcessID(instanceID);//获取最大版本号
            if(missiveForm!=null){
                if(missiveForm.getMissiveInfo()!=null){
                    if(missiveForm.getMissiveInfo().getVersions()!=null){
                        List<MissiveVersion> missiveVersions = missiveForm.getMissiveInfo().getVersions();
                        if(missiveForm.getMissiveInfo().getVersions().size()!=0){
                            List<Long> mvnum=new ArrayList<Long>();
                            for(MissiveVersion mv:missiveVersions){
                                mvnum.add(mv.getVersionNumber());
                            }
                            fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                        }
                    }
                }
            }
            return fileVersionNum;
        }
        else if(fileType.equals("missiveSign")){
            MissiveSign missiveForm=msr.findByProcessID(instanceID);//获取最大版本号
            if(missiveForm!=null){
                if(missiveForm.getMissiveInfo()!=null){
                    if(missiveForm.getMissiveInfo().getVersions()!=null){
                        List<MissiveVersion> missiveVersions = missiveForm.getMissiveInfo().getVersions();
                        if(missiveForm.getMissiveInfo().getVersions().size()!=0){
                            List<Long> mvnum=new ArrayList<Long>();
                            for(MissiveVersion mv:missiveVersions){
                                mvnum.add(mv.getVersionNumber());
                            }
                            fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                        }
                    }
                }
            }
            return fileVersionNum;
        }
        else if(fileType.equals("missiveReceive")){
            MissiveRecSeeCard mrsc=mrscr.getMissData(String.valueOf(instanceID));
            if(mrsc!=null){
                if(mrsc.getMissiveInfo()!=null){
                    List<MissiveVersion> missiveVersions = mrsc.getMissiveInfo().getVersions();
                    if(mrsc.getMissiveInfo().getVersions().size()!=0){
                        List<Long> mvnum=new ArrayList<Long>();
                        for(MissiveVersion mv:missiveVersions){
                            mvnum.add(mv.getVersionNumber());
                        }
                        fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                    }
                }
            }
            return fileVersionNum;
        }
        else if(fileType.contains("faxCable")){
            FaxCablePublish fcp=fcr.getMissiveByProcessID(instanceID);
            if(fcp!=null){
                if(fcp.getMissiveInfo()!=null){
                    List<MissiveVersion> missiveVersions = fcp.getMissiveInfo().getVersions();
                    if(fcp.getMissiveInfo().getVersions().size()!=0){
                        List<Long> mvnum=new ArrayList<Long>();
                        for(MissiveVersion mv:missiveVersions){
                            mvnum.add(mv.getVersionNumber());
                        }
                        fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                    }
                }
            }
            return fileVersionNum;
        }

        else return 0;
    }
    //----------------wls------------used---------------------->>>>>>>>>end




   @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(
                                                 @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            String name = file.getOriginalFilename();
            try {

                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload  because the file was empty.";
        }
    }



    @RequestMapping("/pdf/{fname}.{filetype}")
    public ResponseEntity<byte[]> downFile(@PathVariable String fname,@PathVariable String filetype)  {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String path = req.getSession().getServletContext().getRealPath("/");
        String path = "/Users/sqhe/Documents/Projects/esicmissive_springboot";
        //默认文件名称
        String downFileName = fname+"."+filetype;
        try {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", downFileName);

        try {
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(path + "/" + downFileName)),
                    headers,
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            //日志
            //TODO
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }

    @RequestMapping(value="/upload/{id}", method=RequestMethod.POST)
    @ResponseBody
    public String handleImageUpload(
            @RequestParam("files") MultipartFile file,@PathVariable Long id){
        if (!file.isEmpty()) {
            String rel_path=fileUploadDir+"/userImg/"+id;  //用户的头像上传到到该文件夹
            try {
                FileUtils.forceMkdir(new File(rel_path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String name =rel_path+"/"+file.getOriginalFilename();
            try {
                byte[] bytes=file.getBytes();
                BufferedOutputStream stream=new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                return "true";
            }catch (Exception ex){
                return "false";
            }
        } else {
            return "false";
        }
    }

    @RequestMapping(value="/uploadSign/{id}", method=RequestMethod.POST)
    @ResponseBody
    public boolean handleImageUploadSign(
            @RequestParam("files") MultipartFile file,@PathVariable Long id){
            try {
                byte[] bytes = file.getBytes();//获取签名字符流
                //将获取的签名字符流存入用户数据表
                User userInfo=useR.findOne(id);
                if (userInfo!=null){
                    userInfo.setSignImg(bytes);//将签名插入数据库
                    useR.save(userInfo);//保存操作
                    return true;
                }else {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
        }
    @RequestMapping(value="/getSign/{id}", method=RequestMethod.GET)
    @ResponseBody
    public Image handleImageGetSign(@PathVariable Long id){
        try {

            //将获取的签名字符流存入用户数据表
            User userInfo=useR.findOne(id);
            if (userInfo!=null){
                byte[] bytes = userInfo.getSignImg();//获取签名字符流

                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpg");

                //ImageIO is a class containing static methods for locating ImageReaders
                //and ImageWriters, and performing simple encoding and decoding.

                ImageReader reader = (ImageReader) readers.next();
                Object source = bis;
                ImageInputStream iis = ImageIO.createImageInputStream(source);
                reader.setInput(iis, true);
                ImageReadParam param = reader.getDefaultReadParam();
                Image image = reader.read(0, param);

                return image;
            }else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

}





