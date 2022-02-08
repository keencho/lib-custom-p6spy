# custom-p6spy

- 사이드 프로젝트에 생성시 그때그때 설정하기 귀찮아서 만든 custom library  
- spring boot, spring data jpa 혹은 hibernate 사용시 유용
- [spring-boot-starter-p6spy](https://github.com/gavlyukovskiy/spring-boot-data-source-decorator) 를 바탕으로 한다.  
- jitpack 에 배포되어있으므로 jdk 8을 기반으로 한다.

## 의존성 추가  

### maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

...

<dependency>
    <groupId>com.github.keencho</groupId>
    <artifactId>lib-custom-p6spy</artifactId>
    <version>1.0.1</version>
</dependency>
```

### gradle
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

...

implementation 'com.github.keencho:lib-custom-p6spy:1.0.1'
```  

## 사용법

### @ComponentScan 
원래 사용법대로 라면 @SpringBootApplication 어노테이션이 선언된 클래스의 하위 클래스에 P6spyConfig.java 클래스가 위치해 있어야 겠지만, 이는 그렇지 않다.  

따라서 @ComponentScan 으로 base 를 지정해 줘야 한다.

```java
@SpringBootApplication
@ComponentScan(
        basePackages = { "foo.bar.*" },
        basePackageClasses = { CustomP6spyConfig.class }
)
public class SpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }
}
```  

위와같이 자신의 베이스 패키지가 foo.bar.* 이라면 이를 basePackages 로 지정해준다. 두번째로는 CustomP6spyConfig.class 를 basePackageClasses 로 지정해준다.  

### Properties
```java
@Component
@ConfigurationProperties(
    prefix = "keencho.p6spy"
)
public class CustomP6spyProperties {
    protected static String startPackage = "";
    protected static int limitStackTrace = 10;
    protected static String[] excludePackages = null;

    public CustomP6spyProperties() {
    }

    public void setStartPackage(String startPackage) {
        CustomP6spyProperties.startPackage = startPackage;
    }

    public void setLimitStackTrace(int limitStackTrace) {
        CustomP6spyProperties.limitStackTrace = limitStackTrace;
    }

    public void setExcludePackages(String[] excludePackages) {
        CustomP6spyProperties.excludePackages = excludePackages;
    }
}
```

```ymal
keencho:
  p6spy:
    start_package: foo.bar
    limit_stack_trace: 10
    exclude_packages: foo.bar, bar.baz
```  

1. start_package 는 자신의 베이스 패키지
2. limit_stack_trace 는 최대로 보여질 stackTrace 의 갯수
3. exclude_packages 는 로그에서 제외할 패키지  

## 주의사항  
**굉장히** 비싼 자원을 사용한다. 따라서 로컬 혹은 개발 환경에서만 사용해야 한다. 운영 환경에서 로깅을 비활성화 하는 방법은 다음과 같다.

```ymal
decorator:
  datasource:
    p6spy:
      enable-logging: false // 주의! 기본은 true다.
``` 


