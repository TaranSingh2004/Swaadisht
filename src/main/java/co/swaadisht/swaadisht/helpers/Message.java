package co.swaadisht.swaadisht.helpers;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String content;
    @Builder.Default
    public MessageType type=MessageType.blue;
}
