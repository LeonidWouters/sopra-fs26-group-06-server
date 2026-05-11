package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.UserStats;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserStatsGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.UserStatsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserStatsController {

    private final UserStatsService userStatsService;
    private final UserRepository userRepository;

    UserStatsController(UserStatsService userStatsService, UserRepository userRepository) {
        this.userStatsService = userStatsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/users/{id}/stats")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserStatsGetDTO getUserStats(@PathVariable Long id, @RequestHeader("token") String token) {
        if (userRepository.findByToken(token) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
        UserStats stats = userStatsService.getStats(id);
        return DTOMapper.INSTANCE.convertEntityToUserStatsGetDTO(stats);
    }
}
