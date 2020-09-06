# Code Generator

用于从数据库生成实体类,从mysql中生成postgres的脚本等

## Getting Started

### Start the application
mvn clean package

java -jar target/code-generator*.jar

 
### Create a dataSource by swagger
浏览器打开下面地址通过swagger创建数据

http://localhost:8080/swagger-ui.html#/data-source-controller

### export 

#### export entities 

http://localhost:8080/{dataSourceId}:export-entity?schema=

#### export mysql to postgres ddl


http://localhost:8080/{dataSourceId}:mysql-to-postgres-ddl?schema=

