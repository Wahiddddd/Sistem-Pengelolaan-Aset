# Sistem Pengelolaan Aset (Asset Management System)

Sistem Pengelolaan Aset adalah aplikasi berbasis web yang dirancang untuk membantu perusahaan dalam mengelola siklus hidup aset, mulai dari pendataan, monitoring status, hingga pencatatan log pemeliharaan secara otomatis dan terintegrasi.

## Analisa Kebutuhan Sistem
Sistem ini dikembangkan untuk menyelesaikan masalah inventarisasi manual yang rentan terhadap kesalahan data dan keterlambatan pemeliharaan.

### Kebutuhan Fungsional:
- **Manajemen User**: Sistem mendukung multi-role (Super Admin, Admin, dan Teknisi) dengan keamanan login (Auto-lock account setelah 5x gagal percobaan).

- **Manajemen Aset**: Admin dapat menambah, mengedit, dan mencari aset berdasarkan nomor seri unik.

- **Otomatisasi Maintenance**: Sistem secara otomatis menghitung tanggal servis berikutnya berdasarkan frekuensi pemeliharaan yang diinput.

- **Monitoring Teknisi**: Teknisi dapat mengubah status aset (In Maintenance/Broken) dan mengunggah bukti foto sebelum serta sesudah perbaikan.

- **Pencatatan Log**: Setiap tindakan perbaikan tercatat di tabel log beserta biaya dan durasi pengerjaannya.

## Flow Business (Alur Bisnis)
### 1. Proses Registrasi dan Login
Sistem menggunakan keamanan berbasis NIK dan password, serta fitur penguncian akun otomatis jika gagal login lebih dari 5 kali.
![Flowchart Login](flow_chart/Flow%20Chart(Register%20dan%20Login).drawio.png)

### 2. Manajemen Inventaris (Admin)
Admin dapat menambahkan aset baru, di mana sistem akan otomatis menghitung tanggal servis berikutnya berdasarkan frekuensi pemeliharaan.
![Flowchart Input Aset](flow_chart/Flow%20Chart(Admin%20(Input%20Aset)).drawio.png)

### 3. Monitoring & Update Aset (Admin)
Fitur untuk mencari aset berdasarkan nomor seri dan melakukan pembaruan data.
![Flowchart List Aset](flow_chart/Flow%20Chart(Admin(list%20aset)).drawio.png)

### 4. Alur Kerja Pemeliharaan (Teknisi)
Teknisi memproses aset yang membutuhkan servis, mencatat log pengerjaan, dan mengunggah bukti foto.
![Flowchart Teknisi](flow_chart/Flow%20Chart(Teknisi).drawio.png)

---

## 🗄️ Desain Database

Struktur database dirancang untuk mendukung relasi antara aset, kategori, dan log pemeliharaan.

### Skema Database
![Desain Database](flow_chart/Desain%20Database.drawio.png)

### Detail Relasi:
* **Users**: Menyimpan kredensial, role (Admin/Teknisi), dan status keamanan akun.
* **Assets**: Berelasi dengan categories dan mencatat detail spesifik aset serta jadwal servis otomatis.
* **Maintenance Logs**: Mencatat riwayat perbaikan yang terhubung ke setiap aset.

### Deskripsi Relasi Antar Tabel:
* **Categories 1 : N Assets**: Categories (1) : (N) Assets: Satu kategori dapat menampung berbagai aset, namun satu aset hanya terikat pada satu kategori melalui category_id.
* **Assets 1 : N Maintenance_logs**: Setiap aset memiliki hubungan historis dengan banyak catatan log perbaikan yang terhubung melalui asset_id.
* **Users (1) : (N) Maintenance_Logs**: Satu user (Teknisi) dapat bertanggung jawab atas banyak aktivitas pemeliharaan yang dicatat melalui user_id.
* **Role Management (Logic-Based)**: Pengelolaan hak akses antara Admin dan Teknisi dilakukan melalui logika aplikasi (RBAC) berdasarkan kolom role pada tabel Users, memungkinkan skalabilitas fitur tanpa kompleksitas tabel tambahan.

## Schema Synchronization
Sinkronisasi otomatis antara Java Entity dan Database menggunakan Hibernate Auto-DDL.

---

## 🚀 Update Terbaru (API Endpoints Tersedia)

Sistem sekarang sudah dilengkapi dengan REST API Controllers untuk pengujian menggunakan Postman. Security (JWT) untuk sementara di-set *permit all* agar mempermudah testing.

### 1. User API (/api/users)
- POST / : Create User
- GET / : Get All Users (Pagination)
- GET /{id} : Get User by ID
- PUT /{id} : Update User
- DELETE /{id} : Delete User

### 2. Category API (/api/categories)
- POST / : Create Category
- GET / : Get All Categories
- GET /{id} : Get Category by ID
- PUT /{id} : Update Category
- DELETE /{id} : Delete Category

### 3. Asset API (/api/assets)
- POST / : Create Asset
- GET / : Get All Assets (Pagination)
- GET /{id} : Get Asset by ID
- GET /due-maintenance : Get Assets Due for Maintenance
- PUT /{id} : Update Asset
- DELETE /{id} : Delete Asset

### 4. Maintenance Log API (/api/maintenance)
- POST /start/{assetId}/technician/{technicianId} : Start Maintenance
- POST /finish/{assetId} : Finish Maintenance
- POST /broken/{assetId} : Mark Asset as Broken
- GET /asset/{assetId} : Get Maintenance History by Asset