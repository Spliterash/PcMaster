package ru.spliterash.pcmasterserver.api.methods.auth.register;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AuthRegisterRequest {
    private String username;
    private String password;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("middle_name")
    private String middleName;
}
