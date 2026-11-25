# Лаборатори №8: Mock объектууд — Mockito

- **Оюутны нэр:** А. Наранзул
- **Оюутны код:** B222270823

## Орчны хувилбар

openjdk version "17.0.16" 2025-07-15
OpenJDK Runtime Environment Temurin-17.0.16+8 (build 17.0.16+8)
OpenJDK 64-Bit Server VM Temurin-17.0.16+8 (build 17.0.16+8, mixed mode, sharing)

Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
Maven home: C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.16
Java version: 17.0.16, vendor: Eclipse Adoptium, runtime: C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
Default locale: en_US, platform encoding: Cp1252
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"

## Тестийн үр дүн

- Нийт тест (Tests run:): 10 (esults/mvn-test.txt)

## A8 дэх lenient()-ийн тайлбар

A8 CSV мөр бүрээр бүх stub дуудагдахгүй байж болох тул Mockito-гийн strict шалгалтад унахгүй байхын тулд lenient().when(...) ашигласан.

## Хэсэг B: Мини мутацийн үр дүн

| Мутант | Өөрчлөлт | Унасан тест | Тайлбар |
| :--- | :--- | :--- | :--- |
| Мутант 1 | if (!student.active()) комментлов | egister_inactiveStudent_returnsErrorInactiveStudentAndNoEmailInteraction, egister_errorScenarios_returnsExpectedErrorCode | Идэвхгүй оюутны шалгалт алдагдсаныг тест илрүүлэв |
| Мутант 2 | saveRegistration(...) комментлов | egister_activeStudentWithPrerequisites_returnsOkAndSavesAndSendsEmail, egister_emailSenderThrowsException_stillReturnsOkAndSavesRegistration | Хадгалалт дуудагдаагүйг erify барив |
| Мутант 3 | \|\| courseId == null хасав | egister_nullArguments_throwsIllegalArgumentExceptionWithNoInteractions | courseId == null үед exception шидээгүйг A6 барив |

## Дүгнэлт

Mock нь гадаад хамаарлын зан төлөв болон дуудлагыг (erify) шалгахад тохиромжтой бол fake нь энгийн төлөвийг дотооддоо хадгалж ажилладаг. Гадаад хамаарлыг mock-оор тусгаарлах нь зөв ч энгийн зүйлийг хэт олон mock-оор солих хэрэггүй, мөн erify-ийг хэт олноор тавьбал рефакторинг хийхэд тест хуурамчаар унаж болно. A6 дээр хоёр параметрийн аль алиных нь 
ull-ийг шалгасан нь 3-р мутантыг шууд илрүүлэхэд тусаллаа.