package ar.edu.itba.paw.validators;

import ar.edu.itba.paw.models.PremiumUser;
import ar.edu.itba.paw.webapp.exceptions.ApiException;
import ar.edu.itba.paw.webapp.utils.JSONUtils;
import ar.edu.itba.paw.webapp.validators.UserValidators;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

public class UserValidatorsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void whenValidatingExistenceIfOptionalIsEmptyThenThrowApiExceptionWithExpectedValues() {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("User 'username' not found");
        UserValidators.existenceValidatorOf("username", "log").validate(Optional.empty());
    }

    @Test
    public void whenValidatingExistenceIfOptionalIsPresentThenSuccessByDoingNothing() {
        UserValidators.existenceValidatorOf("username", "log").validate(Optional.of(new PremiumUser()));
    }

    @Test
    public void whenValidatingAuthorizationIfIsTheSameUsernameThenSuccessByDoingNothing() {
        UserValidators.isAuthorizedForUpdateValidatorOf("username", "log").validate(Optional.of(
                new PremiumUser("firstName", "lastName", "an@email.com", "username")));
    }

    @Test
    public void whenValidatingAuthorizationIfOptionalIsEmptyThenThrowApiExceptionWithExpectedValues() {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Only 'username' can update his own user");
        UserValidators.isAuthorizedForUpdateValidatorOf("username", "log").validate(Optional.empty());
    }

    @Test
    public void whenValidatingAuthorizationIfOptionalIsPresentButDifferentUsernameThenThrowApiExceptionWithExpectedValues() {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Only 'username' can update his own user");
        UserValidators.isAuthorizedForUpdateValidatorOf("username", "log").validate(Optional.of(
                new PremiumUser("firstName", "lastName", "an@email.com", "otherUsername")
        ));
    }

    @Test
    public void whenValidatingUpdateIfHasAllKnownAndRequiredFieldsWithValidFormatThenSuccessByDoingNothing() {
        UserValidators.updateValidatorOf("log").validate(JSONUtils.jsonObjectFrom(
                "{\n" +
                    "\t\"email\" : \"an@email.com\",\n" +
                    "\t\"home\" : {\n" +
                    "\t\t\"state\" : \"a state\",\n" +
                    "\t\t\"city\" : \"a city\",\n" +
                    "\t\t\"country\" : \"a country\",\n" +
                    "\t\t\"street\" : \"a street\"\n" +
                    "\t}\n" +
                "}"
                )
        );
    }

    @Test
    public void whenValidatingUpdateIfHasAnUnknownUpdateFieldThenThrowApiExceptionWithExpectedValues() {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Field 'unknown' is unknown or unaccepted");
        UserValidators.updateValidatorOf("log").validate(JSONUtils.jsonObjectFrom(
                "{\n" +
                    "\t\"email\" : \"an@email.com\",\n" +
                    "\t\"home\" : {\n" +
                    "\t\t\"state\" : \"a state\",\n" +
                    "\t\t\"city\" : \"a city\",\n" +
                    "\t\t\"country\" : \"a country\",\n" +
                    "\t\t\"street\" : \"a street\"\n" +
                    "\t},\n" +
                    "\t\"unknown\" : \"whatever value\"\n" +
                "}"
                )
        );
    }

    @Test
    public void whenValidatingUpdateIfHasNoFieldsThenSuccessByDoingNothingBecauseOfNoRequiredFieldsSetUp() {
        UserValidators.updateValidatorOf("log").validate(JSONUtils.jsonObjectFrom("{ }"));
    }

    @Test
    public void whenValidatingCreationIfHasAllKnownAndRequiredFieldsWithValidFormatThenSuccessByDoingNothing() {
        UserValidators.creationValidatorOf("log").validate(JSONUtils.jsonObjectFrom(
                "{\n" +
                    "\t\"username\" : \"a_Username_69\",\n" +
                    "\t\"firstName\" : \"a first name\",\n" +
                    "\t\"lastName\" : \"a last name\",\n" +
                    "\t\"cellphone\" : \"1512345678\",\n" +
                    "\t\"birthday\" : \"06/06/1969\",\n" +
                    "\t\"email\" : \"an@email.com\",\n" +
                    "\t\"home\" : {\n" +
                    "\t\t\"state\" : \"a state\",\n" +
                    "\t\t\"city\" : \"a city\",\n" +
                    "\t\t\"country\" : \"a country\",\n" +
                    "\t\t\"street\" : \"a street\"\n" +
                    "\t}\n" +
                    "}"
                )
        );
    }

    @Test
    public void whenValidatingCreationIfHasAnUnknownCreationFieldThenThrowApiExceptionWithExpectedValues() {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Field 'unknown' is unknown or unaccepted");
        UserValidators.creationValidatorOf("log").validate(JSONUtils.jsonObjectFrom(
                "{\n" +
                    "\t\"email\" : \"an@email.com\",\n" +
                    "\t\"home\" : {\n" +
                    "\t\t\"state\" : \"a state\",\n" +
                    "\t\t\"city\" : \"a city\",\n" +
                    "\t\t\"country\" : \"a country\",\n" +
                    "\t\t\"street\" : \"a street\"\n" +
                    "\t},\n" +
                    "\t\"unknown\" : \"whatever value\"\n" +
                "}"
                )
        );
    }

    @Test
    public void whenValidatingCreationIfARequiredCreationFieldIsMissingThenThrowApiExceptionWithExpectedValues() {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Missing required 'username' field");
        UserValidators.creationValidatorOf("log").validate(JSONUtils.jsonObjectFrom(
                "{\n" +
                    "\t\"firstName\" : \"a first name\",\n" +
                    "\t\"lastName\" : \"a last name\",\n" +
                    "\t\"birthday\" : \"06/06/1969\",\n" +
                    "\t\"email\" : \"an@email.com\"\n" +
                "}"
                )
        );
    }

    @Test
    public void whenValidatingCreationIfAFieldHasInvalidValueThenThrowApiExceptionWithExpectedValues() {
        exceptionRule.expect(ApiException.class);
        exceptionRule.expectMessage("Field 'birthday' must be a date");
        UserValidators.creationValidatorOf("log").validate(JSONUtils.jsonObjectFrom(
                "{\n" +
                    "\t\"username\" : \"a_Username_69\",\n" +
                    "\t\"firstName\" : \"a first name\",\n" +
                    "\t\"lastName\" : \"a last name\",\n" +
                    "\t\"cellphone\" : \"1512345678\",\n" +
                    "\t\"birthday\" : \"this is not a date!\",\n" +
                    "\t\"email\" : \"an@email.com\",\n" +
                    "\t\"home\" : {\n" +
                    "\t\t\"state\" : \"a state\",\n" +
                    "\t\t\"city\" : \"a city\",\n" +
                    "\t\t\"country\" : \"a country\",\n" +
                    "\t\t\"street\" : \"a street\"\n" +
                    "\t}\n" +
                "}"
                )
        );
    }
}