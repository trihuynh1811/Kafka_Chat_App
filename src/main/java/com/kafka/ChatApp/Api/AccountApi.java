package com.kafka.ChatApp.Api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.kafka.ChatApp.Entity.Contact;
import com.kafka.ChatApp.Entity.GroupMember;
import com.kafka.ChatApp.Entity.Message_;
import com.kafka.ChatApp.Listener.WebSocketListener;
import com.kafka.ChatApp.Repository.AccountRepo;
import com.kafka.ChatApp.Repository.GroupMemberRepo;
import com.kafka.ChatApp.Repository.MessageRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class AccountApi {

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private GroupMemberRepo groupMemberRepo;
    @Autowired
    private MessageRepo messageRepo;

    String client_id = "1035634484726-2c0k79fr2n36uullqf0u3lodrd8rbnml.apps.googleusercontent.com";

    @PostMapping(value = "/api/contact/createaccount", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createAccount(@RequestBody JsonNode request) {
        try {
            String username = request.get("username").asText();
            String password = request.get("password").asText();
            String email = request.get("email").asText();
            String firstName = request.get("firstName").asText();
            String lastName = request.get("lastName").asText();

            String existedContact = accountRepo.findById(username).isPresent() ? "Username have been taken" : accountRepo.findByEmail(email) != null ? "Email have been taken" : "";

            if (existedContact.equals("")) {
                Contact contact = Contact.builder()
                        .username(username)
                        .displayName(username)
                        .password(password)
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .build();

                accountRepo.save(contact);
                System.out.println(contact);
                return ResponseEntity.status(HttpStatus.OK).body(contact);

            }
            System.out.println(accountRepo.findById(username));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed to create a new contact");
        } catch (Exception err) {
            System.out.println("error ↓");
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to create new contact");
        }
    }

    @PostMapping(value = "/api/contact/google-createaccount", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> googleCreateAccount(@RequestBody JsonNode request) {
        try {
            String username = request.get("username").asText();
            String password = request.get("password").asText();
            String email = request.get("email").asText();
            String firstName = request.get("firstName").asText();
            String lastName = request.get("lastName").asText();

            String existedContact = accountRepo.findById(username).isPresent() ? "Username have been taken" : accountRepo.findByEmail(email) != null ? "Email have been taken" : "";

            if (existedContact.equals("")) {
                Contact contact = Contact.builder()
                        .username(username)
                        .displayName(username)
                        .password(password)
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .build();

                accountRepo.save(contact);
                System.out.println(contact);
                return ResponseEntity.status(HttpStatus.OK).body(contact);

            }
            System.out.println(accountRepo.findById(username));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed to create a new contact");
        } catch (Exception err) {
            System.out.println("error ↓");
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to create new contact");
        }
    }

    @PostMapping(value = "/api/contact/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody JsonNode request, HttpServletRequest request_) {
        try {
            String username = request.get("username").asText();
            String password = request.get("password").asText();

            Contact acc = accountRepo.findById(username).isPresent() ? accountRepo.findById(username).get() : null;
            System.out.println(acc);

            if (acc != null && acc.getPassword().equals(password)) {
                List<Object> userInfoAndGroup = new ArrayList<>();
                List<GroupMember> listOfGroup = listOfChatBasedOnUsername(acc.getUsername());
                HttpSession session = request_.getSession();

                acc.setLoginTimeStamp(LocalDateTime.now());
                accountRepo.save(acc);
                session.setAttribute("authUser", acc);
                session.setMaxInactiveInterval(60 * 60 * 60 * 24);

                Map<String, HttpSession> socketHttpSessionMap = new HashMap<>();
                socketHttpSessionMap.put(session.getId(), session);
                WebSocketListener.setWebSocketSessionToHttpSessionMap(socketHttpSessionMap);
                WebSocketListener.setSessionId(session.getId());
                WebSocketListener.setHttpServletRequest(request_);

                userInfoAndGroup.add(acc);
                userInfoAndGroup.add(listOfGroup);
                System.out.println(session.getAttribute("authUser"));
                System.out.println(session.getId());
                return ResponseEntity.status(HttpStatus.OK).body(userInfoAndGroup);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("login failed");
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("error ↓");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to create new contact");
        }
    }

    @PostMapping(value = "/api/contact/google-login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> googleLogin(@RequestBody JsonNode request, HttpServletRequest request_) throws GeneralSecurityException, IOException {
        String idTokenString = request.get("idTokenString").asText();
        // Initialize the transport and jsonFactory
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(client_id))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        // (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            // Use or store profile information
            // ...

        } else {
            System.out.println("Invalid ID token.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("login success");
    }

    @PostMapping(value = "/api/contact/search", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> searchContact(@RequestBody JsonNode request) {
        try {
            String searchTxt = request.get("searchTxt").asText();
            Contact contact = accountRepo.findById(searchTxt).isPresent() ?
                    accountRepo.findById(searchTxt).get() : accountRepo.findByDisplayName(searchTxt).isPresent() ? accountRepo.findByDisplayName(searchTxt).get() : null;

            if (contact == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("can't find anything");
            }

            return ResponseEntity.status(HttpStatus.OK).body(contact);
        } catch (Exception err) {
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("server error");
        }
    }

    public List<GroupMember> listOfChatBasedOnUsername(String uName) {
        String username = uName;
        System.out.println(username);
        List<GroupMember> topicList = groupMemberRepo.findByContact(accountRepo.findById(username).get());

        for (int i = 0; i < topicList.size(); i++) {
            Message_ latestMsg = messageRepo.findFirstByTopicOrderByTimestampDesc(topicList.get(i).getTopic());
            topicList.get(i).setLatestMsg(latestMsg);
        }
        System.out.println(topicList);
//        System.out.println(kafkaServices.getTopicList());
        System.out.println(topicList);
        return topicList;
    }
}
