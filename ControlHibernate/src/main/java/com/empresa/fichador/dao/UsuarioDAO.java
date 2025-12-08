package com.empresa.fichador.dao;

import com.empresa.fichador.model.Usuario;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class UsuarioDAO extends GenericDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Usuario findByUsernameAndPassword(String username, String password) {
        try (Session session = getSession()) {
            Query<Usuario> query = session.createQuery(
                    "FROM Usuario WHERE username = :username AND password = :password AND activo = true",
                    Usuario.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Usuario findByUsername(String username) {
        try (Session session = getSession()) {
            Query<Usuario> query = session.createQuery(
                    "FROM Usuario WHERE username = :username",
                    Usuario.class
            );
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Usuario> findByRol(Usuario.Rol rol) {
        try (Session session = getSession()) {
            Query<Usuario> query = session.createQuery(
                    "FROM Usuario WHERE rol = :rol AND activo = true",
                    Usuario.class
            );
            query.setParameter("rol", rol);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void actualizarUltimoAcceso(Usuario usuario) {
        try (Session session = getSession()) {
            session.beginTransaction();
            usuario.setUltimoAcceso(java.time.LocalDateTime.now());
            session.merge(usuario);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

