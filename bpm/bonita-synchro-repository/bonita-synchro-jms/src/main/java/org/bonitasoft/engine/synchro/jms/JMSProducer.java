package org.bonitasoft.engine.synchro.jms;

import java.io.Serializable;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSProducer {

	public static final String TOPIC_CONNECTION_FACTORY = "bonita/jms/TopicConnectionFactory";
	public static final String TOPIC_NAME = "synchroServiceTopic";

	private TopicConnectionFactory topicConnectionFactory;
	private TopicConnection topicConnection;
	private Session session;
	private Topic topic;
	private MessageProducer producer;
	

	private final long timeout;

	private static JMSProducer jmsProducer;
	
	private JMSProducer(final long timeout) throws NamingException, JMSException {
		final String brokerURL = System.getProperty("broker.url");
		
		// Create a ConnectionFactory
        this.topicConnectionFactory = new ActiveMQConnectionFactory(brokerURL);
        this.topicConnection = topicConnectionFactory.createTopicConnection();
        this.session = topicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.topic = session.createTopic(TOPIC_NAME);
		
		topicConnection.start();

		this.producer = session.createProducer(topic);

		this.timeout = timeout;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					topicConnection.stop();
					producer.close();
					session.close();
					topicConnection.close();
					
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static JMSProducer getInstance(final long messageTimeout) {
		if (jmsProducer == null) {
			try {
				jmsProducer = new JMSProducer(messageTimeout);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return jmsProducer;
	}
	
	public void sendMessage(final Map<String, Serializable> properties, final long body) throws JMSException {
		final MapMessage message = session.createMapMessage();

		for (final Map.Entry<String, Serializable> property : properties.entrySet()) {
			message.setObjectProperty(property.getKey(), property.getValue());;
		}
		message.setLong("body-id", body);
		message.setJMSExpiration(System.currentTimeMillis() + timeout);

		producer.send(message);
		//System.err.println("Message sent= " + message);
	}
}