package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdResponse {
    private Long id;

    public static IdResponse of(Long id) {
        return new IdResponse(id);
    }
}