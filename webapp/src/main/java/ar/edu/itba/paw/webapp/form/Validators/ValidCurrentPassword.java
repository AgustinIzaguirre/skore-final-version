package ar.edu.itba.paw.webapp.form.Validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidCurrentPasswordValidator.class)
@Documented
public @interface ValidCurrentPassword {
    String message() default "{ar.edu.itba.paw.webapp.form.Validators.ValidCurrentPassword.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}