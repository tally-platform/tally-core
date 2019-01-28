package org.thomaschen.tally.api;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TallyController {

    @GetMapping("/")
    public String index() {
        return "Hello, Welcome to the Tally API";
    }
}
