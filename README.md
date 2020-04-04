# 乐优商城

## 1.项目介绍

### 1.1.项目简介

- 乐优商城是一个全品类的电商购物网站（B2C）。
- 用户可以在线购买商品、加入购物车、下单、秒杀商品
- 可以品论已购买商品
- 管理员可以在后台管理商品的上下架、促销活动
- 管理员可以监控商品销售状况
- 客服可以在后台处理退款操作
- 希望未来3到5年可以支持千万用户的使用



### 1.2.系统架构

乐优商城架构缩略图：

![1573821025276](https://tiancixiong.coding.net/p/BlogIMG/d/BlogIMG/git/raw/master/blog/20191115_leyou/README/1573821025276.png)

整个乐优商城可以分为两部分：后台管理系统、前台门户系统。

- 后台管理：

  - 后台系统主要包含以下功能：
    - 商品管理，包括商品分类、品牌、商品规格等信息的管理
    - 销售管理，包括订单统计、订单退款处理、促销活动生成等
    - 用户管理，包括用户控制、冻结、解锁等
    - 权限管理，整个网站的权限控制，采用JWT鉴权方案，对用户及API进行权限控制
    - 统计，各种数据的统计分析展示
  - 后台系统会采用前后端分离开发，而且整个后台管理系统会使用Vue.js框架搭建出单页应用（SPA）。
  - 预览图：![1573821246869](https://tiancixiong.coding.net/p/BlogIMG/d/BlogIMG/git/raw/master/blog/20191115_leyou/README/1573821246869.png)

- 前台门户

  - 前台门户面向的是客户，包含与客户交互的一切功能。例如：
    - 搜索商品
    - 加入购物车
    - 下单
    - 评价商品等等
  - 前台系统我们会使用Thymeleaf模板引擎技术来完成页面开发。出于SEO优化的考虑，我们将不采用单页应用。
  - 预览图：![1573821296666](https://tiancixiong.coding.net/p/BlogIMG/d/BlogIMG/git/raw/master/blog/20191115_leyou/README/1573821296666.png)



### 1.3.技术选型

前端技术：

- 基础的HTML、CSS、JavaScript（基于ES6标准）
- JQuery
- Vue.js 2.0以及基于Vue的框架：Vuetify
- 前端构建工具：WebPack
- 前端安装包工具：NPM
- Vue脚手架：Vue-cli
- Vue路由：vue-router
- ajax框架：axios
- 基于Vue的富文本框架：quill-editor

后端技术：

- 基础的SpringMVC、Spring 5.0和MyBatis3
- Spring Boot 2.0.1版本
- Spring Cloud 最新版 Finchley.RC1
- Redis-4.0
- RabbitMQ-3.4
- Elasticsearch-5.6.8
- nginx-1.10.2：
- FastDFS - 5.0.8
- MyCat
- Thymeleaf



## 2.项目结构

- [leyou](https://github.com/tiancixiong/leyou)：后台管理系统后台
  - ly-registry：注册中心模块
  - ly-api-gateway：网关模块
  - ly-item：商品服务模块
  - ly-common：通用工具模块
  - ly-upload：图片上传模块
  - ly-search：搜索服务模块
  - ly-goods-web：商品详情页服务模块
  - ly-user：用户中心模块
  - ly-sms-service：短信服务模块
  - ly-auth：授权中心模块
  - ly-cart：购物车服务模块
  - ly-order：订单服务模块
- [leyou-manage-web](https://github.com/tiancixiong/leyou-manage-web)：后台管理系统前端
- [leyou-protal](https://github.com/tiancixiong/leyou-protal)：前台门户

另：[leyou-demo](https://github.com/tiancixiong/leyou-demo)(乐优商城demo练习项目)，包括：Spring Cloud组件(Eureka、Zuul、Robbin、Feign、Hystix)学习、Elasticsearch和Spring Data Elasticsearch学习、RabbitMQ和Spring AMQP学习、阿里大于学习



## 3.资料

### 3.1.数据库

[leyou.sql](https://github.com/tiancixiong/leyou/raw/db/leyou.sql)



### 3.2.配置文件

#### 3.2.1.hosts

```
# 乐优商城
127.0.0.1 api.leyou.com # 网关Zuul
127.0.0.1 manage.leyou.com # 后台系统
127.0.0.1 www.leyou.com # 乐优门户
192.168.56.101 image.leyou.com	# 图片服务器，虚拟机地址
```



#### 3.2.2nginx

```nginx
# 乐优商城
server {
	listen       80;
	server_name  manage.leyou.com;

	proxy_set_header X-Forwarded-Host $host;
	proxy_set_header X-Forwarded-Server $host;
	proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

	location / {
		proxy_pass http://127.0.0.1:9001;
		proxy_connect_timeout 600;
		proxy_read_timeout 600;
	}
}
server {
	listen       80;
	server_name  api.leyou.com;

	proxy_set_header X-Forwarded-Host $host;
	proxy_set_header X-Forwarded-Server $host;
	proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
	# 转发时，携带自身的host，而不是转发后的host(127.0.0.1)
	proxy_set_header Host $host;

	# 上传路径的映射
	location /api/upload {	
		proxy_pass http://127.0.0.1:8082;
		proxy_connect_timeout 600;
		proxy_read_timeout 600;
		# 对请求路径进行重写 eg：/api/upload/image -> /upload/image
		rewrite "^/api/(.*)$" /$1 break; 
	}
	
	location / {
		proxy_pass http://127.0.0.1:10010;
		proxy_connect_timeout 600;
		proxy_read_timeout 600;
	}
}
# 乐优门户
server {
	listen       80;
	server_name  www.leyou.com;

	proxy_set_header X-Forwarded-Host $host;
	proxy_set_header X-Forwarded-Server $host;
	proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
	
	location /item {
		# 先找本地
		root html/leyou;
		if (!-f $request_filename) { # 请求的文件不存在，就反向代理
			proxy_pass http://127.0.0.1:8084;
			break;
		}
	}
	
	location / {
		proxy_pass http://127.0.0.1:9002;
		proxy_connect_timeout 600;
		proxy_read_timeout 600;
	}
}
```
