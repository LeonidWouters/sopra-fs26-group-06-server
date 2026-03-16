package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;

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
	@Mapping(source = "password", target = "password")
	@Mapping(source = "bio", target = "bio")
	@Mapping(source = "disabilityStatus", target = "disabilityStatus")
	User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "username", target = "username")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "bio", target = "bio")
	@Mapping(source = "creationDate", target = "creationDate")
	@Mapping(source = "disabilityStatus", target = "disabilityStatus")
	UserGetDTO convertEntityToUserGetDTO(User user);

	@Mapping(target = "id", source ="id")
	@Mapping(target = "token", source = "token")
	@Mapping(target = "status", source = "status")
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(source = "username", target = "username")
	@Mapping(source = "password", target = "password")
	User convertUserLoginDTOtoEntity(UserLoginDTO userLoginDTO);

	@Mapping(source = "bio", target = "bio")
	@Mapping(source = "token",target = "token")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "id", target = "id")
	@Mapping(target = "password", ignore = true)
	@Mapping(source = "username", target = "username")
	UserLoginDTO converEntityToUserLoginDTO(User user);

	@Mapping(source = "password", target = "password")
	User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);
}
