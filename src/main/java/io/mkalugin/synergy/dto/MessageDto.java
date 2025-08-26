package io.mkalugin.synergy.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class MessageDto {

    private String message;
    private String response;

    public MessageDto(String message) {
        this.message = message;
    }
}
