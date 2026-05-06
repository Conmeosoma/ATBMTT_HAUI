# 🚀 Hướng dẫn Nhanh - Quick Start Guide

## Cách chạy ứng dụng Schnorr Digital Signature

### **Trên Windows:**

#### **Cách 1: Sử dụng File batch (Dễ nhất)**

1. Mở thư mục chứa các file Java
2. Double-click file `run.bat`
3. Cửa sổ ứng dụng sẽ mở ra tự động

#### **Cách 2: Sử dụng Command Prompt**

```cmd
# Mở Command Prompt (cmd) ở thư mục chứa các file Java
cd "C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin"

# Biên dịch
javac SchorrSignatureParams.java
javac SchorrKeyPair.java
javac SchorrSignature.java
javac SchorrSignatureAlgorithm.java
javac SchorrGUI.java

# Chạy
java SchorrGUI
```

#### **Cách 3: Sử dụng PowerShell**

```powershell
# Mở PowerShell ở thư mục chứa các file
Set-Location "C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin"

# Biên dịch
javac SchorrSignatureParams.java
javac SchorrKeyPair.java
javac SchorrSignature.java
javac SchorrSignatureAlgorithm.java
javac SchorrGUI.java

# Chạy
java SchorrGUI
```

---

### **Trên Linux/Mac:**

#### **Cách 1: Sử dụng Shell Script (Dễ nhất)**

```bash
cd "path/to/An Toàn Bảo Mật Thông Tin"
chmod +x run.sh
./run.sh
```

#### **Cách 2: Biên dịch và chạy thủ công**

```bash
cd "path/to/An Toàn Bảo Mật Thông Tin"

# Biên dịch
javac SchorrSignatureParams.java
javac SchorrKeyPair.java
javac SchorrSignature.java
javac SchorrSignatureAlgorithm.java
javac SchorrGUI.java

# Chạy
java SchorrGUI
```

---

## 📌 Các tệp cần thiết

Để chạy ứng dụng, bạn cần có 5 file Java này:

1. ✅ `SchorrSignatureParams.java` - Tham số hệ thống
2. ✅ `SchorrKeyPair.java` - Cặp khóa
3. ✅ `SchorrSignature.java` - Chữ ký
4. ✅ `SchorrSignatureAlgorithm.java` - Thuật toán chính
5. ✅ `SchorrGUI.java` - Giao diện Swing

---

## 🎯 Sử dụng ứng dụng trong 3 bước

### **Bước 1️⃣: Phát sinh khóa**

```
1. Nhấp nút "Generate Keys"
2. Chờ một chút (phát sinh số nguyên tố)
3. Xem khóa bí mật và khóa công khai
```

### **Bước 2️⃣: Ký thông điệp**

```
1. Nhập thông điệp (hoặc giữ nguyên giá trị mặc định)
2. Nhấp nút "Sign Message"
3. Xem chữ ký (s, e)
```

### **Bước 3️⃣: Xác minh chữ ký**

```
1. Nhấp nút "Verify Signature"
2. Xem kết quả:
   - Xanh (✓) = Chữ ký hợp lệ
   - Đỏ (✗) = Chữ ký không hợp lệ
```

---

## ⚙️ Yêu cầu hệ thống

- ✅ **Java 8** hoặc mới hơn
- ✅ **512 MB RAM** (tối thiểu)
- ✅ **1 GB RAM** (khuyến cáo)
- ✅ Màn hình có độ phân giải **1024x768** (tối thiểu)

### **Kiểm tra phiên bản Java:**

```bash
java -version
javac -version
```

---

## 🐛 Khắc phục sự cố

### **Lỗi: "java: command not found"**

- **Nguyên nhân**: Java chưa được cài đặt hoặc không trong PATH
- **Giải pháp**: Cài đặt Java Development Kit (JDK) từ [oracle.com](https://www.oracle.com/java/technologies/downloads/)

### **Lỗi: "javac: command not found"**

- **Nguyên nhân**: JDK chưa được cài đặt (chỉ có JRE)
- **Giải pháp**: Cài đặt JDK (không phải JRE)

### **Lỗi: "Exception in thread 'main'"**

- **Nguyên nhân**: Lỗi biên dịch hoặc chạy
- **Giải pháp**: Đảm bảo tất cả 5 file Java đều tồn tại và được biên dịch

### **Lỗi: File class không tìm thấy**

- **Nguyên nhân**: Chạy từ thư mục sai
- **Giải pháp**: Đảm bảo bạn đang ở thư mục chứa tất cả file .class

### **Giao diện hiển thị chậm/bị lỗi**

- **Nguyên nhân**: Hệ thống thiếu tài nguyên
- **Giải pháp**: Đóng các chương trình khác, tăng heap size:
  ```bash
  java -Xmx512m SchorrGUI
  ```

---

## 🎓 Tài liệu liên quan

- 📄 `SCHNORR_README.md` - Tài liệu chi tiết đầy đủ
- 📋 `README.md` - Tệp readme gốc

---

## 💡 Mẹo sử dụng

1. **Thay đổi thông điệp** sau khi ký để thử xem chữ ký trở thành không hợp lệ
2. **Phát sinh khóa mới** để so sánh chữ ký từ các khóa khác nhau
3. **Sao chép** các khóa để lưu lại (từ trình duyệt hoặc app khác)
4. **Xem chi tiết** từng phần của hash và số tính toán trong console

---

## ✉️ Liên hệ

Nếu gặp vấn đề, vui lòng:

- Kiểm tra các yêu cầu hệ thống
- Đọc lại `SCHNORR_README.md`
- Liên hệ giáo viên hướng dẫn

---

**Chúc bạn sử dụng thành công! 🎉**
