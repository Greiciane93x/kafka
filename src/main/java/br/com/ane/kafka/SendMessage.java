package br.com.ane.kafka;

import com.github.javafaker.CreditCardType;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.stream.Stream;

@Component
public class SendMessage {

    private final Logger logger;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicCartoes;

    public SendMessage(KafkaTemplate<String, Object> kafkaTemplate,
                       @Value("${config.kafka.topic.testando}") String topicCartoes) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicCartoes = topicCartoes;
        this.logger = LoggerFactory.getLogger(SendMessage.class);
    }

    @EventListener(ApplicationStartedEvent.class) // ao subir aplicação, ele dispara esse evento
    public void sendMessage(){
        // enviador de mensagem p/ kafka
        logger.info("enviando mensgaem p/ kafka");
        Faker faker = new Faker();

        // gerando cartoes de 5 em 5s

        Flux<Long> interval = Flux.interval(Duration.ofMillis(5_000));
        Flux<Cartao> cartoes = Flux.fromStream(Stream.generate(() -> {
            return new Cartao(faker.finance().creditCard(CreditCardType.MASTERCARD),
                    faker.name().fullName(),
                    CreditCardType.MASTERCARD.name(),
                    new Random().nextInt(999));

        }));

        Flux.zip(interval, cartoes).map(linha ->{
                logger.info("chave {} valor {}", linha.getT1(), linha.getT2());
                return kafkaTemplate.send(topicCartoes, linha.getT2());
        }).blockLast();
    }
    @EventListener(ApplicationStartedEvent.class)
    public void sendMessageAne() {
        logger.info("**** Ane enviando mensagem... *****");
    }
}
