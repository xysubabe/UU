package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdStringResponse {
    private String id;

    public static IdStringResponse of(Long id) {
        return new IdStringResponse(id != null ? id.toString() : null);
    }
}