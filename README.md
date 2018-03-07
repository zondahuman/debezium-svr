march action

# Debezium Install
wget https://archive.apache.org/dist/kafka/0.10.0.0/kafka_2.10-0.10.0.0.tgz
wget https://archive.apache.org/dist/kafka/0.9.0.1/kafka_2.11-0.9.0.1.tgz
wget http://mirrors.sorengard.com/apache/kafka/0.10.2.1/kafka_2.10-0.10.2.1.tgz
wget http://mirrors.ocf.berkeley.edu/apache/kafka/1.0.0/kafka_2.12-1.0.0.tgz
tar zxvf kafka_2.12-1.0.0.tgz
cd kafka_2.12-1.0.0

Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties
Start Kafka Server
bin/kafka-server-start.sh config/server.properties


# Mysql Conf  /etc/my.cnf
server-id         = 223344
log_bin           = mysql-bin
binlog_format     = row
binlog_row_image  = full
expire_logs_days  = 10

#mysql Set

show global variables like '%binlog_row_image%' ;
show global variables like '%expire_logs_days%' ;

set global expire_logs_days=10 ;

MySQL 5.6.5以上版本
gtid_mode                 = on
enforce_gtid_consistency  = on



GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'debezium' IDENTIFIED BY 'dbz';


# MySQL Connector plugin archive
wget https://repo1.maven.org/maven2/io/debezium/debezium-connector-mysql/0.7.3/debezium-connector-mysql-0.7.3-plugin.tar.gz
tar -zxvf debezium-connector-mysql-0.7.3-plugin.tar.gz
cd debezium-connector-mysql

export CLASSPATH=/home/test/install/debezium-connector-mysql



# Embedding Debezium Connectors
http://debezium.io/docs/embedded/
https://stackoverflow.com/questions/43324931/debezium-with-kafka-or-only-embedded-debezium/44143859
http://blog.csdn.net/nilin99/article/details/78224785?locationNum=5&fps=1#t0
http://blog.csdn.net/l1028386804/article/details/78348367
https://docs.confluent.io/current/connect/userguide.html
http://debezium.io/docs/connectors/mysql/#setting-up-mysql
http://debezium.io/blog/2016/05/31/Debezium-on-Kubernetes/


# Install Conf
* zookeeper: 3.4.9




# 同步网络时间
sudo ntpdate -u time.nist.gov




# Mysql Configuration :

GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'debezium' IDENTIFIED BY 'dbz';

show grants for 'debezium'


# debezium binlog subscribe :

* debezium


# Zookeeper Install :

* wget https://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
* wget https://archive.apache.org/dist/zookeeper/zookeeper-3.4.5/zookeeper-3.4.5.tar.gz


# Zookeeper Connect
* ./zkCli.sh -server localhost:2181
* ./zkCli.sh -server localhost:3181



# Kafka Topic

bin/kafka-topics.sh --create --topic hadoop --zookeeper master:2181,slave01:2181,slave02:2181 --partitions 1 --replication-factor 1



# Mysql Install
wget https://cdn.mysql.com//Downloads/MySQL-5.7/mysql-5.7.21-linux-glibc2.12-x86_64.tar.gz








