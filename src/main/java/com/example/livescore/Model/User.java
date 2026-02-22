package com.example.livescore.Model;


import com.google.cloud.firestore.annotation.Exclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String id;
    private String name;
    private String email;
    private String photoUrl;
    private Date createdAt;
    private String role;

    @Exclude
    public Role getRoleEnum() {
        return role == null ? null : Role.valueOf(role);
    }

    public void setRole(Role role) {
        this.role = role == null ? null : role.name();
    }
}



