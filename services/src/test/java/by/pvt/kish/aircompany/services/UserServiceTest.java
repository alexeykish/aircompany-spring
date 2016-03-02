package by.pvt.kish.aircompany.services;

import by.pvt.kish.aircompany.enums.UserRole;
import by.pvt.kish.aircompany.exceptions.ServiceException;
import by.pvt.kish.aircompany.exceptions.ServiceLoginException;
import by.pvt.kish.aircompany.pojos.User;
import by.pvt.kish.aircompany.services.impl.UserService;
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
@ContextConfiguration("/testServiceContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

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

        testUser = userService.addUser(testUser);
        id = testUser.getUid();
    }

    @Test
    public void testAdd() throws Exception {
        assertNotNull("Add method failed: null", testUser);
        User addedUser = userService.getById(id);
        assertEquals("Add method failed: wrong user", testUser, addedUser);
    }

    @Test
    public void testCheckLogin() throws Exception {
        assertFalse("CheckLogin method positive test failed", userService.checkLogin(testUser.getLogin()));
        assertTrue("CheckLogin method negative test failed", userService.checkLogin("wrongLogin"));
    }

    @Test
    public void testGetUser() throws Exception {
        User receivedUser = userService.getUser(testUser.getLogin(), testUser.getPassword());
        assertEquals("Get method failed: wrong user", testUser, receivedUser);
    }

    @Test(expected = ServiceLoginException.class)
    public void testGetWrongUser() throws Exception {
        User wrongReceivedUser = userService.getUser("wrongLogin", "wrongPassword");
        assertNull("Get method failed: wrong user", wrongReceivedUser);
    }

    @Test
    public void testGetAll() throws Exception {
        int count = userService.getAll().size();
        int countFact = userService.getCount();
        assertEquals("Get all method failed", count, countFact);

    }

    @Test
    public void testDelete() throws Exception {
        userService.delete(id);
        assertNull("Delete user: failed", userService.getById(id));
    }

    @Test
    public void testGetUserByLogin() throws Exception {
        String login = testUser.getLogin();
        User receivedUser = userService.getByLogin(login);
        assertEquals("GetByLogin method failed: wrong user", testUser, receivedUser);
    }

    @Test(expected = ServiceException.class)
    public void testGetUserByWrongLogin() throws Exception {
        User wrongReceivedUser = userService.getByLogin("wrongLogin");
        assertNull("Get method failed: wrong user", wrongReceivedUser);
    }
}