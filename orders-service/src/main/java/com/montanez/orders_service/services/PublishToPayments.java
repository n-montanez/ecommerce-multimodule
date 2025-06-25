package com.montanez.orders_service.services;

import com.montanez.orders_service.model.payment.PaymentRequest;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

import java.util.logging.Logger;

@ApplicationScoped
public class PublishToPayments {

    private static final Logger logger = Logger.getLogger(PublishToPayments.class.getName());

    @Inject
    @JMSConnectionFactory("MessagingConnectionFactory")
    private JMSContext context;

    @Resource(lookup = "jms/PaymentsQueue")
    private Queue queue;

    public void sendPaymentRequest(PaymentRequest paymentRequest) {
        context.createProducer().send(queue, paymentRequest.toString());
        logger.info("Sending payment order: " + paymentRequest);
    }

}
