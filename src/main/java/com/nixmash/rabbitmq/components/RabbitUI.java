package com.nixmash.rabbitmq.components;

import com.nixmash.rabbitmq.io.data.DataSender;
import com.nixmash.rabbitmq.io.msgs.MsgSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by daveburke on 4/20/17.
 */
@Component
public class RabbitUI {

    private static final Logger logger = LoggerFactory.getLogger(RabbitUI.class);


    private final MsgSender msgSender;
    private final DataSender dataSender;

    public RabbitUI(MsgSender msgSender, DataSender dataSender) {
        this.msgSender = msgSender;
        this.dataSender = dataSender;
    }

    public void init() {
        msgSender.sendStringMessage();
        dataSender.sendReservationToDisplay();
//        System.out.println("any delay here?");
//        System.out.println("how about here?");
    }
}
