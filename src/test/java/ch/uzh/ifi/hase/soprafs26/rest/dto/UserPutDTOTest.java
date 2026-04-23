package ch.uzh.ifi.hase.soprafs26.rest.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserPutDTOTest {

    @Test
    public void testUserPutDTO() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setPassword("testPassword");
        userPutDTO.setUsername("testUsername");
        userPutDTO.setName("testName");
        userPutDTO.setDisabilityStatus("testDisabilityStatus");

        assertEquals("testPassword", userPutDTO.getPassword());
        assertEquals("testUsername", userPutDTO.getUsername());
        assertEquals("testName", userPutDTO.getName());
        assertEquals("testDisabilityStatus", userPutDTO.getDisabilityStatus());
    }
}
