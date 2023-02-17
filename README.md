# Tubes1_JannaWessels.io
Tugas Besar 1 Strategi Algoritma 2023

> Tugas besar ini adalah contoh penerapan metode greedy pada game galaxio. Metode greedy merupakan salah satu cara atau teknik merancang suatu algoritma. Metode Greedy digunakan untuk mendapatkan solusi optimal dari suatu permasalahan. Pada tugas ini kami menggunakan greedy yang merubah objek menjadi suatu nilai matematis yang kemudian dapat diakumulasi menjadi bobot penentu langkah bot.

## Program Requirement
- IntelliJ IDEA 2022.3.2
- apache-maven-3.9.0
- Java 11 or above
- .NET Core 3.1
- .NET 5.0 Runtime  
- https://github.com/EntelectChallenge/2021-Galaxio/releases/tag/2021.3.2

## Cara Build Program
1. Change Directory ke `../Tubes1_JannaWessels.io/Untouchable`
2. Jalankan perintah `mvn clean package`
3. Akan muncul folder target pada directory tersebut, folder target berisi file .jar

## Cara Menjalankan Program
1. Runner –saat dijalankan– akan meng-host sebuah match pada sebuah hostname tertentu. Untuk koneksi lokal, runner akan meng-host pada localhost:5000.
2. Engine kemudian dijalankan untuk melakukan koneksi dengan runner. Setelah terkoneksi, Engine akan menunggu sampai bot-bot pemain terkoneksi ke runner.
3. Logger juga melakukan hal yang sama, yaitu melakukan koneksi dengan runner.
4. Pada titik ini, dibutuhkan beberapa bot untuk melakukan koneksi dengan runner agar match dapat dimulai. Jumlah bot dalam satu pertandingan didefinisikan pada atribut BotCount yang dimiliki file JSON ”appsettings.json”. File tersebut terdapat di dalam folder “runner-publish” dan “engine-publish”.
5. Permainan akan dimulai saat jumlah bot yang terkoneksi sudah sesuai dengan konfigurasi.
6. Bot yang terkoneksi akan mendengarkan event-event dari runner. Salah satu event yang paling penting adalah RecieveGameState karena memberikan status game.
7. Bot juga mengirim event kepada runner yang berisi aksi bot.
8. Permainan akan berlangsung sampai selesai. Setelah selesai, akan terbuat dua file json yang berisi kronologi match.

## Identitas Pembuat
- Ezra M C M H - 13521073 - K1
- Christian Albert Hasiholan - 13521078 -K2
- Tobias Natalio Sianipar - 13521090 - K2
