package cn.edu.shou.missive.service;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by seky on 14/12/2.
 */
@Component
public class SendMsgService {

    @Autowired
    private DeployRepository deR;

    String msgTepmlate="msgTemplate";//获取短息模板名称
    public String sendMsg(String smsMobNum,String missiveTitle) throws IOException {
        String uidStr="seky";//网站注册帐号用户名
        String keyStr="142a3feed90737ada207";//接口安全密码
        //String smsMobNum="15692166810";//手机号码，多个号码使用逗号隔开
        String smsTextStr=deR.getEmailTemplate(msgTepmlate);//获取短信模板内容
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn");
        post.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk");//在头文件中设置转码
        NameValuePair[] data ={ new NameValuePair("Uid", uidStr),new NameValuePair("Key", keyStr),new NameValuePair("smsMob",smsMobNum),new NameValuePair("smsText",smsTextStr)};
        post.setRequestBody(data);
        client.executeMethod(post);
        Header[] headers = post.getResponseHeaders();
        int statusCode = post.getStatusCode();
        System.out.println("statusCode:"+statusCode);
        for(Header h : headers)
        {
            System.out.println(h.toString());
        }
        String result = new String(post.getResponseBodyAsString().getBytes("gbk"));
        System.out.println(result); //打印返回消息状态


        post.releaseConnection();

        return result;
    }
}
