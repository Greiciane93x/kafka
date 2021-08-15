package br.com.ane.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SendMessage {

    private final Logger logger;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SendMessage(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.logger = LoggerFactory.getLogger(SendMessage.class);
    }

    @EventListener(ApplicationStartedEvent.class) // ao subir aplicação, ele dispara esse evento
    public void sendMessage(){
        // enviador de mensagem p/ kafka
        logger.info("enviando mensgaem p/ kafka");
        Cartao cartao = new Cartao("5237469126514216", "Ane", "Visa", 233);
        kafkaTemplate.send("ane-kafka-domingo", cartao);
        logger.info("enviando mensagem para o kafka {}", cartao);
    }
    @EventListener(ApplicationStartedEvent.class)
    public void sendMessageAne() {
        logger.info("**** Ane enviando mensagem... *****");
    }
}
