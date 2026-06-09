package com.chantier.sgvac.web;

import com.chantier.sgvac.access.AccessControlService;
import com.chantier.sgvac.access.AccessEvent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class AccessController {

    private final AccessControlService accessControlService;

    public AccessController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping("/access")
    public String form() {
        return "access";
    }

    @PostMapping("/access")
    public String check(@RequestParam String badgeCode,
                        @RequestParam(defaultValue = "Portail principal") String checkpoint,
                        Principal principal, Model model) {
        AccessEvent event = accessControlService.evaluate(
                badgeCode.trim(), checkpoint, principal.getName(), LocalDateTime.now());
        model.addAttribute("event", event);
        return "access";
    }
}
