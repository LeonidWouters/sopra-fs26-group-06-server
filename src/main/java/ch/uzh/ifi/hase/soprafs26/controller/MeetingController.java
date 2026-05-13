package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Meeting;
import ch.uzh.ifi.hase.soprafs26.rest.dto.MeetingGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.MeetingPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.MeetingPutDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.MeetingService;
import ch.uzh.ifi.hase.soprafs26.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MeetingController {

    private final MeetingService meetingService;

    private final UserService userService;

    public MeetingController(MeetingService meetingService, UserService userService) {
        this.meetingService = meetingService;
        this.userService = userService;
    }

    @PostMapping("/meetings/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingGetDTO createMeeting(@RequestBody MeetingPostDTO meetingPostDTO, @RequestHeader(value = "token", required = true) String token, @PathVariable(required = true) String id) {
        if(!userService.token_auth(token, Long.parseLong(id))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Meeting meeting = meetingService.createMeeting(DTOMapper.INSTANCE.convertMeetingPostDTOtoEntity(meetingPostDTO));

        return DTOMapper.INSTANCE.convertEntitiyToMeetingGetDTO(meeting);
    }

    @GetMapping("/meetings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MeetingGetDTO> getAllMeetings(@RequestHeader(value = "token", required = true) String token, @PathVariable(value = "id", required = true) String Id) {
        if(!userService.token_auth(token, Long.parseLong(Id))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        List<Meeting> meetings = meetingService.getMeetings(Long.parseLong(Id));
        if(!(meetings == null || meetings.isEmpty())) {
            List<MeetingGetDTO> meetingGetDTOs = new ArrayList<>();
            for (Meeting meeting : meetings) {
                meetingGetDTOs.add(DTOMapper.INSTANCE.convertEntitiyToMeetingGetDTO(meeting));
            }
            return meetingGetDTOs;
        }
        else {
            return new ArrayList<>();
        }
    }

    @DeleteMapping("/meetings/{id}/{Id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteMeeting(@PathVariable String id,@RequestHeader(value = "token", required = true) String token, @PathVariable(required = true) String Id) {
        if(!userService.token_auth(token, Long.parseLong(Id))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        Meeting meeting = meetingService.getMeeting(Long.parseLong(id));
        meetingService.deleteMeeting(meeting);
        return "Meeting deleted successfully";
    }

    @PutMapping("/meetings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MeetingGetDTO updateMeeting(@RequestBody MeetingPutDTO meetingPutDTO,@PathVariable String id, @RequestHeader(value = "token", required = true) String token) {
        if(!userService.token_auth(token, Long.parseLong(id))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        Meeting old =  meetingService.getMeeting(meetingPutDTO.getId());
        Meeting current = DTOMapper.INSTANCE.convertMeetingPutDTOtoEntity(meetingPutDTO);
        current.setOwner(old.getOwner());
        current.setId(old.getId());
        meetingService.createMeeting(current);
        return DTOMapper.INSTANCE.convertEntitiyToMeetingGetDTO(current);
    }
}
