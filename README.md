# Tubes1_STIMA

## Daftar Isi
* [Deskripsi Singkat Program](#deskripsi-singkat-program)
* [Sistematika File](#sistematika-file)
* [Requirements](#requirements)
* [Cara Kompilasi Program](#cara-kompilasi-program)
* [Dibuat Oleh](#dibuat-oleh)

## Deskripsi Singkat Program
Program ini merupakan implementasi bot service atau algoritma strategi permainan dari bot yang menggunakan algoritma greedy serta variasi strategi yang digunakan untuk memenangkan permainan dengan cara terbaik. Hasil kompilasi program menjadi executable jar yang dipanggil dalam permainan sehingga muncul dalam permainan sebagai player. Program ini dibuat dalam bahasa Java
## Sistematika File
```bash
.
├─ JavaBot
|  ├─── src
|  |    ├─── Enums
|  |    |    ├─── ObjectTypes.java
|  |    |    ├─── PlayerActions.java
|  |    ├─── Models
|  |    |    ├─── GameObject.java
|  |    |    ├─── GameState.java
|  |    |    ├─── GameStateDto.java
|  |    |    ├─── PlayerAction.java
|  |    |    ├─── Position.java
|  |    |    ├─── World.java
|  |    ├─── Services
|  |    |    ├─── BotService.java
|  |    ├─── main.java
|  ├─── target
|  |   ├─── paSTI MAh rank1.jar
|  ├─── doc
│  |    ├─── paSTI MAh rank 1.pdf
|  ├─── Dockerfile
|  ├─── pom.xml
|  └─── README.md
```
## Requirements
Java versi 11 atau lebih baru

## Cara Kompilasi Program
1. Pastikan Java sudah memiliki versi sesuai requirement
2. Hasil implementasi JavaBot dapat dicompile dengan melakukan `mvn clean package` yang dihasilkan file bertipe jar dalam folder target pada folder JavaBot.

## Dibuat oleh
* Nama: Bernardus Wilson
* NIM: 13521021
* Prodi/Jurusan: STEI/Teknik Informatika
* Profile Github: bernarduswillson
* Nama: Kenneth Dave Bahana
* NIM: 13521145
* Prodi/Jurusan: STEI/Teknik Informatika
* Profile Github: kenndave
