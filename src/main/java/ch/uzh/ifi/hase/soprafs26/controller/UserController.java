package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers(@RequestHeader("token") String token) {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        User userToken = userRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
        if (users == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Users present in database");
        }

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable String id, @RequestHeader("token") String token) {
        User user = userService.getByID(Long.parseLong(id));
        User userToken = userRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @GetMapping("/users/{id}/verifier")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean verifyUser(@PathVariable String id, @RequestHeader("token") String token) {

        boolean res = userService.token_auth(token, Long.parseLong(id));

        return res;

    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserLoginDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation

        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        // create user
        User createdUser = userService.createUser(userInput);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.converEntityToUserLoginDTO(createdUser);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserLoginDTO auth(@RequestBody UserLoginDTO userLoginDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserLoginDTOtoEntity(userLoginDTO);
        //Convert User Input to login instance to check for correct password
        User message = userService.checkUser(userInput);


        return DTOMapper.INSTANCE.converEntityToUserLoginDTO(message);

    }

	@PutMapping("users/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	public UserPutDTO changePassword(@RequestBody UserPutDTO userPutDTO, @RequestHeader("token") String token) {
		User user = userRepository.findByToken(token);
		if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		user.setPassword(userPutDTO.getPassword());
		if (userPutDTO.getFirstname() != null) {
			user.setFirstname(userPutDTO.getFirstname());
		}
		if (userPutDTO.getLastname() != null) {
			user.setLastname(userPutDTO.getLastname());
		}
		userRepository.save(user);
		return userPutDTO;
	}

	@PutMapping("users/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	public void logout(@RequestHeader("token") String token) {
		User user = userRepository.findByToken(token);
		if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		user.setToken(UUID.randomUUID().toString());
		user.setStatus(UserStatus.OFFLINE);
		userRepository.save(user);
	}
}
