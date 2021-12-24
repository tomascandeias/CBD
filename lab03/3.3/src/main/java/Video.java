
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import java.sql.ResultSetMetaData;
import java.util.Scanner;

public class Video {
    public static void main(String[] args) {
        Session session = Cluster.builder().addContactPoint("127.0.0.1").withPort(9042).build().connect("sistema_videos");

        //inserção
        System.out.println("\nInserção");
        session.execute("INSERT INTO utilizadores(username, nome, email, data_registo) VALUES('test123', 'Teste', 'teste@gmail.com', '2021-12-1T12:59');");

        ResultSet resultSet = session.execute("SELECT * FROM utilizadores WHERE username='test123';");
        System.out.println(resultSet.all());


        //edição
        System.out.println("\nEdição");
        session.execute("UPDATE utilizadores SET nome='Sr. Teste' WHERE username='test123';");

        resultSet = session.execute("SELECT * FROM utilizadores WHERE username='test123';");
        System.out.println(resultSet.all());

        //pesquisa
        System.out.println("\nPesquisa");
        resultSet = session.execute("SELECT * FROM utilizadores WHERE username='tomas123'");
        System.out.println(resultSet.all());

        //d.1
        System.out.println("\nOs últimos 3 comentários introduzidos para um vídeo;");
        resultSet = session.execute("select * from comentario where video_id=1 limit 3;");
        System.out.println(resultSet.all());

        //c.10
        System.out.println("\nPermitir a pesquisa do rating médio de um vídeo e quantas vezes foi votado;");
        resultSet = session.execute("select avg(rating) as avg_rating, count(rating_id) as votes from ratings where video_id=1;");
        System.out.println(resultSet.all());

        //d.4
        System.out.println("\nOs últimos 5 eventos de determinado vídeo realizados por um utilizador;");
        resultSet = session.execute("select * from eventos where video_id=6 and username='catarina98' limit 5;");
        System.out.println(resultSet.all());

        //d.7
        System.out.println("\nTodos os seguidores (followers) de determinado vídeo;");
        resultSet = session.execute("select * from followers where video_id=1;");
        System.out.println(resultSet.all());


    }
}
