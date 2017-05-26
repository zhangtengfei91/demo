package cn.edu.shou.missive.web;

/**
 * Created by shou on 2015/12/4.
 */

import org.smslib.IOutboundMessageNotification;
import org.smslib.Message.MessageEncodings;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/MessageService")

public class MessageController {

    public class OutboundNotification implements IOutboundMessageNotification {
        public void process(String gatewayId, OutboundMessage msg) {
            System.out.println("Outbound handler called from Gateway: "
                    + gatewayId);
            System.out.println(msg);
        }
    }
    @RequestMapping(value = "/sendMsg/{nextUserTel}/{msgContent}")
    @SuppressWarnings("deprecation")
    @ResponseBody
    public String sendSMS(@PathVariable String nextUserTel, @PathVariable String msgContent) {
        Service srv;
        OutboundMessage msg;
        OutboundNotification outboundNotification = new OutboundNotification();
        srv = new Service();
        SerialModemGateway gateway = new SerialModemGateway("modem.com1",
                "COM4", 115200, "wavecom", ""); //设置端口与波特率
        gateway.setInbound(true);
        gateway.setOutbound(true);
        gateway.setSimPin("0000");
        gateway.setOutboundNotification(outboundNotification);
        srv.addGateway(gateway);
        System.out.println("开始初始化程序");
        try {
            srv.startService();
            System.out.println("start services");
            String[] phones = nextUserTel.split(",");
            for (int i = 0; i < phones.length; i++) {
                msg = new OutboundMessage(phones[i], msgContent);
                msg.setEncoding(MessageEncodings.ENCUCS2); // 中文
                srv.sendMessage(msg);
            }
            srv.stopService();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
//    public static void main(String[] args) {
//        MessageController sendMessage = new MessageController();
//        sendMessage.sendSMS("18801773426", "test success!");
//    }

}


// 监听短信接收服务
