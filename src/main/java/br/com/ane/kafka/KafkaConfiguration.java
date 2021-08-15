package br.com.ane.kafka;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

// papel de produtor
@Configuration
public class KafkaConfiguration {

    //setando no application.properties
    private final String kafkaServer;
    private final Integer retry;
    private final Integer bufferMemory;

    public KafkaConfiguration(@Value("${config.kafka.server}") String kafkaServer,
                              @Value("${config.kafka.retry}") Integer retry,
                              @Value("${config.kafka.bufferMemory}") Integer bufferMemory) {
        this.kafkaServer = kafkaServer;
        this.retry = retry;
        this.bufferMemory = bufferMemory;
    }
    // kafka trabalha numa estrutura de chave e valor da mensagem, e mais uma posição que é
    // o offset - a posição numa pilha que está essa mensagem
    @Bean
    public ProducerFactory<String, Object> producerFactory(){
      return new DefaultKafkaProducerFactory(Map.of(
                KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                BOOTSTRAP_SERVERS_CONFIG, kafkaServer,  // hardcoded substituído , endereco do servidor do kafka
                RETRIES_CONFIG,  retry, // retentativa caso não consiga enviar as mensagens
                BUFFER_MEMORY_CONFIG, bufferMemory //empacotamento de qnt em qnts bytes eu quero que ele espere p mandar as mensagens

        ));

    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
