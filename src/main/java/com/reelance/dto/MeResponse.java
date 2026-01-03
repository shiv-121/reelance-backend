package com.reelance.dto;

import com.reelance.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
