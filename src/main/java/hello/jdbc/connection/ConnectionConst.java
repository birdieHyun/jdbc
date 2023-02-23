package hello.jdbc.connection;

public abstract class ConnectionConst {  // 상수를 선언한 것들이기 때문에 생성을 못하게 abstract class 로 만든다.
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
