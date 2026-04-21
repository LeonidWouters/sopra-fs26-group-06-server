package ch.uzh.ifi.hase.soprafs26.room;


public class Room {


    private Long id;

    private String name;

    private String description;

    private RoomStatus roomStatus;

    private Long CallerID;

    private Long CalleeID;

    private String baseTranscript;

    private String baseNote;

    private boolean isPrivate = false;

    private Long creatorId;

    private Long invitedUserId;


    public static Room createRoom(long id,String name,String description) {
        Room room = new Room();
        room.setId(id);
        room.setName(name);
        room.setDescription(description);
        room.setRoomStatus(RoomStatus.EMPTY);
        room.setBaseTranscript("");
        room.setBaseNote("");
        return room;
    }

    public static Room createPrivateRoom(long id, Long creatorId, String name, String description) {
        Room room = new Room();
        room.setId(id);
        room.setName(name);
        room.setDescription(description);
        room.setRoomStatus(RoomStatus.EMPTY);
        room.setBaseTranscript("");
        room.setBaseNote("");
        room.setPrivate(true);
        room.setCreatorId(creatorId);
        return room;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {this.id = id;}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Long getCallerID() {
        return CallerID;
    }

    public void setCallerID(Long callerID) {
        CallerID = callerID;
    }

    public Long getCalleeID() {
        return CalleeID;
    }

    public void setCalleeID(Long calleeID) {
        CalleeID = calleeID;
    }

    public String getBaseTranscript() {
        return baseTranscript;
    }

    public void setBaseTranscript(String baseTranscript) {
        this.baseTranscript = baseTranscript;
    }

    public String getBaseNote() {
        return baseNote;
    }

    public void setBaseNote(String baseNote) {
        this.baseNote = baseNote;
    }

    public boolean getIsPrivate() { return isPrivate; }

    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    public Long getCreatorId() { return creatorId; }

    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public Long getInvitedUserId() { return invitedUserId; }

    public void setInvitedUserId(Long invitedUserId) { this.invitedUserId = invitedUserId; }
}
