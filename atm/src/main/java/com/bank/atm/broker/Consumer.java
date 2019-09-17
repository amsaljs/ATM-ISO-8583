package com.bank.atm.broker;

import com.bank.atm.client.function.ClientHttp;
import com.bank.atm.client.function.DecodeIso;
import org.jpos.iso.ISOMsg;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    private DecodeIso decodeIso = new DecodeIso ();
    private ISOMsg isoMsg;
    ClientHttp clientHttp = new ClientHttp ();
    private String url="";
    @RabbitListener(queues = "${amsalj.rabbitmq.queue}")
    public void receiveMsg(String msg){
        isoMsg = decodeIso.parseISOMessage (msg);
        if(isoMsg.getString (3).equalsIgnoreCase ("300000")){
            url = "balanced";
            clientHttp.sendHttp (msg,url);
        }else if(isoMsg.getString (3).equalsIgnoreCase ("010000")){
            url="cashwithdrawal";
            clientHttp.sendHttp (msg,url);
        }else if(isoMsg.getString (3).equalsIgnoreCase ("380000")){
            url="inquiry";
            clientHttp.sendHttp (msg,url);
        }else if(isoMsg.getString (3).equalsIgnoreCase ("180000")){
            url="payment";
            clientHttp.sendHttp (msg,url);
        }else if(isoMsg.getString (3).equalsIgnoreCase ("370000")){
            url="purchaseInquiry";
            clientHttp.sendHttp (msg,url);
        }else if(isoMsg.getString (3).equalsIgnoreCase ("390000") && !isoMsg.getString (127).equalsIgnoreCase ("0314")){
            url="switchingInquiry";
            clientHttp.sendHttp (msg,url);
        }else if(isoMsg.getString (3).equalsIgnoreCase ("400000") && !isoMsg.getString (127).equalsIgnoreCase ("0314")){
            url="switchingTransfer";
            clientHttp.sendHttp (msg,url);
        }else if(isoMsg.getString (3).equalsIgnoreCase ("170000")){
            url="purchase";
            clientHttp.sendHttp (msg,url);
        }else if(isoMsg.getString (3).equalsIgnoreCase ("390000")){
            url="transferInquiry";
            clientHttp.sendHttp (msg,url);
        }else if(isoMsg.getString (3).equalsIgnoreCase ("400000")){
            url="transfer";
            clientHttp.sendHttp (msg,url);
        }

    }
}
