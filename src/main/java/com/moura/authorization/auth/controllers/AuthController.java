package com.moura.authorization.auth.controllers;

import com.moura.authorization.auth.dtos.AuthDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {


    @PostMapping()
    public ResponseEntity<Void> auth(
            @RequestBody AuthDto authDto,
            @RequestHeader(value = "X-Tenant-ID", required = false) UUID tenantId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
