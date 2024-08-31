
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;


public class Consumidor {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("mqadmin");
        connectionFactory.setPassword("Admin123XX_");
        Connection conexao = connectionFactory.newConnection();
        Channel canal = conexao.createChannel();

        String NOME_FILA = "queue_total_time_duravel_persistente";

        boolean duravel = true;
        int prefetchCount = 1;
        canal.basicQos(prefetchCount);
        canal.queueDeclare(NOME_FILA, duravel, false, false, null);

        

        DeliverCallback callback = (consumerTag, delivery) -> {
            String mensagem = new String(delivery.getBody());
            System.out.println("Eu " + consumerTag + " Recebi: " + mensagem);
            
            try {
                doWork(mensagem, canal, duravel);
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            } finally {
                // System.out.println("Trabalho feito.");
                canal.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        boolean autoAck = false;
        // fila, noAck, callback, callback em caso de cancelamento (por exemplo, a fila foi deletada)
        canal.basicConsume(NOME_FILA, autoAck, callback, consumerTag -> {
            System.out.println("Cancelaram a fila: " + NOME_FILA);
        });
    }

    private static void enviaMensagemParaNovaFila(String mensagem, Channel canal, boolean isFilaDuravel ){
        String NOME_FILA = "diffTotalTime_2";
        BasicProperties messageProperty = MessageProperties.MINIMAL_PERSISTENT_BASIC;
        try {
            canal.queueDeclare(NOME_FILA, isFilaDuravel, false, false, null);
            canal.basicPublish("", NOME_FILA, false, false, messageProperty , mensagem.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Mensagem publicada: " + mensagem);
    }

    private static void doWork(String task, Channel canal, boolean isFilaDuravel) throws InterruptedException {
        // for (char a : task.toCharArray()){
        //     if (a == '.') Thread.sleep(1000);
        // }
        // Thread.sleep(1000);
        String[] strings = task.split("-");
        if (strings[0].equals("1") || strings[0].equals("1000000")){
            System.out.println("chegou no 1 ou 1000");
            enviaMensagemParaNovaFila(strings[1], canal, isFilaDuravel);
        }
        // System.out.println("Recebi mensagem: " + strings[0] + " - " + strings[1]);
    }
}


