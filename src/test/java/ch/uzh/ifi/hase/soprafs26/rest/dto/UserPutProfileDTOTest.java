package ch.uzh.ifi.hase.soprafs26.rest.dto;

import ch.uzh.ifi.hase.soprafs26.constant.DisabilityStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserPutProfileDTOTest {

    @Test
    public void testUserPutProfileDTO() {
        UserPutProfileDTO userPutProfileDTO = new UserPutProfileDTO();
        userPutProfileDTO.setUsername("testUsername");
        userPutProfileDTO.setBio("testBio");
        userPutProfileDTO.setDisabilityStatus(DisabilityStatus.HEARING);

        assertEquals("testUsername", userPutProfileDTO.getUsername());
        assertEquals("testBio", userPutProfileDTO.getBio());
        assertEquals(DisabilityStatus.HEARING, userPutProfileDTO.getDisabilityStatus());
    }
}
