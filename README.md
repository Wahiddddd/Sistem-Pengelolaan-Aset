# Sistem Pengelolaan Aset (Asset Management System)

Sistem Pengelolaan Aset adalah aplikasi berbasis Spring Boot yang dirancang untuk membantu perusahaan dalam mengelola siklus hidup aset secara efisien, mulai dari pendataan, monitoring status, hingga pencatatan log pemeliharaan secara otomatis dan terintegrasi.

---

## Tech Stack

Aplikasi ini dibangun menggunakan teknologi modern untuk memastikan performa dan keamanan yang optimal:

- **Back-end:** Java 17, Spring Boot 4.0.3
- **Security:** Spring Security & JSON Web Token (JWT)
- **Database:** MySQL
- **Migration:** Flyway (Database Version Control)
- **Documentation:** Swagger/OpenAPI (TBA)
- **Tools:** Maven, Lombok, Jakarta Validation

---

## Fitur Utama

- **Manajemen User (RBAC):** Dukungan multi-role (Super Admin, Admin, dan Teknisi) dengan fitur keamanan *Auto-lock account* setelah 5 kali gagal login.
- **Manajemen Inventaris:** Admin dapat mengelola data aset, kategori, dan mencari aset berdasarkan nomor seri unik.
- **Otomatisasi Jadwal Servis:** Sistem menghitung tanggal servis berikutnya secara otomatis berdasarkan frekuensi pemeliharaan.
- **Dashboard Teknisi:** Teknisi dapat memperbarui status aset (*In Maintenance*, *Broken*, *Working*) dan mencatat detail perbaikan.
- **History Maintenance:** Pencatatan log lengkap untuk setiap aset, mencakup biaya, durasi, dan teknisi yang bertanggung jawab.

---

## 📂 Struktur Proyek

```text
src/main/java/com/asset/manager/asset_management/
├── config/             # Konfigurasi aplikasi (Security, Beans)
├── controller/         # REST API Endpoints
├── DTO/                # Data Transfer Objects untuk Request/Response
├── entity/             # Java Persistence API (JPA) Entities
├── exception/          # Global Exception Handling
├── repository/         # Spring Data JPA Repositories
├── security/           # JWT & Authentication Logic
└── service/            # Business Logic Layer
```

---

## Flow Business (Alur Bisnis)

Berikut adalah visualisasi alur kerja dalam sistem ini:

### 1. Registrasi dan Login
Sistem menggunakan keamanan berbasis NIK dan password, serta fitur penguncian akun otomatis.
![Flowchart Login](flow_chart/Dashboard%20Teknisi%20(Maintenance)-Flow%20Bussiness%20(Register%20dan%20Login).drawio.png)

### 2. Manajemen Inventaris (Admin)
Admin menambahkan aset baru dan sistem menghitung jadwal servis secara otomatis.
![Flowchart Input Aset](flow_chart/Dashboard%20Teknisi%20(Maintenance)-Flow%20Chart(Admin%20(Input%20Aset)).drawio.png)

### 3. Monitoring & Update Aset (Admin)
Fitur untuk melihat daftar aset dan melakukan pembaruan data secara berkala.
![Flowchart List Aset](flow_chart/Dashboard%20Teknisi%20(Maintenance)-Flow%20Chart(Admin(list%20aset)).drawio.png)

### 4. Laporan Maintenance (Admin)
Admin dapat memantau laporan pemeliharaan aset dari semua teknisi.
![Flowchart Laporan](flow_chart/Dashboard%20Teknisi%20%28Maintenance%29-Flow%20Chart%28Admin%28Laporan%20Maintenence%29%29%29.drawio.png)

### 5. Alur Kerja Pemeliharaan (Teknisi)
Teknisi memproses aset yang membutuhkan servis dan mencatat riwayat pengerjaan.
![Flowchart Teknisi](flow_chart/Dashboard%20Teknisi%20(Maintenance)-Flow%20Chart(Teknisi).drawio.png)

---

## Desain Database

Struktur database dirancang untuk mendukung relasi yang kompleks antara aset, kategori, dan log pemeliharaan.

![Desain Database](flow_chart/Desain%20Database.png)

### Relasi Antar Tabel:
- **Categories 1 : N Assets**: Satu kategori dapat memiliki banyak aset.
- **Assets 1 : N Maintenance Logs**: Satu aset memiliki riwayat banyak catatan pemeliharaan.
- **Users 1 : N Maintenance Logs**: Satu teknisi dapat bertanggung jawab atas banyak log perbaikan.

---

## API Endpoints

Aplikasi menyediakan RESTful API yang dapat diakses melalui endpoint berikut:

### 1. User API (`/api/users`)
- `POST /api/users` : Membuat User Baru (Admin Only)
- `GET /api/users` : Mengambil Semua Daftar User (Dengan Pagination)
- `GET /api/users/{id}` : Mengambil Detail User berdasarkan ID
- `PUT /api/users/{id}` : Memperbarui Data User
- `DELETE /api/users/{id}` : Menghapus User (Soft Delete)

### 2. Category API (`/api/categories`)
- `POST /api/categories` : Menambahkan Kategori Baru
- `GET /api/categories` : Mengambil Semua Daftar Kategori
- `GET /api/categories/{id}` : Mengambil Detail Kategori berdasarkan ID
- `PUT /api/categories/{id}` : Mengubah Nama Kategori
- `DELETE /api/categories/{id}` : Menghapus Kategori

### 3. Asset API (`/api/assets`)
- `POST /api/assets` : Menambahkan Aset Baru
- `GET /api/assets` : Mengambil Semua Daftar Aset (Dengan Pagination)
- `GET /api/assets/{id}` : Mengambil Detail Satu Aset berdasarkan ID
- `GET /api/assets/due-maintenance` : Mengambil Daftar Aset yang Mendekati/Sudah Jatuh Tempo Maintenance
- `GET /api/assets/search` : Mencari Aset berdasarkan Keyword
- `PUT /api/assets/{id}` : Mengubah/Update Data Aset
- `DELETE /api/assets/{id}` : Menghapus Aset (Soft Delete)

### 4. Maintenance API (`/api/maintenance`)
- `POST /api/maintenance/start/{assetId}` : Memulai Proses Maintenance
- `POST /api/maintenance/finish/{assetId}` : Menyelesaikan Maintenance
- `POST /api/maintenance/broken/{assetId}` : Melaporkan Aset Rusak Total
- `POST /api/maintenance/force-test/{assetId}` : Memaksa Aset ke Status NEEDS_MAINTENANCE (Testing)
- `GET /api/maintenance/asset/{assetId}` : Riwayat Maintenance per Aset
- `GET /api/maintenance/maintenance-logs` : Mengambil Semua Histori Maintenance (Pagination)
- `GET /api/maintenance/maintenance-logs/search` : Mencari Histori Maintenance berdasarkan Serial Number
- `GET /api/maintenance/assets-for-maintenance` : Mengambil Aset yang Membutuhkan Maintenance

### 5. Auth API (`/api/auth`)
- `POST /api/auth/login` : Endpoint Login (Mengubah Kredensial Menjadi Token)

---

## Instalasi & Cara Menjalankan

1. Clone repositori ini.
2. Pastikan file `application.properties` sudah dikonfigurasi dengan database MySQL lokal Anda.
3. Jalankan perintah:
   ```bash
   mvn spring-boot:run
   ```
4. Aplikasi akan berjalan di `http://localhost:8080`..