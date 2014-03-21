package com.sohu.tv.m.kafka.es;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.sohu.tv.m.kafka.es.resolve.NginxConfigResolver;
import com.sohu.tv.m.kafka.es.resolve.SerializSparkLog;
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

    private AtomicInteger count = new AtomicInteger(0);

	public IndexLoader() {
		this.loader = new PropertiesLoader("kafka_es.properties", "nginx_log.properties");
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
    public void printStatics() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    logger.info("processed log : " + count.get());
                    count.set(0);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
	public static void main(String[] arg) throws IOException,
			InterruptedException, ExecutionException {
		IndexLoader f = new IndexLoader();
        f.printStatics();
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
		String topics = f.loader.getProperty("kafka.topic");
		KafkaStream<Message> message = f.createConsumer(topics);
		long i = 0;
        final NginxConfigResolver resolver = new NginxConfigResolver(
                f.loader.getProperty("nginx"));
		for (MessageAndMetadata<Message> msgAndMetadata : message) {
			String log = new String(toByteArray(msgAndMetadata.message()
					.payload()));
            SerializSparkLog l = resolver.resolve(log);
            String json = JSON.toJSONString(l.getFields());
            logger.debug(json);
            CatonEvent e = JSON.parseObject(json, CatonEvent.class);
			IndexRequestBuilder irb = client.prepareIndex(
					f.loader.getProperty("index.name.prefix") + "_"
							+ sdf.format(e.getTimestamp()),
					msgAndMetadata.topic()).setSource(JSON.toJSONString(e));
			brb.add(irb);
			i++;
            f.count.incrementAndGet();
			if (i % 100 == 0) {
				brb.execute().get();
				brb = client.prepareBulk();
			}
		}
	}
//    public static void main(String[] args) {
//        IndexLoader f = new IndexLoader();
//        SimpleDateFormat sdf = new SimpleDateFormat(
//                f.loader.getProperty("index.rolling.fmt"));
//        final NginxConfigResolver resolver = new NginxConfigResolver(
//                f.loader.getProperty("nginx"));
//        String log = "211.162.33.245 - - [21/Mar/2014:11:45:28 +0800] qc.hd.sohu.com.cn \"GET /caton/video/?code=15&uid=1394656681561550&poid=&plat=6h5&sver=&os=2&sysver=0&net=wifi&playmode=&vid=1092119&sid=5259648&vtype=mp4&pn=&duFile=http%3A%2F%2Fdata.vod.itc.cn%2F%3Fnew%3D%2F85%2F128%2F6mF8nznTdV3suJ4re90hd2.mp4%26plat%3D17%26mkey%3DzLUb3WLkQ4pixRVsFCWECY9Tqoo2zZNz%26ch%3Dtv%26vid%3D1092119%26uid%3D1394656681561550%26plat%3D17%26pt%3D5%26prod%3Dh5%26pg%3Dnull%26eye%3D0%26cateCode%3D100%3B100108%3B100106%3B100102&version=0&isp2p=0&ltype=0&time=1395373534489 HTTP/1.1\" 200 237 \"http://m.tv.sohu.com/v1092119.shtml?channeled=1211010300\" \"Mozilla/5.0 (Linux; U; Android 4.1.2; zh-cn; MI 1SC Build/JZO54K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 XiaoMi/MiuiBrowser/1.0\" \"-\"";
//
//    }
	public static byte[] toByteArray(ByteBuffer buffer) {
		byte[] ret = new byte[buffer.remaining()];
		buffer.get(ret, 0, ret.length);
		return ret;
	}
}
