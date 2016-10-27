package facades;

import security.IUserFacade;
import entity.Users;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import security.IUser;
import security.PasswordStorage;

public class UserFacade implements IUserFacade {

    /*When implementing your own database for this seed, you should NOT touch any of the classes in the security folder
    Make sure your new facade implements IUserFacade and keeps the name UserFacade, and that your Entity Users class implements 
    IUser interface, then security should work "out of the box" with users and roles stored in your database */
    EntityManagerFactory emf;

    public UserFacade() {
        addEntityManagerFactory(Persistence.createEntityManagerFactory("seedDB"));
        //Test Users
        EntityManager em = emf.createEntityManager();
        try {

            Users member = new Users();
            member.setUserName("Kasper");
            member.setPassword(PasswordStorage.createHash("Vetter"));
            member.addRole("Member");
            Users user = new Users();
            user.setUserName("user");
            user.setPassword(PasswordStorage.createHash("test"));
            user.addRole("User");
            Users admin = new Users();
            admin.setUserName("admin");
            admin.setPassword(PasswordStorage.createHash("test"));
            admin.addRole("Admin");
            Users both = new Users();
            both.setUserName("user_admin");
            both.setPassword(PasswordStorage.createHash("test"));
            both.addRole("User");
            both.addRole("Admin");
            em.getTransaction().begin();
            em.persist(member);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            em.getTransaction().commit();
        } catch (Exception e) {

        } finally {
            em.close();
        }

    }

    public void addEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /*
  Return the Roles if users could be authenticated, otherwise null
     */
    @Override
    public List<String> authenticateUser(String userName, String password) {

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Users> q = em.createQuery("SELECT u FROM Users u WHERE u.userName=:username", Users.class
            );
            q.setParameter("username", userName);
            Users user = q.getSingleResult();
            if (PasswordStorage.verifyPassword(password, user.getPassword())){
                return q.getSingleResult().getRoles();
            }
            
            
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            Logger.getLogger(UserFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PasswordStorage.InvalidHashException ex) {
            Logger.getLogger(UserFacade.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        return null;
    }

    @Override
    public IUser getUserByUserId(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
