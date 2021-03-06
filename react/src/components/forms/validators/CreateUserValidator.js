import i18next from 'i18next';
import { hasStringValidSymbols, isString,
         isStringValidEmail, isStringNumeric, 
         isStringAlphaOrSpaces} from './utils/StringValidators';
import { isValidPastDate } from './utils/DateValidators';
import { validateRequiredField, validateOnlyRequiredField } from './utils/RequiredFieldValidators';
import { validImageFormats } from './utils/ImageValidators';

const ERROR_BASE_LABEL = "createUserForm.errors.";

//username constants
const MIN_USERNAME_LENGTH = 4;
const MAX_USERNAME_LENGTH = 100;
const usernameInvalidSymbols= /[^a-zA-Z0-9_.]/;

//password constants
const MIN_PASSWORD_LENGTH = 6;
const MAX_PASSWORD_LENGTH = 100;

//names constants
const MIN_NAME_LENGTH = 2;
const MAX_NAME_LENGTH = 100;

//image constants
const MAX_IMAGE_SIZE_IN_BYTES = 1048576;

//cellphone constants
const CELLPHONE_LENGTH = 10;

//birthday constants
const BIRTHDAY_LENGTH = 10;
const dateInvalidSymbols= /[^0-9/]/;

const hasUsernameValidSymbols = username => hasStringValidSymbols(username, usernameInvalidSymbols);

const validateUsername = username => validateRequiredField(username, `${ERROR_BASE_LABEL}username`,
                                                                hasUsernameValidSymbols, MIN_USERNAME_LENGTH,
                                                                MAX_USERNAME_LENGTH);

const validatePassword = password => validateRequiredField(password, `${ERROR_BASE_LABEL}password`,
                                                                isString, MIN_PASSWORD_LENGTH,
                                                                MAX_PASSWORD_LENGTH);

const validateRepeatedPassword = (repeatedPassword, password) => {
    let errorMessage = validateRequiredField(repeatedPassword, `${ERROR_BASE_LABEL}repeatedPassword`,
                                                isString, MIN_PASSWORD_LENGTH,
                                                MAX_PASSWORD_LENGTH);
    if (repeatedPassword !== password) {
        errorMessage =`${errorMessage} ${i18next.t(`${ERROR_BASE_LABEL}repeatedPassword.passwordDoesNotMatch`)}`;
    }
    return errorMessage;
}

const validateFirstName = firstName => validateRequiredField(firstName, `${ERROR_BASE_LABEL}firstName`,
                                                                isStringAlphaOrSpaces, MIN_NAME_LENGTH,
                                                                MAX_NAME_LENGTH);

const validateLastName = lastName => validateRequiredField(lastName, `${ERROR_BASE_LABEL}lastName`,
                                                                isStringAlphaOrSpaces, MIN_NAME_LENGTH,
                                                                MAX_NAME_LENGTH);

const validateEmail = email => {
    let errorLabelBase = `${ERROR_BASE_LABEL}email`;
    let errorMessage = ``;
    if (!email) {
        errorMessage = i18next.t(`${errorLabelBase}.required`);
    }
    if (email && !isStringValidEmail(email)) {
        errorMessage =`${errorMessage} ${i18next.t(`${errorLabelBase}.invalidEmail`)}`;
    }
    return errorMessage;
}

const validateImage = image => {
    let errorLabelBase = `${ERROR_BASE_LABEL}image`;
    let errorMessage = ``;
    if (image && !validImageFormats(image.type)) {
        errorMessage = i18next.t(`${errorLabelBase}.invalidImageFormat`);
    }
    if (image && image.size > MAX_IMAGE_SIZE_IN_BYTES) {
        errorMessage =`${errorMessage} ${i18next.t(`${errorLabelBase}.invalidImageSize`)}`;
    }
    return errorMessage;
}

const validateCellphone = cellphone => {
    if (cellphone && (cellphone.length !== CELLPHONE_LENGTH || !isStringNumeric(cellphone))) {
        return i18next.t(`${ERROR_BASE_LABEL}cellphone.invalidCellphone`);
    }
}

const hasStringValidDateSymbols = date => hasStringValidSymbols(date, dateInvalidSymbols);

const validatePastDate = date => {
    const dateArray = date.split("/");
    if(dateArray.length !== 3) {
        return false;
    }
    const month = parseInt(dateArray[0]);
    const day = parseInt(dateArray[1]);
    const year = parseInt(dateArray[2]);
    if(!isValidPastDate(day, month, year)) {
        return false;
    }
    return true;
}

const validateDate = date => {
    let errorLabelBase = `${ERROR_BASE_LABEL}birthday`;
    let errorMessage = validateRequiredField(date, `${ERROR_BASE_LABEL}birthday`,
                                                hasStringValidDateSymbols, BIRTHDAY_LENGTH,
                                                BIRTHDAY_LENGTH);
    
    if (date && !validatePastDate(date)) {
        errorMessage =`${errorMessage} ${i18next.t(`${errorLabelBase}.invalidPastDate`)}`;
    }
    return errorMessage;
}

const validateYear = year => validateOnlyRequiredField(year, `${ERROR_BASE_LABEL}year`);

const validateMonth = month => validateOnlyRequiredField(month, `${ERROR_BASE_LABEL}month`);

const validateDay = day => validateOnlyRequiredField(day, `${ERROR_BASE_LABEL}day`);

const CreateUserFormValidator = {
    validateUsername: validateUsername,
    validatePassword: validatePassword,
    validateRepeatedPassword: validateRepeatedPassword,
    validateFirstName: validateFirstName,
    validateLastName: validateLastName,
    validateEmail: validateEmail,
    validateImage: validateImage,
    validateCellphone: validateCellphone,
    validateDate: validateDate,
    validateYear: validateYear,
    validateMonth: validateMonth,
    validateDay: validateDay,
}

export default CreateUserFormValidator; 