package com.example.seg2105_project_grp_33;

import static org.junit.Assert.*;

import org.junit.Test;

public class InputValidationHelperTest {

    @Test
    public void validPassword() {
        assertTrue(InputValidationHelper.ValidPassword("PasswordIsValid!"));
    }

    @Test
    public void validNumber() {
        assertTrue(InputValidationHelper.ValidNumber("234232"));
    }

    @Test
    public void validSocialMedia() {
        assertTrue(InputValidationHelper.ValidNumber("blah@instagram.com"));
    }

    @Test
    public void validDate() {
        assertTrue(InputValidationHelper.ValidDate("06/12/23"));
    }

    @Test
    public void nullOrEmptyField() {
        assertTrue(InputValidationHelper.NullOrEmptyField(" "));
    }

    @Test
    public void validInteger() {
        assertTrue(InputValidationHelper.ValidInteger("9000"));
    }
}