<div><img src="public/banner_logo.png" alt="CommunicALL Banner Logo" width="15%"></div>

# CommunicALL

> Note that this app was developed in about a semesters length, since this is a student project for a course at the University of Zurich. There's still a lot of rough edges in this project.

CommunicALL is our attempt at an accessibility focused video call client.
It allows for p2p video calling and supports a range of accessibility features, currently focused on deaf or hard of hearing users. These include text-to-speech, speech-to-text for hearing/deaf sessions, a chat with automatic time stamped messages, as well as automated transcripts for later use after a call has ended. As a further collaborative feature, a collaborative MD editor is present.

# UI and Functionalities

---to be added after UI redesign---

# Technologies

- [React](https://react.dev/) - Frontend Framework
- [SpringBoot](https://spring.io/projects/spring-boot/) - Backend Framework
- [Gradle](https://gradle.org/) - Dependency management
- [WebSpeechAPI](https://developer.mozilla.org/en-US/docs/Web/API/Web_Speech_API) - STT/TTS support
- [WebRTC](webrtc.org/?hl=de) - P2P video streaming

# High-Level Components

1. **WebSocket Handler ([SocketsHandler.java](../sopra-fs26-group-06-server/src/main/java/ch/uzh/ifi/hase/soprafs26/sockets/SocketsHandler.java))**:
   This backend element routes real-time, bi-directional communication between the server and the clients. Besides handling joining and leaving logic for users in active rooms, it acts as the signaling channel for P2P connection setups and distributes payload events such as live chat messages, STT transcripts, and collaborative markdown updates to peers.

2. **Main Dashboard ([app/mainpage/page.tsx](../sopra-fs26-group-06-client/app/mainpage/page.tsx))**:
   Acting as the central dashboard after logging in, this component gives users a comprehensive overview of joinable rooms, their statistics, and user search. It dynamically manages UI state for room invitations and enables users to easily create their own private rooms and invite friends.

3. **Meeting Rooms ([app/rooms/[id]/page.tsx](../sopra-fs26-group-06-client/app/rooms/[id]/page.tsx))**:
   The primary layout where active sessions take place. It handles the entire P2P WebRTC connection process, tracks ICE candidates, and hosts the collaborative markdown editor. Crucially, it manages `SpeechRecognition` (STT) for live spoken subtitles and `SpeechSynthesis` (TTS) to read incoming chats aloud, wrapping it all into one interface and preparing the transcripts to be saved upon exit.

4. **Transcripts & Notes Management ([TranscriptController.java](../sopra-fs26-group-06-server/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/TranscriptController.java) & [NoteController.java](../sopra-fs26-group-06-server/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/NoteController.java))**:
   These Spring Boot REST controllers expose endpoints (`POST /transcripts` and `POST /notes`) to securely persist all messages (Chat & STT) and markdown editor data after a call terminates. Upon creation, these endpoints append the generated `sessionId` into the `User` entity's session history, ensuring only authenticated participants can later retrieve their private meeting records.


# Getting Started

### Prerequisites :
- Node.js 17+
- React
- Spring Boot backend running locally or cloud server
- Chrome (not guaranteed to work on other browser or limited functionality)
***
### Installation:

**Start with the backend**
1. Clone the repo `git clone https://github.com/LeonidWouters/sopra-fs26-group-06-server.git`
2. Navigate to the project, run `./gradlew build`
3. Run server `./gradlew bootRun`
4. The server runs on port 8080, so visit [localhost://8080](localhost://8080). You should now see the following text: *"The application is running."*
5. To open the h2 database, visit [http://localhost:8080/h2-console/](http://localhost:8080/h2-console/)
6. If you want to run the tests, run `./gradlew test`

**If the backend is running, continue with the frontend**
1. Clone the repo
   `git clone https://github.com/LeonidWouters/sopra-fs26-group-06-client.git`
2. Navigate to the project, run
   `npm install`
3. Setup the dev environment if any changes are necessary
4. Run dev server
   `npm run dev`
5. The frontend runs on port 3000, so visit [localhost://3000](localhost://3000).


# Deployment

The backend is deployed to google cloud through the workflow file [sopra-fs26-group-06-server/.github/workflows/main.yml](../sopra-fs26-group-06-server/.github/workflows/main.yml). Pushing to main triggers the workflow, which creates a new build in the google cloud. The new docker image is then deployed in google cloud run.

The frontend is deployed to vercel through the workflow file in [../sopra-fs26-group-06-client/.github/workflows/verceldeployment.yml](../sopra-fs26-group-06-client/.github/workflows/verceldeployment.yml)``.
Pushing to main triggers the workflow, which releases a new build, please tag a new release as `v0.XX`.
The Vercel Deployment is configured via the vercel secrets in the repo.
Furthermore, a dockerbuild is automatically triggered. The container can be pulled from Dockerhub.

# Contributing

Contributions are welcome! If you find any part of our application useful, you may also integrate it into your own project, see the licensing section for more details.
For direct contributions, please follow the steps below:
1. If you are on Windows, please use WSL to run and develop the project
2. Fork the repository and create a new branch for your feature or bugfix, if you want to add a large feature, please create an issue first to discuss it with us
3. Make your changes and commit them with clear messages
4. Make a pull request to the main branch of this repository, describing your changes
5. Wait for a review and feedback

## Features
If you are looking for what to contribute, here are some ideas:
- Check the issues page for open problems
- Add a true auth system, instead of using uuid tokens
- Make accessibility settings independent of the session, so that a user can store their preferences
- Add more accessibility features, such as screen reader support, color blindness modes or even sign language recognition

# Authors and acknowledgment
| Name | GitHub | Primary Role |
| :--- | :--- | :--- |
| **Raffeal Bischoff** | [@RaffB05](https://github.com/Raff05B) | Backend & Frontend |
| **Leonid Wouters** | [@LeonidWouters](https://github.com/LeonidWouters) | Backend |
| **Silvan Müller** | [@somueller03](https://github.com/somueller03) | Frontend |
| **Tiago Caselas** | [@tcaselas](https://github.com/tcaselas) | Frontend |
| **Laurin Arpagaus** | [@laurin10](https://github.com/laurin10) | Frontend |

This project was developed during the Software Engineering Lab (SoPra) at the **University of Zurich**. We are grateful for the support of our tutor **Sergi Montmany** and the valuable feedback from the peer review groups.

# License

This project is licensed under different licenses depending on the component:

*   **Frontend:** [GNU GPL v3.0](../sopra-fs26-group-06-client/LICENSE.txt)
*   **Backend:** [Apache License 2.0](../sopra-fs26-group-06-server/LICENSE.txt)

For more details, please see the respective `LICENSE` files in the subdirectories.


