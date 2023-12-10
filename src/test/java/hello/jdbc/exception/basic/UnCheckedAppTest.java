package hello.jdbc.exception.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnCheckedAppTest {

    @DisplayName("")
    @Test
    void unchecked() {
        Controller controller = new Controller();

        assertThatThrownBy(controller::request)
                .isInstanceOfAny(RuntimeSQLException.class, RuntimeConnectException.class);
    }

    static class Controller {
        Service service = new Service();
        public void request() {
            service.call();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void call(){
            repository.call();
            networkClient.call();
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
               throw new RuntimeSQLException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException() {
        }

        public RuntimeConnectException(String message) {
            super(message);
        }

        public RuntimeConnectException(Throwable cause) {
            super(cause);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
