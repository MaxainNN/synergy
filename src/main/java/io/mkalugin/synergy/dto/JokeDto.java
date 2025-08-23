package io.mkalugin.synergy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JokeDto {
    private int id;
    private String setup;
    private String punchline;
    private String type;
}
