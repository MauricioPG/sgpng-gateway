# SGPNG GATEWAY

* Nota: Não é possível integrar o projeto Gateway com o projetos que tenham
  a dependência do spring-boot-starter-web, pois o gateway é incompatível
  com o MVC, como mostra a mensagem abaixo. Mesmo colocando no POM a exclusão
  da dependência referida, não funciona.
  
  ***************************
  APPLICATION FAILED TO START
  ***************************
  Description:
  Spring MVC found on classpath, which is incompatible with Spring Cloud Gateway.
  Action:
  Please set spring.main.web-application-type=reactive or remove spring-boot-starter-web dependency.
  
* Outro erro devido aos outros projetos, mas esse é reparável colocando a linha sugerida

 ***************************
 APPLICATION FAILED TO START
 ***************************
 Description:
 The bean 'resourceExceptionHandler', defined in class path resource 
 [comgep/sigpesng/gateway/config/AppConfig.class], could not be registered. 
 A bean with that name has already been defined in file 
 [...\comgep\sgpng-gateway\target\classes\comgep\sigpesng\gateway\exception
esourceExceptionHandler.class]
 and overriding is disabled.
 Action:
 Consider renaming one of the beans or enabling overriding by setting 
 spring.main.allow-bean-definition-overriding=true  


  
  