package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

public class CheckedAppTest {

    @DisplayName("")
    @Test
    void checked() {
        Controller controller = new Controller();

        assertThatThrownBy(controller::request)
                .isInstanceOfAny(SQLException.class, ConnectException.class);
    }

    static class Controller {
        Service service = new Service();
        public void request() throws SQLException, ConnectException {
            service.call();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void call() throws SQLException, ConnectException{
            repository.call();
            networkClient.call();
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("SQL 에러");
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }
}
