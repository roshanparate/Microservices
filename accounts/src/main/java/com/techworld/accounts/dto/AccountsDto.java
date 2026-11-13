package com.techworld.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(
        name = "Accounts",
        description = "Schema to hold Account information"
)
public class AccountsDto {
    @NotEmpty(message = "Account number should not be Empty")
    @Pattern(regexp = "^[0-9]{10}$", message = "Account number must be 10 digits")
    @Schema(
            description = "Account Number of Eazy Bank account", example = "3454433243"
    )
    private Long accountNumber;

    @Schema(
            description = "Account type of Eazy Bank account", example = "Savings"
    )
    @NotEmpty(message = "account type can not be empty or a null")
    private String accountType;

    @NotEmpty(message = "branch address can not be empty or a null")
    @Schema(
            description = "Eazy Bank branch address", example = "123 NewYork"
    )
    private String branchAddress;
}
