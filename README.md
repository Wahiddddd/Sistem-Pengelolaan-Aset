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
* **Assets**: Berelasi dengan `categories` dan mencatat detail spesifik aset serta jadwal servis otomatis.
* **Maintenance Logs**: Mencatat riwayat perbaikan yang terhubung ke setiap aset.

### Deskripsi Relasi Antar Tabel:
* **Categories 1 : N Assets**: Satu kategori (misal: Elektronik) dapat menampung banyak aset, namun satu aset hanya memiliki satu kategori.
* **Assets 1 : N Maintenance_logs**: Satu aset dapat memiliki riwayat pemeliharaan yang panjang seiring berjalannya waktu.
* **Users N : M Roles (via Logic)**: Pengelolaan hak akses yang membedakan fitur Admin dan Teknisi.

## Schema Synchronization
Sinkronisasi otomatis antara Java Entity dan Database menggunakan Hibernate Auto-DDL.