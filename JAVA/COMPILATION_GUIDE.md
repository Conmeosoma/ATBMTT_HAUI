# 🔨 Hướng Dẫn Biên Dịch & Chạy - Compilation & Execution Guide

## 📋 Mục Lục

1. [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
2. [Cách chạy nhanh nhất](#cách-chạy-nhanh-nhất)
3. [Biên dịch thủ công](#biên-dịch-thủ-công)
4. [Khắc phục sự cố](#khắc-phục-sự-cố)
5. [Tùy chỉnh nâng cao](#tùy-chỉnh-nâng-cao)

---

## 🖥️ Yêu Cầu Hệ Thống

### **Bắt buộc**

- ✅ **Java Development Kit (JDK) 8 hoặc mới hơn**
  - Không phải JRE (Java Runtime Environment)
  - Cần javac (Java compiler)

### **Khuyến cáo**

- ✅ **RAM**: 512 MB (tối thiểu), 1 GB (khuyến cáo)
- ✅ **Disk**: 100 MB trống
- ✅ **Màn hình**: 1024x768 (tối thiểu)

### **Kiểm tra Java đã cài đặt chưa**

#### **Windows (Command Prompt / PowerShell)**

```cmd
java -version
javac -version
```

#### **Linux/Mac (Terminal)**

```bash
java -version
javac -version
```

**Kết quả mong đợi:**

```
java version "1.8.0_xxx" or "11.x.x" or "17.x.x" or higher
javac 1.8.0_xxx or 11.x.x or 17.x.x or higher
```

---

## 🚀 Cách Chạy Nhanh Nhất

### **1️⃣ Windows - Double-click**

```
1. Mở File Explorer
2. Điều hướng tới: C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin
3. Double-click file: run.bat
4. Chuỗi dòng lệnh sẽ hiển thị, biên dịch, và chạy ứng dụng
```

### **2️⃣ Windows - Command Prompt**

```cmd
# Mở Command Prompt (cmd.exe)
# Hoặc PowerShell

# Điều hướng tới thư mục dự án
cd "C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin"

# Chạy script batch
run.bat

# Hoặc biên dịch & chạy thủ công:
javac SchorrSignatureParams.java SchorrKeyPair.java SchorrSignature.java SchorrSignatureAlgorithm.java SchorrGUI.java SchorrMain.java
java SchorrMain
```

### **3️⃣ Windows - PowerShell (Hiện đại)**

```powershell
# Mở PowerShell

# Điều hướng
Set-Location "C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin"

# Chạy
.\run.bat

# Hoặc thủ công
javac SchorrSignatureParams.java, SchorrKeyPair.java, SchorrSignature.java, SchorrSignatureAlgorithm.java, SchorrGUI.java, SchorrMain.java
java SchorrMain
```

### **4️⃣ Linux/Mac - Terminal**

```bash
# Mở Terminal

# Điều hướng tới thư mục
cd "path/to/An Toàn Bảo Mật Thông Tin"

# Phương pháp 1: Sử dụng shell script
chmod +x run.sh
./run.sh

# Phương pháp 2: Biên dịch thủ công
javac SchorrSignatureParams.java SchorrKeyPair.java SchorrSignature.java SchorrSignatureAlgorithm.java SchorrGUI.java SchorrMain.java
java SchorrMain
```

---

## 🔨 Biên Dịch Thủ Công (Chi Tiết)

### **Bước 1: Kiểm tra Java**

```bash
java -version
javac -version
```

### **Bước 2: Đi tới thư mục dự án**

#### **Windows**

```cmd
cd "C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin"
```

#### **Linux/Mac**

```bash
cd "/path/to/An Toàn Bảo Mật Thông Tin"
```

### **Bước 3: Biên dịch từng file theo thứ tự**

#### **Cách 1: Biên dịch riêng lẻ (từng file)**

```bash
javac SchorrSignatureParams.java
javac SchorrKeyPair.java
javac SchorrSignature.java
javac SchorrSignatureAlgorithm.java
javac SchorrGUI.java
javac SchorrMain.java
```

#### **Cách 2: Biên dịch toàn bộ (một lệnh)**

**Windows (PowerShell hoặc Command Prompt):**

```cmd
javac SchorrSignatureParams.java SchorrKeyPair.java SchorrSignature.java SchorrSignatureAlgorithm.java SchorrGUI.java SchorrMain.java
```

**Linux/Mac (Bash):**

```bash
javac SchorrSignatureParams.java SchorrKeyPair.java SchorrSignature.java SchorrSignatureAlgorithm.java SchorrGUI.java SchorrMain.java
```

### **Bước 4: Kiểm tra các file .class được tạo**

```bash
# Windows (cmd)
dir *.class

# Linux/Mac (bash)
ls -la *.class
```

**Kết quả mong đợi:**

```
SchorrMain.class
SchorrGUI.class
SchorrSignatureAlgorithm.class
SchorrSignature.class
SchorrKeyPair.class
SchorrSignatureParams.class
```

### **Bước 5: Chạy ứng dụng**

```bash
java SchorrMain
```

---

## 🐛 Khắc Phục Sự Cố

### **❌ Lỗi: "javac: command not found" hoặc "javac is not recognized"**

**Nguyên nhân**: JDK chưa được cài đặt hoặc PATH chưa được cấu hình

**Giải pháp**:

1. Cài đặt Java Development Kit (JDK) từ:
   - https://www.oracle.com/java/technologies/downloads/
   - Chọn phiên bản phù hợp (Windows, Linux, Mac)
2. Sau khi cài đặt, khởi động lại terminal

3. Xác minh cài đặt:
   ```bash
   javac -version
   ```

### **❌ Lỗi: "SchorrSignatureParams.class not found"**

**Nguyên nhân**: Chạy từ thư mục sai hoặc biên dịch chưa hoàn thành

**Giải pháp**:

1. Kiểm tra đường dẫn hiện tại:

   ```bash
   # Windows
   cd

   # Linux/Mac
   pwd
   ```

2. Đảm bảo ở thư mục chứa các file .java

3. Kiểm tra các file .class:

   ```bash
   # Windows
   dir *.class

   # Linux/Mac
   ls -la *.class
   ```

4. Nếu không có file .class, biên dịch lại:
   ```bash
   javac SchorrSignatureParams.java SchorrKeyPair.java SchorrSignature.java SchorrSignatureAlgorithm.java SchorrGUI.java SchorrMain.java
   ```

### **❌ Lỗi: "Exception in thread 'main' java.lang.ClassNotFoundException"**

**Nguyên nhân**: Một hoặc nhiều lớp Java bị thiếu hoặc không được biên dịch

**Giải pháp**:

1. Xóa tất cả file .class:

   ```bash
   # Windows
   del *.class

   # Linux/Mac
   rm -f *.class
   ```

2. Biên dịch lại tất cả:

   ```bash
   javac SchorrSignatureParams.java SchorrKeyPair.java SchorrSignature.java SchorrSignatureAlgorithm.java SchorrGUI.java SchorrMain.java
   ```

3. Kiểm tra lỗi biên dịch:
   ```bash
   # Nếu có lỗi, javac sẽ in chi tiết
   ```

### **❌ Lỗi: "The system cannot find the path specified"**

**Nguyên nhân**: Đường dẫn tệp chứa ký tự đặc biệt hoặc khoảng trắng không được xử lý đúng

**Giải pháp**:

1. Đảm bảo thư mục được quoted nếu có khoảng trắng:

   ```cmd
   cd "C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin"
   ```

2. Hoặc tạo đường dẫn ngắn (Windows):
   ```cmd
   cd C:\PROGRA~1\...  (hoặc sử dụng 8.3 filename format)
   ```

### **❌ Lỗi: Cửa sổ GUI không mở**

**Nguyên nhân**: Không có giao diện đồ họa (Headless environment)

**Giải pháp**:

1. Kiểm tra xem có desktop environment không:
   - Windows: ✓ Luôn có
   - Linux: Cần X11 hoặc Wayland
   - Mac: ✓ Luôn có

2. Nếu chạy qua SSH, sử dụng X11 forwarding:
   ```bash
   ssh -X user@host
   java SchorrMain
   ```

### **❌ Lỗi: "Insufficient memory" hoặc OutOfMemoryError**

**Nguyên nhân**: Heap size quá nhỏ

**Giải pháp**: Tăng heap size

```bash
java -Xmx512m SchorrMain    # 512 MB
java -Xmx1024m SchorrMain   # 1 GB
```

### **❌ Biên dịch chậm (lâu > 30 giây)**

**Nguyên nhân**: Phát sinh số nguyên tố (256-bit) mất thời gian

**Giải pháp**: Bình thường - không phải lỗi

- Lần đầu chạy có thể mất 10-30 giây
- Phát sinh khóa là phép toán nặng

---

## ⚙️ Tùy Chỉnh Nâng Cao

### **1. Thay đổi kích thước khóa**

Sửa file `SchorrSignatureParams.java`, dòng ~24:

```java
// Hiện tại:
BigInteger q = BigInteger.probablePrime(256, random);

// Để tăng lên 512 bit (an toàn hơn):
BigInteger q = BigInteger.probablePrime(512, random);
```

**Lưu ý**: Biên dịch sẽ chậm hơn

### **2. Thay đổi Look & Feel**

Sửa file `SchorrMain.java`, phương thức `configureUI()`:

```java
// Hiện tại: Sử dụng Look & Feel hệ thống
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

// Để sử dụng Nimbus (hiện đại):
UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

// Để sử dụng Metal (cổ điển):
UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
```

### **3. Thay đổi kích thước cửa sổ**

Sửa file `SchorrGUI.java`, dòng ~42:

```java
// Hiện tại:
setSize(1000, 700);

// Để tăng kích thước:
setSize(1200, 800);

// Để fullscreen:
setExtendedState(JFrame.MAXIMIZED_BOTH);
```

### **4. Thay đổi thông điệp mặc định**

Sửa file `SchorrGUI.java`, dòng ~87:

```java
// Hiện tại:
messageInput.setText("Xin chào từ HAUI - An toàn bảo mật thông tin");

// Đổi thành:
messageInput.setText("Thông điệp của bạn ở đây");
```

### **5. Tối ưu hóa hiệu suất**

Chạy với JIT compiler tối ưu:

```bash
java -XX:+AggressiveOpts -XX:+UseG1GC SchorrMain
```

---

## 📊 Troubleshooting Checklist

Nếu ứng dụng không hoạt động:

- [ ] Java version >= 8?
  ```bash
  java -version
  ```
- [ ] javac (compiler) có sẵn?
  ```bash
  javac -version
  ```
- [ ] Đang ở thư mục đúng?

  ```bash
  # Windows
  cd

  # Linux/Mac
  pwd
  ```

- [ ] Tất cả 6 file .java có sẵn?

  ```bash
  # Windows
  dir *.java

  # Linux/Mac
  ls -la *.java
  ```

- [ ] Các file .class được tạo?

  ```bash
  # Windows
  dir *.class

  # Linux/Mac
  ls -la *.class
  ```

- [ ] Xóa .class và biên dịch lại?

  ```bash
  # Windows
  del *.class

  # Linux/Mac
  rm -f *.class

  # Rồi biên dịch lại
  javac *.java
  ```

---

## 🎓 Ví Dụ Hoàn Chỉnh

### **Ví dụ 1: Windows (Command Prompt)**

```cmd
C:\Users\Nguyen Nam Tien\Desktop\CK> cd "An Toàn Bảo  Mật Thông Tin"
C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin> javac *.java
C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin> java SchorrMain

[GUI mở ra]
```

### **Ví dụ 2: Linux/Mac (Terminal)**

```bash
$ cd ~/Desktop/CK/An\ Toàn\ Bảo\ Mật\ Thông\ Tin
$ javac *.java
$ java SchorrMain

[GUI mở ra]
```

### **Ví dụ 3: Windows (PowerShell)**

```powershell
PS C:\Users\Nguyen Nam Tien\Desktop\CK> Set-Location "An Toàn Bảo  Mật Thông Tin"
PS C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin> javac *.java
PS C:\Users\Nguyen Nam Tien\Desktop\CK\An Toàn Bảo  Mật Thông Tin> java SchorrMain

[GUI mở ra]
```

---

## ✉️ Cần Giúp Đỡ?

1. Đọc lại hướng dẫn này
2. Kiểm tra `SCHNORR_README.md`
3. Đọc console output để tìm lỗi
4. Liên hệ giáo viên hướng dẫn

---

**Chúc bạn biên dịch & chạy thành công! 🎉**
