package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Note;
import ch.uzh.ifi.hase.soprafs26.entity.Transcript;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.room.Room;
import ch.uzh.ifi.hase.soprafs26.room.RoomStatus;
import ch.uzh.ifi.hase.soprafs26.room.RoomService;
import ch.uzh.ifi.hase.soprafs26.service.NoteService;
import ch.uzh.ifi.hase.soprafs26.service.TranscriptService;
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
    private final NoteService noteService;
    private final TranscriptService transcriptService;
    private final UserRepository userRepository;

    private final RoomService roomService;

    UserController(UserService userService, NoteService noteService, TranscriptService transcriptService,
                   UserRepository userRepository, RoomService roomService) {
        this.userService = userService;
        this.noteService = noteService;
        this.transcriptService = transcriptService;
        this.userRepository = userRepository;
        this.roomService = roomService;
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

    @GetMapping("/users/{id}/documents")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserDocumentsGetDTO getAllDocumentsForUser(@PathVariable String id, @RequestHeader("token") String token) {
        User user = userService.getByID(Long.parseLong(id));
        User userToken = userRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }

        List<UUID> sessions = user.getSessions();
        List<TranscriptGetDTO> transcriptGetDTOs = new ArrayList<>();
        for (Transcript transcript : transcriptService.getTranscriptsBySessionIds(sessions)) {
            transcriptGetDTOs.add(DTOMapper.INSTANCE.convertEntityToTranscriptGetDTO(transcript));
        }

        List<NoteGetDTO> noteGetDTOs = new ArrayList<>();
        for (Note note : noteService.getNotesBySessionIds(sessions)) {
            noteGetDTOs.add(DTOMapper.INSTANCE.convertEntityToNoteGetDTO(note));
        }

        UserDocumentsGetDTO response = new UserDocumentsGetDTO();
        response.setTranscripts(transcriptGetDTOs);
        response.setNotes(noteGetDTOs);
        return response;
    }

    @GetMapping("/users/{id}/transcripts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TranscriptGetDTO> getAllTranscriptsForUser(@PathVariable String id,
                                                           @RequestHeader("token") String token) {
        User user = userService.getByID(Long.parseLong(id));
        User userToken = userRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }

        List<UUID> sessions = user.getSessions();
        List<TranscriptGetDTO> transcriptGetDTOs = new ArrayList<>();
        for (Transcript transcript : transcriptService.getTranscriptsBySessionIds(sessions)) {
            transcriptGetDTOs.add(DTOMapper.INSTANCE.convertEntityToTranscriptGetDTO(transcript));
        }
        return transcriptGetDTOs;
    }

    @GetMapping("/users/{id}/notes")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<NoteGetDTO> getAllNotesForUser(@PathVariable String id, @RequestHeader("token") String token) {
        User user = userService.getByID(Long.parseLong(id));
        User userToken = userRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }

        List<UUID> sessions = user.getSessions();
        List<NoteGetDTO> noteGetDTOs = new ArrayList<>();
        for (Note note : noteService.getNotesBySessionIds(sessions)) {
            noteGetDTOs.add(DTOMapper.INSTANCE.convertEntityToNoteGetDTO(note));
        }
        return noteGetDTOs;
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

	@PutMapping("users/{id}/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	public void changePassword(@PathVariable Long id, @RequestBody UserPutPasswordDTO userPutPasswordDTO, @RequestHeader("token") String token) {
		User user = userRepository.findByToken(token);
		if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		String newPassword = userPutPasswordDTO.getPassword();
		if (newPassword == null || newPassword.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must not be empty");
		user.setPassword(newPassword);
		userRepository.save(user);
	}

	@PutMapping("users/{id}/profile")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	public void updateProfile(@PathVariable Long id, @RequestBody UserPutProfileDTO userPutProfileDTO, @RequestHeader("token") String token) {
		User user = userRepository.findByToken(token);
		if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		if (userPutProfileDTO.getUsername() != null) user.setUsername(userPutProfileDTO.getUsername());
		if (userPutProfileDTO.getBio() != null) user.setBio(userPutProfileDTO.getBio());
		if (userPutProfileDTO.getDisabilityStatus() != null) user.setDisabilityStatus(userPutProfileDTO.getDisabilityStatus());
		userRepository.save(user);
	}

	@PostMapping("users/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	public void logout(@RequestHeader(value = "token", required = false) String token, @RequestParam(value = "token", required = false) String tokenParam) {
		String finalToken = token != null ? token : tokenParam;
		if (finalToken == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not provided");
		}
		User user = userRepository.findByToken(finalToken);
		if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

		if (user.getRoomId() != null) {
			Room room = roomService.getRoomById(user.getRoomId().toString());
			if (room != null) {
                persistRoomArtifactsForUser(user, room);

				if (room.getCallerID() != null && room.getCallerID().equals(user.getId())) {
					room.setCallerID(null);
				}
				if (room.getCalleeID() != null && room.getCalleeID().equals(user.getId())) {
					room.setCalleeID(null);
				}

                if (room.getCallerID() == null && room.getCalleeID() == null) {
                    room.setRoomStatus(RoomStatus.EMPTY);
                    room.setBaseTranscript("");
                    room.setBaseNote("");
                }
                else if (room.getCallerID() == null || room.getCalleeID() == null) {
                    room.setRoomStatus(RoomStatus.JOINABLE);
                }
                else {
                    room.setRoomStatus(RoomStatus.FULL);
                }
			}
			user.setRoomId(null);
		}

		user.setToken(UUID.randomUUID().toString());
		user.setStatus(UserStatus.OFFLINE);
		userRepository.save(user);
	}

    private void persistRoomArtifactsForUser(User user, Room room) {
        String noteContent = room.getBaseNote() == null ? "" : room.getBaseNote().trim();
        String transcriptContent = room.getBaseTranscript() == null ? "" : room.getBaseTranscript().trim();

        if (noteContent.isBlank() && transcriptContent.isBlank()) {
            return;
        }

        UUID sessionId = UUID.randomUUID();

        if (!noteContent.isBlank()) {
            Note note = new Note();
            note.setContent(noteContent);
            note.setSessionId(sessionId);
            noteService.createNote(note);
        }

        if (!transcriptContent.isBlank()) {
            Transcript transcript = new Transcript();
            transcript.setContent(transcriptContent);
            transcript.setSessionId(sessionId);
            transcriptService.createTranscript(transcript);
        }

        if (user.getSessions() == null) {
            user.setSessions(new ArrayList<>());
        }

        if (!user.getSessions().contains(sessionId)) {
            user.getSessions().add(sessionId);
        }
    }
}
