# Kettle-Plugin-Master
微信公众号："游走在数据之间"<br><br>
基于Kettle6.X和Kettle7.x平台，构建Kettle自定义插件库
<br><br>
一、集成方式：<br>
  插件集成方式很简单，把构建好的包放入到plugins目录即可<br><br>
  
二、插件库：<br>
1、splunk查询插件（app-pentaho-kettle-splunk）<br>
Splunk作为顶级企业应用日志分析软件，很多企业把Splunk作为企业运维、监控及后续集成分析的首选。当然作为完善的软件体系，也提供了丰富的API接入方式，本代码是基于服务端Splunk Enterprise 7.1.2企业版本Java Api构建splunk插件，<br>
根据UI提示，配置输入参数ip、port、username、password及SPL语句<br>
spl语句示例如下：<br>
search earliest=-7d@d latest=@d index=sep |stats dc(ip) as ip<br>
| eval host="${host}",username="${username}",password="${password}",apiurl="${apiurl}"<br>
| table index,source,_raw,_time,ip,sourcetype,host,username,password,apiurl<br>
| sort +_time<br>
| head ${limit_nm}<br>

即可get Output Fields获取对应输出字段信息，而后进行下一步的数据流操作<br>


2、Elasticsearch写入插件（app-pentaho-es6或app-pentaho-es7）<br>
2.1、General参数<br>
①Index：动态索引字段，索引前缀+动态日期<br>
②Type：默认_doc<br>
③Test Index：在线检查索引是否存在<br>
④Batch Size：批次大小<br>
⑤Stop on error：遇到错误是否终止<br>
⑥Batch Timeout：批次写入超时时间，单位秒<br>
⑦Id Field：即文档ID，doc_id<br>
⑧Overwrite if exists：存在是否覆盖<br>
⑨Output Rows：输出行<br>
2.2、Servers参数<br>
①Address：Elasticsearch集群地址列表<br>
②Port：匹配端口号<br>
2.3、Fields输出字段<br>
①Name：数据流字段<br>
②Target Name：Elasticsearch集群对应index，目标mapping字段<br>
2.4、Settings参数<br>
①cluster.name：集群名称<br>
②es.user：es鉴权认证用户名，自定义参数名<br>
③es.password：es鉴权认证密码，自定义参数名<br>
<br>
3、Redis读写插件<br>
3.1、Redis Output插件<br>
①Key：动态key字段，从上一步骤数据流动态获取（必选）<br>
②Value：动态value字段，从上一步骤数据流动态获取（必选）<br>
③TTL：key超时时间，默认值172800秒，从上一步骤数据流动态获取，可对单行数据做控制（必选）<br>
④mastername：Redis sentinels哨兵模式集群master名称（必选）<br>
⑤server ip：服务端ip列表（必选）<br>
⑥server port：哨兵端口列表，和服务端IP一一对应（必选）<br>
⑦认证密钥：服务端IP对应鉴权密码（可选）<br><br>
3.2、Redis Input插件<br>
①Key Field：动态key字段，从上一步骤数据流动态获取（必选）<br>
②Key type：key数据类型，从集合列表选择（必选）<br>
③Hash值：动态hash值字段，从上一步骤数据流动态获取（可选）<br>
④Value Field：动态value字段，从上一步骤数据流动态获取（必选）<br>
⑤Value type：value数据类型，从集合列表选择（必选）<br>
⑥mastername：Redis sentinels哨兵模式集群master名称（必选）<br>
⑦hostname：服务端ip列表（必选）<br>
⑧host port：哨兵端口列表，和服务端IP一一对应（必选）<br>
⑨auth：服务端IP对应鉴权密码（可选）<br>
<br>
3.3、Redis Delete插件<br>
①Key：动态key字段，从上一步骤数据流动态获取（必选）<br>
②输出字段名：delete操作，返回自定义输出字段名称（必选）<br>
③Value type：value数据类型，从集合列表选择（必选）<br>
④mastername：Redis sentinels哨兵模式集群master名称（必选）<br>
⑤server ip：服务端ip列表（必选）<br>
⑥server port：哨兵端口列表，和服务端IP一一对应（必选）<br>
⑦认证密钥：服务端IP对应鉴权密码（可选）<br><br>

4、MqToSql插件<br>
①JsonStr name：动态待解析json字段,从上一步骤数据流动态获取（必选）<br>
②JsonKeyStr name：所需Key List列表,此处可做字段名匹配映射（必选）<br>
③JsonValueStr name：所需Value List列表,此处可做字段名对应值类型转换和数值做函数运算操作（必选）<br>
④TableName name：目标表名（必选）<br>
⑤PrimaryKey name：主键字段，更新必备。分库分表分区键必须配置，多个字段以逗号分隔（必选）<br>
⑥OperType name：动态操作类型字段,从上一步骤数据流动态获取（必选）<br>
⑦JsonDefaultStr name：默认字段追加，必须更新时追加更新时间、更新人员等（可选）<br>
⑧OutputDML name：最终生成的可执行的DML语句（必选）<br><br>


5、KafkaConsumer插件<br>
①Topic name：要消费的topic名称（必选）<br>
②Target message field name：消息message对应的输出字段名称（必选）<br>
③Target key field name：消息key对应的输出字段名称（必选）<br>
④Partition name：消息partition对应的输出字段名称（必选）<br>
⑤Offset name：消息offset对应的输出字段名称（必选）<br>
⑥Offset value：初始化对应partition的offset值,多个分区用逗号隔开（必选）<br>
⑦JMessages limit：当次消费限制的消息行阈值，默认0不限制（必选）<br>
⑧Maximum duration of：当次消费限制的超时时间阈值，默认0不限制，单位毫秒（必选）<br>
⑨Stop on empty topic：topic为空时是否停止任务（可选）<br><br>

6、KafkaConsumerAssignPartition插件<br>
①Topic name：要消费的topic名称（必选）<br>
②Target message field name：消息message对应的输出字段名称（必选）<br>
③Target key field name：消息key对应的输出字段名称（必选）<br>
④Partition name：消息partition对应的输出字段名称（必选）<br>
⑤Assign Partition：指定消息主题对应partition编号（必选）<br>
⑥Offset name：消息offset对应的输出字段名称（必选）<br>
⑦Offset value：初始化对应partition的offset值,多个分区用逗号隔开（必选）<br>
⑧group.id：自定义的消费组ID,插件下方参数设置有默认值（可选）<br>
⑨Messages limit：当次消费限制的消息行阈值，默认0不限制（必选）<br>
⑩Maximum duration of：当次消费限制的超时时间阈值，默认0不限制，单位毫秒（必选）<br>
⑪Stop on empty topic：topic为空时是否停止任务（可选）<br><br>

7、KafkaProducerByKey插件<br>
①Topic name：要推送消息的topic名称（必选）<br>
②Message field name：消息message对应的输出字段名称（必选）<br>
③Key field name：消息key对应的输出字段名称，为空则随机分区分配（可选）<br>