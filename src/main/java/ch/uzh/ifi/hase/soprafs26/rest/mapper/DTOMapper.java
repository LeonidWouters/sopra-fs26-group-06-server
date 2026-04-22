package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.entity.Note;
import ch.uzh.ifi.hase.soprafs26.entity.Transcript;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "token", ignore = true)
	@Mapping(target = "status", source = "status")
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(source = "username", target = "username")
    @Mapping(source = "name", target = "name")
	@Mapping(source = "password", target = "password")
	@Mapping(source = "bio", target = "bio")
	@Mapping(source = "disabilityStatus", target = "disabilityStatus")
	User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "username", target = "username")
    @Mapping(source = "name", target = "name")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "bio", target = "bio")
	@Mapping(source = "creationDate", target = "creationDate")
	@Mapping(source = "disabilityStatus", target = "disabilityStatus")
	@Mapping(source = "pendingFriendRequests", target = "pendingFriendRequests")
	@Mapping(target = "friendCount", ignore = true)
    @Mapping(source = "friends", target = "friends")
	UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserLoginDTOtoEntity(UserLoginDTO userLoginDTO);

    @Mapping(source = "bio", target = "bio")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "id", target = "id")
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "username", target = "username")
    UserLoginDTO converEntityToUserLoginDTO(User user);

    @Mapping(source = "password", target = "password")
    User convertUserPutPasswordDTOtoEntity(UserPutPasswordDTO userPutDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "bio", target = "bio")
    @Mapping(source = "disabilityStatus", target = "disabilityStatus")
    User convertUserProfileDTOtoEntity(UserPutProfileDTO userPutProfileDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "content", target = "content")
    @Mapping(source = "sessionId", target = "sessionId")
    Note convertNotePostDTOtoEntity(NotePostDTO notePostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "sessionId", target = "sessionId")
    NoteGetDTO convertEntityToNoteGetDTO(Note note);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "content", target = "content")
    @Mapping(source = "sessionId", target = "sessionId")
    Transcript convertTranscriptPostDTOtoEntity(TranscriptPostDTO transcriptPostDTO);

    // Keep this ready for later if transcript updates are enabled again.
    // @Mapping(source = "content", target = "content")
    // Transcript convertTranscriptPutDTOtoEntity(TranscriptPutDTO transcriptPutDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "sessionId", target = "sessionId")
    TranscriptGetDTO convertEntityToTranscriptGetDTO(Transcript transcript);

}
