package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        checkIfUserExists(newUser);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User checkUser(User loginUser) {
        authenticate(loginUser);
        User user = userRepository.findByUsername(loginUser.getUsername());
        user.setToken(UUID.randomUUID().toString());
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
        return user;
    }

    public User getByID(long id) {
        User user = userRepository.findByid(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }


        return user;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(baseErrorMessage, "username", "is"));
        }
    }

    private void authenticate(User userLogin) {
        User findUsername = userRepository.findByUsername(userLogin.getUsername());

        String baseErrorMessage = "The %s provided %s not correct!";
        if (findUsername == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "This User does not exist!");
        }

        if (!findUsername.getPassword().equals(userLogin.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Password!");
        }

    }

    public boolean token_auth(String token, long id) {
        String userToken = token;
        String databaseToken = userRepository.findByid(id).getToken();

        String baseError = "invalid token";


        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is null");
        }
        else if (!userToken.equals(databaseToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, baseError);
        }
        return true;
    }

    public void sendFriendRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findByid(senderId);
        User receiver = userRepository.findByid(receiverId);

        if (sender == null || receiver == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (senderId.equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cant send friend request to yourself");
        }
        if (receiver.getFriends().contains(senderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already friends");
        }
        if (receiver.getPendingFriendRequests().contains(senderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Friend request already sent");
        }

        receiver.getPendingFriendRequests().add(senderId);
        userRepository.save(receiver);
    }

    public void acceptFriendRequest(Long userId, Long senderId) {
        User user = userRepository.findByid(userId);
        User sender = userRepository.findByid(senderId);

        if (user == null || sender == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!user.getPendingFriendRequests().contains(senderId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pending request from this user");
        }

        user.getPendingFriendRequests().remove(Long.valueOf(senderId));
        user.getFriends().add(senderId);
        sender.getFriends().add(userId);

        userRepository.save(user);
        userRepository.save(sender);
    }

    public List<User> getFriends(Long userId) {
        User user = userRepository.findByid(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<User> friendList = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            User friend = userRepository.findByid(friendId);
            if (friend != null) {
                friendList.add(friend);
            }
        }
        return friendList;
    }

    public void declineFriendRequest(Long userId, Long senderId) {
        User user = userRepository.findByid(userId);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        if (!user.getPendingFriendRequests().contains(senderId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pending request from this user");
        }
        user.getPendingFriendRequests().remove(Long.valueOf(senderId));
        userRepository.save(user);
    }

    public List<User> getPendingRequests(Long userId) {
        User user = userRepository.findByid(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<User> requesters = new ArrayList<>();
        for (Long requesterId : user.getPendingFriendRequests()) {
            User requester = userRepository.findByid(requesterId);
            if (requester != null) {
                requesters.add(requester);
            }
        }
        return requesters;
    }
}
