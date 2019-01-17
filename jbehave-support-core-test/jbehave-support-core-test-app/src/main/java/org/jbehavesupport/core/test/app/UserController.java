package org.jbehavesupport.core.test.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.jbehavesupport.core.test.app.domain.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class UserController {
    private Map<Long, User> users = new HashMap<>();

    @PostConstruct
    public void postConstruct() {
        users.put(5L, User.builder().id(5L).firstName("Emanuel").lastName("Rego").build());
        users.put(9L, User.builder().id(9L).firstName("Alison").lastName("Cerutti").build());
    }

    @PatchMapping("/init/")
    public ResponseEntity init() {
        users = new HashMap<>();
        postConstruct();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/")
    public ResponseEntity<List<User>> getUsers(@RequestParam(name = "order", required = false) String order) {
        if (users.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        List<User> userList = new ArrayList<>(users.values());
        if ("name".equals(order)) {
            Collections.sort(userList, Comparator.comparing(User::getFirstName));
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        return new ResponseEntity<>(users.get(id), HttpStatus.OK);
    }

    @PostMapping("/user/")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getId() == null) {
            Long id = users.keySet().stream().max(Comparator.naturalOrder()).get() + 1;
            user.setId(id);
        }

        users.put(user.getId(), user);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @PutMapping("/user/")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        users.put(user.getId(), user);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @PostMapping("/user/batch")
    public ResponseEntity updateUser(@RequestBody List<User> newUsers) {
        for (User user : newUsers) {
            users.put(user.getId(), user);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/secure/user/{id}")
    public ResponseEntity<User> getSecuredUser(@PathVariable("id") long id, @RequestHeader("customHeader") String customHeader) {
        return new ResponseEntity<>(users.get(id), HttpStatus.OK);
    }

    @PostMapping("/body/")
    public ResponseEntity handleRawBodyRequest(@RequestBody String body) {
        if ("this is raw body".equals(body)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/base64/multipart/")
    public ResponseEntity handleBase64Request(HttpServletRequest request) throws IOException, ServletException {
        if (!request.getParts().isEmpty()) {
            Map<String, String> response = request.getParts().stream()
                .sorted(Comparator.comparing(Part::getName))
                .map(Part::getName)
                .collect(Collectors.toMap(String::toString, String::toString));
            return new ResponseEntity(response, HttpStatus.OK);
        } else {
            return new ResponseEntity("valid multipart part was expected", HttpStatus.BAD_REQUEST);
        }
    }


}
