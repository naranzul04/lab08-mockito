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

## Хэсэг A — Нэгжийн тестүүд

esults/mvn-test.txt файлын сүүлийн үр дүн: **Tests run: 10, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS** (7 энгийн @Test метод + @ParameterizedTest-ийн 3 CSV мөр).

| № | Тестийн метод | Гол хэрэгсэл |
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

Matcher ашигласан erify / when дуудалт бүрд бүх аргумент дээр тохирох matcher (eq(...), nyString(), contains(...)) хэрэглэсэн.

### A8 дахь lenient()-ийн тайлбар

A8 параметржүүлсэн тестийн CSV мөр бүр egister()-ийн өөр нөхцөл дээр зогсдог тул тухайн мөрийн замд хүрэлгүй ашиглагдахгүй үлдэх stub гарах боломжтой. MockitoExtension-ий strict stubbing шалгалт ашиглагдаагүй stub дээр UnnecessaryStubbingException үүсгэхээс сэргийлж lenient().when(...) ашигласан.

## Хэсэг B — Мини мутацийн шинжилгээ

| Мутант | Түр өөрчлөлт | Унасан тестүүд | Тайлбар |
|---|---|---|---|
| Мутант 1 | if (!student.active()) return "ERROR_INACTIVE_STUDENT"; комментлов | egister_inactiveStudent_returnsErrorInactiveStudentAndNoEmailInteraction, egister_errorScenarios_returnsExpectedErrorCode | Идэвхгүй оюутны шалгалт алдагдсаныг тест илрүүлэв (esults/mvn-test-mutant-1.txt) |
| Мутант 2 | epository.saveRegistration(studentId, courseId); комментлов | egister_activeStudentWithPrerequisites_returnsOkAndSavesAndSendsEmail, egister_emailSenderThrowsException_stillReturnsOkAndSavesRegistration | Бүртгэл хадгалаагүйг erify барив (esults/mvn-test-mutant-2.txt) |
| Мутант 3 | if (studentId == null \|\| courseId == null) -> if (studentId == null) | egister_nullArguments_throwsIllegalArgumentExceptionWithNoInteractions | courseId == null үед exception шидээгүйг A6 барив (esults/mvn-test-mutant-3.txt) |

**Мутацийн ажиглалт:** Гурван мутант бүгд тестүүдээр баригдаж BUILD FAILURE болсон. A6 тестэд хоёр параметрийн аль алиных нь 
ull тохиолдлыг шалгасан тул 3-р мутант илэрсэн; харин и-мэйлийн агуулгыг голчлон шалгадаг A7 нь хадгалалтын алдааг (Мутант 2) шалгахгүй байгаа нь нэг тест нэг гол зан төлөвийг шалгах зарчимд нийцэж байна.

## Дүгнэлт

Mock объект нь хамаарлын зан төлөв, буцаах утга болон дуудлагыг (when, erify, ArgumentCaptor) тест дотор тодорхойлж шалгахад тохиромжтой бол гараар бичсэн fake нь дотоод төлөв хадгалдаг хялбаршуулсан бодит хэрэгжүүлэлт юм. Гадаад эсвэл удаан хамаарлыг (StudentRepository, EmailSender) mock-оор орлуулах нь зохимжтой боловч энгийн өгөгдлийн объект (Student, List, String) эсвэл тестлэгдэж буй классыг өөрийг нь mock хийх хэрэггүй. Мөн erify-ийг дотоод жижиг алхам бүрд хэт олноор тавьбал кодын бүтцийг цэгцлэхэд (refactoring) үйлдэл нь зөв байсан ч тест хуурамчаар унах эрсдэлтэй тул зөвхөн бодит үр дагавартай харилцан үйлчлэлийг шалгах нь зүйтэй.