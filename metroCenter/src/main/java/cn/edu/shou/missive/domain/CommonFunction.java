package cn.edu.shou.missive.domain;

//import cn.edu.shou.missive.domain.FaxCablePublish;

import cn.edu.shou.missive.service.MissivePublishRepository;
import cn.edu.shou.missive.service.MissiveSignRespository;
import cn.edu.shou.missive.web.FileOperate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Calendar;
import java.util.Date;

//import org.codehaus.jackson.annotate.JsonAutoDetect;
//import org.codehaus.jackson.annotate.JsonMethod;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.map.SerializationConfig;

/**
 * Created by seky on 14-8-3.
 * 放置一些常用处理函数
 */

public class CommonFunction {

    @Autowired
    MissivePublishRepository mpr;

    @Autowired
    MissiveSignRespository msr;
//    //获取系统当前日期
//    public String BASEPATH="/Users/sqhe18/Desktop/wlsh/esicmissive_springboot_1/";
//    public String BASEHOSTURL="http://localhost:8888/";
    public Date getCurrentDate(){

        Date now=new Date();

        return now;
    }
    //根据object返回带ID的Json数据
//    public String getJsonDataByObject(Object obj){
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
//        mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
//        String json = null;
//        try {
//            json = mapper.writeValueAsString(obj);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }

    //获取当前年月日
    public int[] getCurrentDateTime() {
        Date date = new Date(); // your date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int yearC = cal.get(Calendar.YEAR);
        int monthC = cal.get(Calendar.MONTH)+1;
        int dayC = cal.get(Calendar.DAY_OF_MONTH);
        int[] arr = new int[]{yearC, monthC, dayC};
        return arr;
    }

    //根据对象数据返回数据值
    public  String getStrByObject(Object[] obj){
        String str="";
        for (int i=0;i<obj.length;++i){
                str+=((Group) obj[i]).id+",";//获取对象值
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

                //byte[] bytes = file.getBytes();
                InputStream fileInputStream = file.getInputStream();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(fileName)));

                byte[] bytes = new byte[1024];

                int bytesWritten = 0;
                int byteCount = 0;
                while ((byteCount = fileInputStream.read(bytes)) != -1)
                {
                   // int bytesLength=bytes.length;

                    stream.write(bytes, 0, byteCount);
                    bytesWritten += byteCount;
//                    stream.flush();
                }
                fileInputStream.close();
                //outputStream.close();
               // stream.write(fileInputStream.read());
                //stream.write(bytes);
                stream.close();
                return "true";
            } catch (Exception e) {
                return "false";
            }
        } else {
            return "false";
        }
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
    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
//            p.waitFor();
//            BufferedReader reader =
//                    new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//            String line = "";
//            while ((line = reader.readLine())!= null) {
//                output.append(line + "\n");
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "OK";

    }

}
