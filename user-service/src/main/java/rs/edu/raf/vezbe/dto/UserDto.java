package rs.edu.raf.vezbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 6879714582042601486L;

    private UUID id;

    private String username;

    private Boolean isAdmin;

    private String imePrezime;

}
