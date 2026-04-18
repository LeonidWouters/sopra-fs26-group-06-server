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
}
