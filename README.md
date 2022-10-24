# IKEYIT 后端服务
当前最新版本： 0.5  
代码正在整理搬运中，初步可以运行起来。  
[![GitHub stars](https://img.shields.io/github/stars/ikeyit/ikeyit-services.svg?style=social&label=Stars)](https://github.com/ikeyit/ikeyit-services)
[![GitHub forks](https://img.shields.io/github/forks/ikeyit/ikeyit-services.svg?style=social&label=Fork)](https://github.com/ikeyit/ikeyit-services)

## 项目介绍
IKEYIT是一个开源的电商平台系统，支持多店铺。目标基于此构架创建更多的基础服务。 
此GIT仓库仅为后端微服务实现。  
React后台管理平台：https://github.com/ikeyit/ikeyit-management-web  
小程序：https://github.com/ikeyit/ikeyit-shop-wxamp (敬请期待)
## 技术栈
- Spring Boot
- Spring Security + JWT
- Mybatis
- redis
- MySQL
- Rocket MQ
- Nacos
- logback
- ElasticSearch
## 子模块说明
### 支持模块
- common：工具类，业务异常统一处理
- mqhelper: 本地消息表，配合MQ用来实现分布式事务，目前系统并没有使用senta，或ROCKET MQ中的分布式事务这样的中间件
- passport-security：提供对spring security的功能扩展，支持手机认证/手机验证码认证/微信小程序认证/手机微信认证
- passport-resource: 资源服务器实现JWT校验。目前整个系统为各个服务自行校验JWT，而非使用服务网关统一认证校验  
### 微服务
- passport-service：统一认证服务
- user-service: 用户管理服务
- message-service: 消息/通知服务
- cms-service: 内容管理服务
- media-service：用户上传文件管理服务
- product-service: 商品服务
- pay-service: 支付服务
- trade-service：交易服务
## 微服务合并
微服务很多，就需要很多机器或者实例来运行，对硬件要求比较高。故进行了一些微服务的合并，仅保留了代码上的区分。  
目前：  
user-service, message-service 合并在 passport-service 中  
cms-service, media-service 合并在 product-service 中  
pay-service 合并在 trade-service 中  
## 功能
- 多店铺
- 商品管理
- 商品搜索
- 订单管理
- 退货售后
- 店铺装修
- 店铺分类
- 商品类目管理
- 第三方支付：微信
## IKEYIT能给你带来什么好处？
1.中小企业可以直接用来架设自己的私有网店系统。  
2.学习SpringBoot开发微服务的参考。IKEYIT代码上尽量减少依赖，避免学习成本过高。  
3.项目结构尽可能清晰，方便在此基础上做调优，适配第三方。
## 如何在开发环境尽快跑起来？   
项目使用Gradle构建工具，所以你至少需要已经配置好JDK 1.8和gradle 6.5+  
1.安装并运行 mysql，执行db-schemas中所有sql文件，初始化数据库  
2.安装并运行 redis 不要设置密码  
3.安装并运行 rocket mq  至少运行一个naming server和一个broker  
4.在password-service, product-service, trade-service这三个子模块的src/main/resources下新建application-dev.yml文件，并做好配置。也可以直接修改application.yml。
``` yaml
spring:
  datasource:
    # 配置mysql地址，用户名，密码
    url: jdbc:mysql://localhost:3306/trade?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: your name
    password: your password
  redis:
    # 配置redis地址
    host: localhost
    port: 6379

rocketmq:
  # 配置rocket mq naming server地址
  name-server: localhost:9876
```
然后执行以下命令，编译并运行各个微服务
```
cd ikeyit-services
gradle bootRun --parallel
```
运行成功后浏览器打开  
http://localhost:9001/post/1  
就可以看到返回json了  
此项目为后端，仅提供REST API!没有图形界面!  
管理后台请移步 https://github.com/ikeyit/ikeyit-management-web 
## 演示系统
https://ikeyit.xyz
## 技术支持  
如有问题请开issue。
