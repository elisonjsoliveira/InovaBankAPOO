package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    // Fábrica, é o objeto que gerencia as operações do banco, como o CRUD, transações e consultas, como se fosse a conexão com o banco.
    private static EntityManagerFactory emf;

    public static void init(String user, String password) {
        Map<String, String> configOverrides = new HashMap<>();
        configOverrides.put("jakarta.persistence.jdbc.user", user);
        configOverrides.put("jakarta.persistence.jdbc.password", password);

        emf = Persistence.createEntityManagerFactory("inovabank", configOverrides);
    }

    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("JPAUtil não foi inicializado. Chame init(user, password) primeiro.");
        }
        return emf.createEntityManager();
    }

    // NOVO MÉTODO: Para fechar a fábrica de entidades
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("EntityManagerFactory closed.");
        }
    }
}
