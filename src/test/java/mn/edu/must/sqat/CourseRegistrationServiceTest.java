package mn.edu.must.sqat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock
    StudentRepository repository;

    @Mock
    EmailSender emailSender;

    CourseRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new CourseRegistrationService(repository, emailSender);
    }

    @Test
    void register_activeStudentWithPrerequisites_returnsOkAndSavesAndSendsEmail() {
        Student student = new Student("B222270823", "student@must.edu.mn", true, List.of("CS101", "CS102"));
        when(repository.findStudent("B222270823")).thenReturn(student);
        when(repository.courseExists("F.CSA313")).thenReturn(true);
        when(repository.prerequisites("F.CSA313")).thenReturn(List.of("CS101"));

        String result = service.register("B222270823", "F.CSA313");

        assertEquals("OK", result);
        verify(repository).saveRegistration("B222270823", "F.CSA313");
        verify(emailSender).send(eq("student@must.edu.mn"), anyString(), contains("F.CSA313"));
    }

    @Test
    void register_nonExistentStudent_returnsErrorNoStudentWithoutSavingOrSendingEmail() {
        when(repository.findStudent("UNKNOWN")).thenReturn(null);

        String result = service.register("UNKNOWN", "F.CSA313");

        assertEquals("ERROR_NO_STUDENT", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void register_inactiveStudent_returnsErrorInactiveStudentAndNoEmailInteraction() {
        Student inactive = new Student("B222270001", "inactive@must.edu.mn", false, List.of("CS101"));
        when(repository.findStudent("B222270001")).thenReturn(inactive);

        String result = service.register("B222270001", "F.CSA313");

        assertEquals("ERROR_INACTIVE_STUDENT", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }

    @Test
    void register_missingPrerequisites_returnsErrorPrerequisitesWithoutSaving() {
        Student student = new Student("B222270823", "student@must.edu.mn", true, List.of("CS101"));
        when(repository.findStudent("B222270823")).thenReturn(student);
        when(repository.courseExists("F.CSA313")).thenReturn(true);
        when(repository.prerequisites("F.CSA313")).thenReturn(List.of("CS101", "CS201"));

        String result = service.register("B222270823", "F.CSA313");

        assertEquals("ERROR_PREREQUISITES", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }

    @Test
    void register_emailSenderThrowsException_stillReturnsOkAndSavesRegistration() {
        Student student = new Student("B222270823", "student@must.edu.mn", true, List.of("CS101"));
        when(repository.findStudent("B222270823")).thenReturn(student);
        when(repository.courseExists("F.CSA313")).thenReturn(true);
        when(repository.prerequisites("F.CSA313")).thenReturn(List.of("CS101"));
        doThrow(new RuntimeException("SMTP error"))
                .when(emailSender).send(anyString(), anyString(), anyString());

        String result = service.register("B222270823", "F.CSA313");

        assertEquals("OK", result);
        verify(repository).saveRegistration("B222270823", "F.CSA313");
    }

    @Test
    void register_nullArguments_throwsIllegalArgumentExceptionWithNoInteractions() {
        assertThrows(IllegalArgumentException.class, () -> service.register(null, "F.CSA313"));
        assertThrows(IllegalArgumentException.class, () -> service.register("B222270823", null));
        verifyNoInteractions(repository, emailSender);
    }

    @Test
    void register_successfulRegistration_emailBodyContainsCourseId() {
        Student student = new Student("B222270823", "student@must.edu.mn", true, List.of());
        when(repository.findStudent("B222270823")).thenReturn(student);
        when(repository.courseExists("F.CSA313")).thenReturn(true);
        when(repository.prerequisites("F.CSA313")).thenReturn(List.of());

        service.register("B222270823", "F.CSA313");

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(eq("student@must.edu.mn"), anyString(), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("F.CSA313"));
    }

    @ParameterizedTest(name = "{index} => studentId={0}, active={1}, courseExists={2} -> {3}")
    @CsvSource({
            "NO_SUCH_STUDENT, true,  true,  ERROR_NO_STUDENT",
            "INACTIVE_ID,     false, true,  ERROR_INACTIVE_STUDENT",
            "ACTIVE_ID,       true,  false, ERROR_NO_COURSE"
    })
    void register_errorScenarios_returnsExpectedErrorCode(
            String studentId, boolean active, boolean courseExists, String expectedError) {
        if (!"NO_SUCH_STUDENT".equals(studentId)) {
            Student student = new Student(studentId, "s@must.edu.mn", active, List.of());
            lenient().when(repository.findStudent(studentId)).thenReturn(student);
        } else {
            lenient().when(repository.findStudent(studentId)).thenReturn(null);
        }
        lenient().when(repository.courseExists("F.CSA313")).thenReturn(courseExists);

        String result = service.register(studentId, "F.CSA313");

        assertEquals(expectedError, result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }
}