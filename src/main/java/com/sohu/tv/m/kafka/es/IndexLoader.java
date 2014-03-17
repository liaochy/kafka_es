package com.sohu.tv.m.kafka.es;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.Message;
import kafka.message.MessageAndMetadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.alibaba.fastjson.JSON;

public class IndexLoader {

	public static Log logger = LogFactory.getLog(IndexLoader.class);

	private final PropertiesLoader loader;

	public IndexLoader() {
		this.loader = new PropertiesLoader("kafka_es.properties");

	}

	private Properties createConsumerProperties(String topic) {
		Properties props = new Properties();
		props.put("zk.connect", loader.getProperty("zookeeper.connect"));
		props.put("zk.sessiontimeout.ms", "30000");
		props.put("autooffset.reset", "largest");
		props.put("autocommit.enable", "false");
		props.put("socket.buffersize",
				loader.getProperty("kafka.client.buffer.size"));
		props.put("fetch.size", loader.getProperty("kafka.client.buffer.size"));
		props.put("groupid", loader.getProperty("kafka.groupid"));
		logger.info("Properties:" + props.toString());
		return props;
	}

	public KafkaStream<Message> createConsumer(String topic) throws IOException {
		Properties props = createConsumerProperties(topic);
		ConsumerConfig consumerConfig = new ConsumerConfig(props);
		ConsumerConnector consumerConnector = Consumer
				.createJavaConsumerConnector(consumerConfig);
		Map<String, Integer> topicMap = new HashMap<String, Integer>();
		topicMap.put(topic, 1);
		Map<String, List<KafkaStream<Message>>> topicMessageStreams = consumerConnector
				.createMessageStreams(topicMap);
		List<KafkaStream<Message>> streams = topicMessageStreams.get(topic);
		KafkaStream<Message> stream = streams.get(0);
		return stream;
	}

	public static void main(String[] arg) throws IOException,
			InterruptedException, ExecutionException {
		IndexLoader f = new IndexLoader();
		SimpleDateFormat sdf = new SimpleDateFormat(
				f.loader.getProperty("index.rolling.fmt"));
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", f.loader.getProperty("cluster.name"))
				.build();
		TransportClient client = new TransportClient(settings);
		String hosts = f.loader.getProperty("cluster.hosts");
		String[] hostslist = hosts.split(",");
		for (String h : hostslist) {
			client.addTransportAddress(new InetSocketTransportAddress(h, 9300));
		}
		BulkRequestBuilder brb = client.prepareBulk();
		String topics = f.loader.getProperty("kafka.topics");
		KafkaStream<Message> message = f.createConsumer(topics);
		long i = 0;
		for (MessageAndMetadata<Message> msgAndMetadata : message) {
			String json = new String(toByteArray(msgAndMetadata.message()
					.payload()));
			Event e = JSON.parseObject(json, Event.class);
			IndexRequestBuilder irb = client.prepareIndex(
					f.loader.getProperty("index.name.prefix") + "_"
							+ sdf.format(new Date(e.getTimestamp())),
					msgAndMetadata.topic()).setSource(JSON.toJSONString(e));
			brb.add(irb);
			i++;
			if (i % 100 == 0) {
				brb.execute().get();
				brb = client.prepareBulk();
			}
		}
	}

	public static byte[] toByteArray(ByteBuffer buffer) {
		byte[] ret = new byte[buffer.remaining()];
		buffer.get(ret, 0, ret.length);
		return ret;
	}
}
