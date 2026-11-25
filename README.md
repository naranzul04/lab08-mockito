# Лаборатори №8 — Mockito mock объектууд

**Оюутан:** А. Наранзул  
**Оюутны код:** B222270823  
**Хичээл:** F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест  
**Орчин:** Windows 11, PowerShell

## Орчны хувилбар

```text
openjdk version "17.0.16" 2025-07-15
OpenJDK Runtime Environment Temurin-17.0.16+8 (build 17.0.16+8)
OpenJDK 64-Bit Server VM Temurin-17.0.16+8 (build 17.0.16+8, mixed mode, sharing)
```

```text
Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
Maven home: C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.16
Java version: 17.0.16, vendor: Eclipse Adoptium, runtime: C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
Default locale: en_US, platform encoding: Cp1252
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

## Төслийн бүтэц

```text
lab08-mockito/
├── pom.xml
├── README.md
├── src/main/java/mn/edu/must/sqat/
│   ├── Student.java
│   ├── StudentRepository.java
│   ├── EmailSender.java
│   └── CourseRegistrationService.java
├── src/test/java/mn/edu/must/sqat/
│   └── CourseRegistrationServiceTest.java
└── results/
    ├── mvn-test.txt
    ├── mvn-test-mutant-1.txt
    ├── mvn-test-mutant-2.txt
    └── mvn-test-mutant-3.txt
```

## Хэсэг A — Тестүүд

esults/mvn-test.txt файлын сүүлийн мөр: **Tests run: 10, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS** (7 энгийн тест + A8-ийн 3 CSV мөр).

| № | Тестийн метод | Ашигласан зүйл |
|---|---|---|
| A1 | egister_activeStudentWithPrerequisites_returnsOkAndSavesAndSendsEmail | when + erify |
| A2 | egister_nonExistentStudent_returnsErrorNoStudentWithoutSavingOrSendingEmail | erify(..., never()) |
| A3 | egister_inactiveStudent_returnsErrorInactiveStudentAndNoEmailInteraction | erifyNoInteractions |
| A4 | egister_missingPrerequisites_returnsErrorPrerequisitesWithoutSaving | when + 
ever |
| A5 | egister_emailSenderThrowsException_stillReturnsOkAndSavesRegistration | doThrow(...).when(...) |
| A6 | egister_nullArguments_throwsIllegalArgumentExceptionWithNoInteractions | ssertThrows |
| A7 | egister_successfulRegistration_emailBodyContainsCourseId | ArgumentCaptor |
| A8 | egister_errorScenarios_returnExpectedErrorCode (3 мөр) | @ParameterizedTest + @CsvSource |

Matcher хэрэглэсэн бүх дуудалт дээр аргумент бүрд нь matcher (eq(...), nyString(), contains(...)) тавьсан.

### A8 дахь lenient()-ийн тайлбар

A8 тестийн CSV мөр бүр дээр өөр өөр нөхцөл шалгадаг болохоор зарим мөрд тохируулсан stub нь тухайн замд хүрэхгүй дуудагдахгүй үлддэг. Mockito-гийн энгийн (strict) горим ашиглагдаагүй stub дээр алдаа өгдөг тул тэр мөрүүдийг алдаагүй ажиллуулахын тулд lenient().when(...) хэрэглэсэн.

## Хэсэг B — Мини мутаци

| Мутант | Хийсэн өөрчлөлт | Унасан тестүүд | Тайлбар |
|---|---|---|---|
| Мутант 1 | if (!student.active()) return "ERROR_INACTIVE_STUDENT"; комментлосон | egister_inactiveStudent_returnsErrorInactiveStudentAndNoEmailInteraction, egister_errorScenarios_returnsExpectedErrorCode | Идэвхгүй оюутныг шалгахгүй болсныг тест барьсан (esults/mvn-test-mutant-1.txt) |
| Мутант 2 | epository.saveRegistration(studentId, courseId); комментлосон | egister_activeStudentWithPrerequisites_returnsOkAndSavesAndSendsEmail, egister_emailSenderThrowsException_stillReturnsOkAndSavesRegistration | Хадгалах функц дуудагдаагүйг erify барьсан (esults/mvn-test-mutant-2.txt) |
| Мутант 3 | if (studentId == null \|\| courseId == null) -> if (studentId == null) | egister_nullArguments_throwsIllegalArgumentExceptionWithNoInteractions | courseId == null үед алдаа шидэхгүй болсныг A6 барьсан (esults/mvn-test-mutant-3.txt) |

**Мутацийн тухай:** 3 мутантыг гурвууланг нь тестүүд маань барьж чадсан (BUILD FAILURE). A6 тест дээр studentId болон courseId хоёулангийнх нь 
ull тохиолдлыг тус тусад нь шалгасан болохоор 3-р мутант шууд баригдсан. Харин A7 тест зөвхөн и-мэйлийн утгыг шалгадаг тул Мутант 2-ыг бариагүй, тэр хэсгийг A1 болон A5 тестүүд хамгаалж байна.

## Дүгнэлт

Mock гэдэг нь хамаарлын буцаах утгыг when-ээр тохируулаад, ямар дуудалт хийгдсэнийг erify-ээр шалгадаг орлуулагч объект юм. Харин fake нь жишээ нь HashMap ашиглаад өгөгдлөө өөр дээрээ хадгалдаг хялбаршуулсан бодит код байдаг. Өгөгдлийн сан, и-мэйл илгээгч шиг гаднын хамаарлыг mock хийх нь зөв ч Student, List, String зэрэг энгийн өгөгдлийн объектуудыг mock хийх хэрэггүй, шууд бодитоор нь үүсгэсэн нь дээр. Мөн erify-ийг хэт олон жижиг зүйл дээр тавьбал дараа нь кодоо цэгцэлж өөрчлөх үед логик нь зөв байсан ч тест унаад байдаг сул талтай. Хэсэг B-ийн мутаци хийж үзсэнээр манай тестүүд зөвхөн ногоон гараад зогсохгүй, кодод алдаа гарахад бодитоор илрүүлж чадаж байгаа нь харагдлаа.