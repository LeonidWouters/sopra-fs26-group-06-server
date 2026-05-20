package ch.uzh.ifi.hase.soprafs26.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		// given
		testUser = new User();
		testUser.setId(1L);
		testUser.setPassword("testName");
		testUser.setUsername("testUsername");
		testUser.setName("First Last");
		testUser.setStatus(UserStatus.ONLINE);

		// when -> any object is being save in the userRepository -> return the dummy
		// testUser
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
	}

	@Test
	public void createUser_validInputs_success() {
		// when -> any object is being save in the userRepository -> return the dummy
		// testUser
		User createdUser = userService.createUser(testUser);

		// then
		Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

		assertEquals(testUser.getId(), createdUser.getId());
		assertEquals(testUser.getPassword(), createdUser.getPassword());
		assertEquals(testUser.getUsername(), createdUser.getUsername());
		assertNotNull(createdUser.getToken());
		assertEquals(UserStatus.ONLINE, createdUser.getStatus());
	}

	@Test
	public void createUser_duplicateUsername_throwsException() {
		// given -> a first user has already been created
		userService.createUser(testUser);

		// when -> setup additional mocks for UserRepository
		Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

		// then -> attempt to create second user with same user -> check that an error
		// is thrown
		assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
	}

	@Test
	public void createUser_duplicateInputs_throwsException() {
		// given -> a first user has already been created
		userService.createUser(testUser);

		// when -> setup additional mocks for UserRepository
		Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

                // then -> attempt to create second user with same user -> check that an error
                // is thrown
                assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
        }

        @Test
        public void checkUser_validCredentials_returnsUser() {
                Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
                User result = userService.checkUser(testUser);
                assertEquals(testUser.getId(), result.getId());
                assertEquals(testUser.getUsername(), result.getUsername());
        }

        @Test
        public void getByID_nonExistingUser_throwsException() {
                Mockito.when(userRepository.findByid(999L)).thenReturn(null);
                assertThrows(ResponseStatusException.class, () -> userService.getByID(999L));
        }

        @Test
        public void token_auth_validToken_returnsUser() {
                testUser.setToken("1234");
                Mockito.when(userRepository.findByid(testUser.getId())).thenReturn(testUser);
                boolean result = userService.token_auth("1234", testUser.getId());
                assertTrue(result);
        }

        @Test
        public void sendFriendRequest_alreadyFriends_throwsException() {
                User sender = new User();
                sender.setId(2L);
                testUser.setId(3L);
                
                testUser.setFriends(new java.util.ArrayList<>());
                testUser.getFriends().add(sender.getId());

                Mockito.when(userRepository.findByid(sender.getId())).thenReturn(sender);
                Mockito.when(userRepository.findByid(testUser.getId())).thenReturn(testUser);

                assertThrows(ResponseStatusException.class, () -> userService.sendFriendRequest(sender.getId(), testUser.getId()));
        }

        @Test
        public void acceptFriendRequest_noPendingRequest_throwsException() {
                User sender = new User();
                sender.setId(2L);
                testUser.setId(3L);
                
                testUser.setPendingFriendRequests(new java.util.ArrayList<>());

                Mockito.when(userRepository.findByid(sender.getId())).thenReturn(sender);
                Mockito.when(userRepository.findByid(testUser.getId())).thenReturn(testUser);

                assertThrows(ResponseStatusException.class, () -> userService.acceptFriendRequest(testUser.getId(), sender.getId()));
        }

        @Test
        public void checkUser_invalidPassword_throwsException() {
                // testUser exists in DB with specific password
                Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
                // user tries to login with wrong password
                User loginAttempt = new User();
                loginAttempt.setUsername(testUser.getUsername());
                loginAttempt.setPassword("wrongPassword123");
                //an UNAUTHORIZED exception is thrown
                assertThrows(ResponseStatusException.class, () -> userService.checkUser(loginAttempt));
        }

        @Test
        public void acceptFriendRequest_succesfullyAddsFriendsAndRemovesPending() {
                // two valid users where testUser has a pending friend request from sender
                User sender = new User();
                sender.setId(2L);
                sender.setFriends(new java.util.ArrayList<>());
                testUser.setId(3L);
                testUser.setFriends(new java.util.ArrayList<>());
                testUser.setPendingFriendRequests(new java.util.ArrayList<>(java.util.List.of(sender.getId())));
                Mockito.when(userRepository.findByid(sender.getId())).thenReturn(sender);
                Mockito.when(userRepository.findByid(testUser.getId())).thenReturn(testUser);
                // testUser accepts the friend request
                userService.acceptFriendRequest(testUser.getId(), sender.getId());
                // the request is removed and both are mutually added as friends
                assertFalse(testUser.getPendingFriendRequests().contains(sender.getId()));
                assertTrue(testUser.getFriends().contains(sender.getId()));
                assertTrue(sender.getFriends().contains(testUser.getId()));
                Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
                Mockito.verify(userRepository, Mockito.times(1)).save(sender);
        }

    @Test
    public void removeFriend_userNotFound_throwsException() {
        Mockito.when(userRepository.findByid(1L)).thenReturn(testUser);
        Mockito.when(userRepository.findByid(2L)).thenReturn(null);

        assertThrows(ResponseStatusException.class,
                () -> userService.removeFriend(1L, 2L));
    }

    @Test
    public void removeFriend_notFriends_throwsException() {
        testUser.setFriends(new ArrayList<>());

        User other = new User();
        other.setId(2L);
        other.setFriends(new ArrayList<>());

        Mockito.when(userRepository.findByid(1L)).thenReturn(testUser);
        Mockito.when(userRepository.findByid(2L)).thenReturn(other);

        assertThrows(ResponseStatusException.class,
                () -> userService.removeFriend(1L, 2L));
    }
    @Test
    public void getFriends_userNotFound_throwsException() {
        Mockito.when(userRepository.findByid(99L)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.getFriends(99L));

    }

    @Test
    public void getFriends_skipsDeletedFriends_returnsOnlyFound() {
        testUser.setFriends(new ArrayList<>(List.of(2L, 3L)));
        Mockito.when(userRepository.findByid(1L)).thenReturn(testUser);

        User validFriend = new User();
        validFriend.setId(2L);
        Mockito.when(userRepository.findByid(2L)).thenReturn(validFriend);
        Mockito.when(userRepository.findByid(3L)).thenReturn(null); // stale/deleted

        List<User> result = userService.getFriends(1L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    public void getFriends_noFriends_returnsEmptyList() {
        testUser.setFriends(new ArrayList<>());
        Mockito.when(userRepository.findByid(1L)).thenReturn(testUser);

        List<User> result = userService.getFriends(1L);

        assertTrue(result.isEmpty());
    }

    
}
