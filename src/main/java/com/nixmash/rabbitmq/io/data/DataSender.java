package com.nixmash.rabbitmq.io.data;

import com.nixmash.rabbitmq.enums.ReservationQueue;
import com.nixmash.rabbitmq.h2.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by daveburke on 4/20/17.
 */
@Component
public class DataSender {

    private static final Logger logger = LoggerFactory.getLogger(DataSender.class);

    private final RabbitTemplate rabbitTemplate;
    private final DataReceiver dataReceiver;

    public DataSender(RabbitTemplate rabbitTemplate, DataReceiver dataReceiver) {
        this.rabbitTemplate = rabbitTemplate;
        this.dataReceiver = dataReceiver;
    }

    public void createReservation() {

        Reservation reservation = new Reservation();
        Reservation created = new Reservation();
        String createQueue = ReservationQueue.Create;
        String showQueue = ReservationQueue.Show;
        String jsonCreateQueue = ReservationQueue.JsonCreate;
        String createAndShowQueue = ReservationQueue.CreateAndShow;

        // Using @SendTo

        reservation = new Reservation("Waldo");
        rabbitTemplate.convertAndSend(createQueue, reservation);
        getReceipt(dataReceiver.getCreateLatch(), createQueue);
        created = (Reservation) rabbitTemplate.receiveAndConvert(showQueue, 10_000);
        logger.info("Reservation Created: " + created.toString() + "\n");

        // Sending and Receiving from a Single Queue

        reservation = new Reservation("Pete");
        created = (Reservation)
                rabbitTemplate.convertSendAndReceive(createAndShowQueue, reservation);
        getReceipt(dataReceiver.getCreateAndShowLatch(), createAndShowQueue);
        logger.info("Reservation Created: " + created.toString() + "\n");

        // Sending Json

//        reservation = new Reservation("Jack");
//        created = (Reservation)
//                rabbitTemplate.convertSendAndReceive(jsonCreateQueue,
//                       serialize(reservation));
//        getReceipt(dataReceiver.getJsonCreateLatch(), jsonCreateQueue);
//        logger.info("Reservation Created: " + created.toString());

        // Sending Json

        reservation = new Reservation("Bopper");
        created = (Reservation) rabbitTemplate.convertSendAndReceive(jsonCreateQueue, reservation);

        getReceipt(dataReceiver.getJsonCreateLatch(), jsonCreateQueue);
        logger.info("Reservation Created: " + created.toString());

    }

    private void getReceipt(CountDownLatch latch, String threadName) {
        try {
            boolean done =  latch.await(10000, TimeUnit.MILLISECONDS);
            if (!done) {
                logger.error(String.format("%s THREAD NOT COMPLETE -- TIMEOUT OCCURRED", threadName));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
