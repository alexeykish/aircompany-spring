package by.pvt.kish.aircompany.dao;

import by.pvt.kish.aircompany.dao.impl.UserDAO;
import by.pvt.kish.aircompany.enums.UserRole;
import by.pvt.kish.aircompany.enums.UserStatus;
import by.pvt.kish.aircompany.pojos.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * @author Kish Alexey
 */
@ContextConfiguration("/testDaoContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UserDAOTest {

    @Autowired
    private UserDAO userDao;

    private Long id;
    private User testUser;

    @Before
    public void setUp() throws Exception {
        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("Test");
        testUser.setLogin("login" + (int) (Math.random() * 100));
        testUser.setPassword("testPassword");
        testUser.setEmail("test@test.com");
        testUser.setRole(UserRole.ROLE_DISPATCHER);

        testUser = userDao.add(testUser);
        id = testUser.getUid();
    }

    @Test
    public void testAdd() throws Exception {
        assertNotNull("Add method failed: null", testUser);
        User addedUser = userDao.getById(id);
        assertEquals("Add method failed: wrong user", testUser, addedUser);
    }

    @Test
    public void testCheckLogin() throws Exception {
        assertFalse("CheckLogin method positive test failed", userDao.checkLogin(testUser.getLogin()));
        assertTrue("CheckLogin method negative test failed", userDao.checkLogin("wrongLogin"));
    }

    @Test
    public void testGetUser() throws Exception {
        User receivedUser = userDao.getUser(testUser.getLogin(), testUser.getPassword());
        assertEquals("Get method failed: wrong user", testUser, receivedUser);
        User wrongReceivedUser = userDao.getUser("wrongLogin", "wrongPassword");
        assertNull("Get method failed: wrong user", wrongReceivedUser);
    }

    @Test
    public void testGetAll() throws Exception {
        int count = userDao.getAll().size();
        int countFact = userDao.getCount();
        assertEquals("Get all method failed", count, countFact);

    }

    @Test
    public void testDelete() throws Exception {
        userDao.delete(id);
        assertNull("Delete user: failed", userDao.getById(id));
    }
}