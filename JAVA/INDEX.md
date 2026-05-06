# 📚 Chỉ Mục Dự Án - Project Index

## 🗂️ Danh Sách Tất Cả Các File

### **🔥 FILE CHÍNH (Entry Point)**

- **[SchorrMain.java](SchorrMain.java)**
  - ⭐ File chính để chạy ứng dụng
  - Kiểm tra yêu cầu hệ thống
  - Khởi chạy GUI
  - ~300 dòng code

---

### **💾 File Java (Core Logic)**

| #        | File                                                           | Mô Tả                      | Dòng Code |
| -------- | -------------------------------------------------------------- | -------------------------- | --------- |
| 1        | [SchorrSignatureParams.java](SchorrSignatureParams.java)       | Tham số hệ thống (p, q, g) | ~70       |
| 2        | [SchorrKeyPair.java](SchorrKeyPair.java)                       | Cặp khóa (private, public) | ~50       |
| 3        | [SchorrSignature.java](SchorrSignature.java)                   | Chữ ký (s, e)              | ~50       |
| 4        | [SchorrSignatureAlgorithm.java](SchorrSignatureAlgorithm.java) | **Thuật toán 3 giai đoạn** | ~280      |
| 5        | [SchorrGUI.java](SchorrGUI.java)                               | Giao diện Swing            | ~600      |
| **TỔNG** | **6 files**                                                    | **~1,250 dòng code**       | -         |

---

### **📖 Tài Liệu (Documentation)**

| Tên File                                         | Nội Dung                   | Đọc Khi                      |
| ------------------------------------------------ | -------------------------- | ---------------------------- |
| **[SCHNORR_README.md](SCHNORR_README.md)**       | Tài liệu chi tiết đầy đủ   | Cần hiểu chi tiết thuật toán |
| **[QUICKSTART.md](QUICKSTART.md)**               | Hướng dẫn nhanh            | Muốn chạy ngay lập tức       |
| **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)**     | Tóm tắt dự án              | Cần overview toàn bộ         |
| **[COMPILATION_GUIDE.md](COMPILATION_GUIDE.md)** | Hướng dẫn biên dịch & chạy | Gặp lỗi biên dịch            |
| **[INDEX.md](INDEX.md)**                         | Chỉ mục này                | Cần tìm file nào đó          |

---

### **🚀 Scripts Chạy (Automation)**

| Tên File           | Hệ Điều Hành | Cách Dùng                      |
| ------------------ | ------------ | ------------------------------ |
| [run.bat](run.bat) | Windows      | Double-click hoặc `run.bat`    |
| [run.sh](run.sh)   | Linux/Mac    | `chmod +x run.sh` → `./run.sh` |

---

## 🎯 Lộ Trình Học Tập (Learning Path)

### **1️⃣ Bắt Đầu (5 phút)**

1. Đọc [QUICKSTART.md](QUICKSTART.md)
2. Chạy ứng dụng: `run.bat` hoặc `./run.sh`
3. Thử các nút bấm

### **2️⃣ Hiểu Cơ Bản (30 phút)**

1. Đọc [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)
2. Xem kiến trúc dự án
3. Đọc comment trong `SchorrMain.java`

### **3️⃣ Hiểu Thuật Toán (2 giờ)**

1. Đọc [SCHNORR_README.md](SCHNORR_README.md) - phần "Giai đoạn 1, 2, 3"
2. Đọc `SchorrSignatureAlgorithm.java` - phần comment
3. Xem các công thức toán học

### **4️⃣ Hiểu Sâu Mã (1-2 giờ)**

1. Đọc từng file Java:
   - [SchorrSignatureParams.java](SchorrSignatureParams.java) - Tham số
   - [SchorrKeyPair.java](SchorrKeyPair.java) - Khóa
   - [SchorrSignature.java](SchorrSignature.java) - Chữ ký
   - [SchorrSignatureAlgorithm.java](SchorrSignatureAlgorithm.java) - Thuật toán
   - [SchorrGUI.java](SchorrGUI.java) - Giao diện

### **5️⃣ Thực Hành (1 giờ)**

1. Thay đổi code (ví dụ: tăng kích thước khóa)
2. Biên dịch lại
3. Chạy và thử kết quả

---

## 🔍 Tìm Kiếm Nhanh

### **Tôi cần...**

#### **...chạy ứng dụng**

→ [QUICKSTART.md](QUICKSTART.md) hoặc [run.bat](run.bat) / [run.sh](run.sh)

#### **...biên dịch & sửa lỗi**

→ [COMPILATION_GUIDE.md](COMPILATION_GUIDE.md)

#### **...hiểu toàn bộ dự án**

→ [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

#### **...hiểu chi tiết thuật toán**

→ [SCHNORR_README.md](SCHNORR_README.md)

#### **...hiểu mã nguồn**

→ Đọc các file `.java` theo thứ tự:

1. [SchorrMain.java](SchorrMain.java)
2. [SchorrSignatureParams.java](SchorrSignatureParams.java)
3. [SchorrKeyPair.java](SchorrKeyPair.java)
4. [SchorrSignature.java](SchorrSignature.java)
5. [SchorrSignatureAlgorithm.java](SchorrSignatureAlgorithm.java)
6. [SchorrGUI.java](SchorrGUI.java)

#### **...sửa giao diện**

→ [SchorrGUI.java](SchorrGUI.java) (search: "Font", "Color", "Button")

#### **...tăng độ an toàn**

→ [SchorrSignatureParams.java](SchorrSignatureParams.java) (line ~24)

#### **...khắc phục sự cố**

→ [COMPILATION_GUIDE.md](COMPILATION_GUIDE.md) - Troubleshooting section

---

## 📊 Thống Kê Dự Án

| Chỉ số             | Giá trị         |
| ------------------ | --------------- |
| Tổng file          | 13 files        |
| File Java          | 6 files         |
| Tài liệu           | 5 files         |
| Scripts            | 2 files         |
| **Tổng dòng code** | **~1,250 dòng** |
| **Tổng tài liệu**  | **~3,000 dòng** |
| **TỔNG CỘNG**      | **~4,250 dòng** |

---

## 🗺️ Cấu Trúc Thư Mục

```
An Toàn Bảo Mật Thông Tin/
│
├── 📄 CODE CHÍNH
│   ├── SchorrMain.java ⭐ ENTRY POINT
│   ├── SchorrSignatureParams.java
│   ├── SchorrKeyPair.java
│   ├── SchorrSignature.java
│   ├── SchorrSignatureAlgorithm.java
│   └── SchorrGUI.java
│
├── 📖 TÀI LIỆU HƯỚNG DẪN
│   ├── SCHNORR_README.md (Chi tiết)
│   ├── QUICKSTART.md (Nhanh)
│   ├── PROJECT_SUMMARY.md (Tổng quan)
│   ├── COMPILATION_GUIDE.md (Biên dịch)
│   └── INDEX.md (Chỉ mục này)
│
├── 🚀 SCRIPTS TỰ ĐỘNG
│   ├── run.bat (Windows)
│   └── run.sh (Linux/Mac)
│
└── 📁 KHÁC
    ├── .git/ (Git repository)
    ├── README.md (File readme gốc)
    └── test.java (Test file)
```

---

## 🔐 Mô Tả Tóm Tắt Các Lớp

### **SchorrMain.java** 🌟

- Entry point chính
- Kiểm tra yêu cầu hệ thống
- Khởi chạy GUI
- Error handling

### **SchorrSignatureParams.java**

- Tham số hệ thống (p, q, g)
- Tạo số nguyên tố lớn
- Phát sinh phần tử sinh

### **SchorrKeyPair.java**

- Cặp khóa (x, y)
- Getters
- Utility methods

### **SchorrSignature.java**

- Chữ ký (s, e)
- Getters
- Display method

### **SchorrSignatureAlgorithm.java** 🔑

- **GIAI ĐOẠN 1**: generateKeys()
- **GIAI ĐOẠN 2**: sign()
- **GIAI ĐOẠN 3**: verify()
- hashSHA256()

### **SchorrGUI.java** 🎨

- Giao diện Swing
- Các button handlers
- Display components
- Event listeners

---

## ⏱️ Thời Gian Dự Kiến

| Hoạt động           | Thời Gian   |
| ------------------- | ----------- |
| Đọc QUICKSTART      | 5 phút      |
| Chạy ứng dụng       | 2 phút      |
| Đọc PROJECT_SUMMARY | 20 phút     |
| Đọc SCHNORR_README  | 1 giờ       |
| Hiểu mã nguồn       | 1-2 giờ     |
| **TỔNG CỘNG**       | **3-4 giờ** |

---

## 🎓 Các Khái Niệm Chính

### **Toán Học**

- Discrete Logarithm Problem (DLP)
- Modular Arithmetic
- Prime Numbers
- Cryptographic Hash Function

### **Lập Trình**

- Object-Oriented Programming (OOP)
- Java Swing GUI
- BigInteger Arithmetic
- Exception Handling

### **Bảo Mật**

- Key Generation
- Digital Signature
- Hash Function (SHA-256)
- Verification

---

## 📚 Tài Liệu Khuyến Cáo

### **Đọc trước (Prerequisites)**

- Kiến thức cơ bản Java
- Hiểu về GUI
- Kiến thức cơ bản mật mã

### **Đọc thêm (Extra)**

- Schnorr, C. P. (1989)
- RFC 8032
- FIPS 186-4
- Wikipedia: Discrete logarithm

---

## 💬 Hỏi Đáp Nhanh (FAQ)

### **Q: Tôi bắt đầu từ đâu?**

A: Đọc [QUICKSTART.md](QUICKSTART.md), sau đó chạy `run.bat` hoặc `./run.sh`

### **Q: Ứng dụng dùng để làm gì?**

A: Học và thực hành Schnorr Digital Signature Algorithm

### **Q: Có thể dùng cho production không?**

A: Không, đây là ứng dụng giáo dục

### **Q: Biên dịch lỗi?**

A: Xem [COMPILATION_GUIDE.md](COMPILATION_GUIDE.md)

### **Q: Không hiểu mã?**

A: Đọc comment trong code, rồi đọc [SCHNORR_README.md](SCHNORR_README.md)

### **Q: Tôi muốn sửa code?**

A: Thay đổi file `.java`, biên dịch lại, chạy lại

---

## ✅ Checklist Hoàn Thành

- [ ] Tải/tìm tất cả 6 file Java
- [ ] Kiểm tra Java 8+ được cài
- [ ] Chạy ứng dụng (run.bat hoặc run.sh)
- [ ] Thử Generate Keys
- [ ] Thử Sign Message
- [ ] Thử Verify Signature
- [ ] Đọc PROJECT_SUMMARY.md
- [ ] Đọc SCHNORR_README.md
- [ ] Hiểu các giai đoạn 1, 2, 3
- [ ] Hiểu công thức toán học

---

## 🔗 Liên Kết Nhanh

| Tài Liệu                                     | Loại                | Độ Dài      |
| -------------------------------------------- | ------------------- | ----------- |
| [QUICKSTART.md](QUICKSTART.md)               | Hướng dẫn nhanh     | ~200 dòng   |
| [SCHNORR_README.md](SCHNORR_README.md)       | Tài liệu chi tiết   | ~3,000 dòng |
| [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)     | Tóm tắt dự án       | ~800 dòng   |
| [COMPILATION_GUIDE.md](COMPILATION_GUIDE.md) | Hướng dẫn biên dịch | ~600 dòng   |
| [INDEX.md](INDEX.md)                         | Chỉ mục (file này)  | ~400 dòng   |

---

## 🎯 Mục Đích Dự Án

✅ **Giáo dục**: Học Schnorr Signature Algorithm  
✅ **Thực hành**: Triển khai trong Java  
✅ **GUI**: Học Java Swing  
✅ **Cryptography**: Hiểu DLP & Digital Signatures

---

## ⚠️ Lưu Ý Quan Trọng

> **ĐÂY LÀ ỨNG DỤNG GIÁO DỤC**
>
> - Không dùng cho bảo vệ dữ liệu thật
> - Số nguyên tố quá nhỏ (256-512 bit)
> - Không có bảo vệ side-channel attacks
> - Chỉ dùng để học tập

---

## 📞 Liên Hệ & Hỗ Trợ

- 📧 Liên hệ giáo viên hướng dẫn
- 📖 Đọc tài liệu chi tiết
- 🐛 Kiểm tra Troubleshooting section

---

**Happy Learning! 🎓**

**Tài liệu cập nhật lần cuối: 2026-05-06**
